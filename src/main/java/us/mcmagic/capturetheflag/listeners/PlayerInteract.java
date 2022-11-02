package us.mcmagic.capturetheflag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.abilities.SpecialAbility;
import us.mcmagic.capturetheflag.handlers.BannerPlace;
import us.mcmagic.capturetheflag.handlers.GameState;
import us.mcmagic.capturetheflag.handlers.GameTeam;
import us.mcmagic.capturetheflag.handlers.PlayerData;
import us.mcmagic.mcmagiccore.arcade.ctf.CTFKit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 12/30/14
 */
public class PlayerInteract implements Listener {
    private List<UUID> cooldown = new ArrayList<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (GameState.isState(GameState.IN_LOBBY)) {
            if (player.getItemInHand().getType().equals(Material.BED)) {
                player.performCommand("hub");
            }
            event.setCancelled(true);
            return;
        }
        if (PlayerDamage.isSpectator(player.getUniqueId()) || CaptureTheFlag.gameUtil.getTeam(player.getUniqueId())
                .equals(GameTeam.SPECTATOR)) {
            event.setCancelled(true);
            return;
        }
        Block b = event.getClickedBlock();
        if (GameState.isState(GameState.PREPARING)) {
            if (b != null) {
                if (b.getType().equals(Material.FURNACE)) {
                    UUID uuid = BlockEdit.isProtected(b.getLocation());
                    if (uuid != null && !uuid.equals(player.getUniqueId())) {
                        player.sendMessage(ChatColor.GREEN + "You cannot use " + CaptureTheFlag.cache.get(uuid) +
                                "'s Furnace!");
                        event.setCancelled(true);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        return;
                    }
                }
            }
        }
        if (!GameState.isState(GameState.SEARCHING)) {
            return;
        }
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (b.getType().equals(Material.STANDING_BANNER)) {
                Banner banner = (Banner) b.getState();
                BannerPlace place = CaptureTheFlag.gameUtil.placeFromBanner(banner);
                if (CaptureTheFlag.gameUtil.grabFlag(player, place, false)) {
                    b.setType(Material.AIR);
                    return;
                }
            }
        }
        String n = player.getItemInHand().getType().name().toLowerCase();
        if (player.getItemInHand() != null && !cooldown.contains(player.getUniqueId())) {
            if (((n.contains("sword") || n.contains(" axe")) && event.getAction().name().toLowerCase().contains("right"))
                    || (player.getItemInHand().getType().equals(Material.BOW) &&
                    event.getAction().name().toLowerCase().contains("left"))) {
                playAbility(player);
            }
        }
    }

    private void playAbility(final Player player) {
        if (cooldown.contains(player.getUniqueId())) {
            return;
        }
        PlayerData data = CaptureTheFlag.getPlayerData(player.getUniqueId());
        CTFKit kit = data.getKit();
        SpecialAbility ability = CaptureTheFlag.gameUtil.getAbility(kit);
        ability.runAbility(player, data.get(kit, 1),
                CaptureTheFlag.gameUtil.getTeamMembers(CaptureTheFlag.gameUtil.getTeam(player.getUniqueId())));
        if (kit.equals(CTFKit.HERCULES)) {
            double mult = 1;
            switch (data.get(kit, 1)) {
                case 1:
                    mult = 1.25;
                    break;
                case 2:
                    mult = 1.5;
                    break;
                case 3:
                    mult = 1.75;
                    break;
                case 4:
                    mult = 2.0;
                    break;
                case 5:
                    mult = 2.25;
                    break;
                case 6:
                    mult = 2.5;
                    break;
                case 7:
                    mult = 3.0;
                    break;
            }
            PlayerDamage.damageMultiplier.put(player.getUniqueId(), mult);
        }
        cooldown.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(CaptureTheFlag.getInstance(), new Runnable() {
            @Override
            public void run() {
                cooldown.remove(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "You can use your Special Ability again!");
            }
        }, ability.getCooldown() * 20);
    }
}