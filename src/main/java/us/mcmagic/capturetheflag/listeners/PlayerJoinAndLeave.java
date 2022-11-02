package us.mcmagic.capturetheflag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.handlers.GameState;
import us.mcmagic.capturetheflag.handlers.GameTeam;
import us.mcmagic.capturetheflag.handlers.PlayerData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Marc on 12/29/14
 */
public class PlayerJoinAndLeave implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * from ctf_data WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(ChatColor.RED + "Could not find Kit data for your player!");
                return;
            }
            PlayerData data = new PlayerData(uuid, result.getString("kit"), result.getInt("hades1"), result.getInt("hades2"),
                    result.getInt("hades3"), result.getInt("baymax1"), result.getInt("baymax2"), result.getInt("baymax3"),
                    result.getInt("hercules1"), result.getInt("hercules2"), result.getInt("hercules3"),
                    result.getInt("merida1"), result.getInt("merida2"), result.getInt("merida3"), result.getInt("bolt1"),
                    result.getInt("bolt2"), result.getInt("bolt3"));
            if (data.getKit() == null) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(ChatColor.RED + "Please select a Kit by clicking on the " + ChatColor.GREEN +
                        "Shop Manager!");
                return;
            }
            CaptureTheFlag.login(uuid, data);
        } catch (SQLException e) {
            e.printStackTrace();
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "Could not find Kit data for your player!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user == null) {
            return;
        }
        if (user.getRank().getRankId() >= Rank.SPECIALGUEST.getRankId()) {
            if (GameState.isState(GameState.IN_LOBBY)) {
                if (CaptureTheFlag.gameUtil.getIngame().size() == CaptureTheFlag.gameUtil.getMaxPlayers()) {
                    event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                    event.setKickMessage(ChatColor.RED + "That game is full!");
                }
            }
            return;
        }
        if (!GameState.getState().equals(GameState.IN_LOBBY)) {
            event.setKickMessage(ChatColor.RED + "There is currently a game in progress!");
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        CaptureTheFlag.cache.remove(player.getUniqueId());
        CaptureTheFlag.cache.put(player.getUniqueId(), player.getName());
        player.teleport(CaptureTheFlag.gameUtil.lobby);
        CaptureTheFlag.scoreboardUtil.setupPlayer(player);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[0]);
        Bukkit.getScheduler().runTaskLater(CaptureTheFlag.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    if (tp.getUniqueId().equals(player.getUniqueId())) {
                        continue;
                    }
                    player.hidePlayer(tp);
                }
                Bukkit.getScheduler().runTaskLater(CaptureTheFlag.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        for (Player tp : Bukkit.getOnlinePlayers()) {
                            if (tp.getUniqueId().equals(player.getUniqueId())) {
                                continue;
                            }
                            player.showPlayer(tp);
                        }
                    }
                }, 10L);
            }
        }, 20L);
        player.recalculatePermissions();
        if (GameState.isState(GameState.IN_LOBBY)) {
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().setItem(8, new ItemCreator(Material.BED, ChatColor.GREEN + "Return to Arcade"));
            MCMagicCore.gameManager.setPlayerCount(MCMagicCore.getMCMagicConfig().serverName, Bukkit.getOnlinePlayers().size());
        } else {
            CaptureTheFlag.gameUtil.joinSpectator(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        Player player = event.getPlayer();
        if (!GameState.isState(GameState.POSTGAME) && !GameState.isState(GameState.IN_LOBBY)) {
            if (!CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).equals(GameTeam.SPECTATOR)) {
                MCMagicCore.gameManager.broadcast(CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).getColor() +
                        player.getName() + ChatColor.GREEN + " was killed!");
                PlayerDamage.die(player, true);
            }
            if (CaptureTheFlag.gameUtil.isInGame(player.getUniqueId())) {
                CaptureTheFlag.gameUtil.removePlayer(player.getUniqueId());
            }
        }
        CaptureTheFlag.logout(player.getUniqueId());
        if (GameState.isState(GameState.IN_LOBBY)) {
            MCMagicCore.gameManager.setPlayerCount(MCMagicCore.getMCMagicConfig().serverName,
                    Bukkit.getOnlinePlayers().size() - 1);
        } else {
            MCMagicCore.gameManager.setPlayerCount(MCMagicCore.getMCMagicConfig().serverName,
                    CaptureTheFlag.gameUtil.getCurrentAmount());
        }
    }
}
