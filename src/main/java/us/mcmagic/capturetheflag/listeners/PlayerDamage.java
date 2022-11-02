package us.mcmagic.capturetheflag.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.handlers.BannerPlace;
import us.mcmagic.capturetheflag.handlers.GameState;
import us.mcmagic.capturetheflag.handlers.GameTeam;
import us.mcmagic.capturetheflag.handlers.PlayerData;
import us.mcmagic.capturetheflag.utils.GameUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.arcade.GameDamageByPlayerEvent;
import us.mcmagic.mcmagiccore.arcade.ctf.CTFKit;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;
import us.mcmagic.mcmagiccore.title.TitleObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 12/30/14
 */
public class PlayerDamage implements Listener {
    private static List<UUID> spectators = new ArrayList<>();
    public static HashMap<UUID, Double> damageMultiplier = new HashMap<>();

    public static boolean isSpectator(UUID uuid) {
        return spectators.contains(uuid);
    }

    @EventHandler
    public void onAchievementGive(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        //Bukkit.broadcastMessage(event.getEntity().getName() + " " + event.getDamage() + " " + event.getFinalDamage() +
        //        " " + event.getCause().name());
        if (GameState.isState(GameState.IN_LOBBY) || GameState.isState(GameState.POSTGAME)) {
            event.setCancelled(true);
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).equals(GameTeam.SPECTATOR)) {
            event.setCancelled(true);
            return;
        }
        if (damageMultiplier.containsKey(player.getUniqueId())) {
            event.setDamage(event.getDamage() * damageMultiplier.get(player.getUniqueId()));
        }
        if (player.getHealth() - event.getFinalDamage() > 0) {
            event.setCancelled(false);
            return;
        }
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            MCMagicCore.gameManager.broadcast(CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).getColor() +
                    player.getName() + ChatColor.GREEN + " was killed!");
        }
        event.setCancelled(true);
        die(player, false);
    }

    protected static void die(Player player, boolean leaving) {
        if (player.hasMetadata("hasflag")) {
            if (player.getMetadata("hasflag").get(0).asBoolean()) {
                GameTeam team = CaptureTheFlag.gameUtil.getTeam(player.getUniqueId());
                BannerPlace flag = BannerPlace.fromString(player.getMetadata("flag").get(0).asString());
                CaptureTheFlag.gameUtil.setDroppedFlag(player, team, flag, player.getLocation());
            }
        }
        PlayerData data = CaptureTheFlag.getPlayerData(player.getUniqueId());
        data.setDeaths(data.getDeaths() + 1);
        CaptureTheFlag.gameUtil.dropItems(player);
        ParticleUtil.spawnParticle(ParticleEffect.RED_DUST, player.getLocation().clone().add(0, 1, 0), .35f, .5f,
                .35f, 0, 200);
        if (!leaving) {
            PlayerInventory inv = player.getInventory();
            inv.clear();
            ItemStack air = new ItemStack(Material.AIR);
            inv.setHelmet(air);
            inv.setChestplate(air);
            inv.setLeggings(air);
            inv.setBoots(air);
            player.setMetadata("hasflag", new FixedMetadataValue(CaptureTheFlag.getInstance(), false));
            player.getWorld().playSound(player.getLocation(), Sound.BLAZE_DEATH, 0.5f, 0.5f);
            setSpectator(player);
            player.playSound(player.getLocation(), Sound.HURT_FLESH, 5f, 0f);
            player.setHealth(40.0);
            player.setFoodLevel(20);
            CaptureTheFlag.scoreboardUtil.updateDeaths(player);
        }
    }

    private static void setSpectator(final Player player) {
        spectators.add(player.getUniqueId());
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(player.getLocation().clone().add(0, 0.2, 0));
        player.setAllowFlight(true);
        player.setFlying(true);
        for (Player tp : Bukkit.getOnlinePlayers()) {
            Scoreboard sb = tp.getScoreboard();
            sb.getTeam("spectator").addEntry(player.getName());
            tp.hidePlayer(player);
        }
        TitleObject title = new TitleObject(ChatColor.RED + "You were killed!", ChatColor.YELLOW + "Respawning in " +
                ChatColor.AQUA + "10 " + ChatColor.YELLOW + "seconds").setFadeIn(10).setStay(60).setFadeOut(10);
        title.send(player);
        Bukkit.getScheduler().runTaskLater(CaptureTheFlag.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (Bukkit.getPlayer(player.getUniqueId()) == null) {
                    return;
                }
                player.setAllowFlight(false);
                player.setFlying(false);
                player.setGameMode(GameMode.SURVIVAL);
                CaptureTheFlag.gameUtil.teleportToSpawn(player.getUniqueId());
                PlayerData data = CaptureTheFlag.getPlayerData(player.getUniqueId());
                CTFKit kit = data.getKit();
                kit.setItems(player, data.get(kit, 2));
                spectators.remove(player.getUniqueId());
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 3, true, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, 1, true, false));
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    Scoreboard sb = tp.getScoreboard();
                    sb.getTeam(CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).name().toLowerCase()).addEntry(player.getName());
                    tp.showPlayer(player);
                }
            }
        }, 200L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }
        if (GameState.isState(GameState.PREPARING)) {
            event.setCancelled(true);
            return;
        }
        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        if (spectators.contains(damager.getUniqueId()) || spectators.contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        if (CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).equals(GameTeam.SPECTATOR) ||
                CaptureTheFlag.gameUtil.getTeam(damager.getUniqueId()).equals(GameTeam.SPECTATOR)) {
            event.setCancelled(true);
            return;
        }
        if (player.getHealth() - event.getFinalDamage() <= 0) {
            if (event.getDamager() instanceof Player) {
                PlayerData damdata = CaptureTheFlag.getPlayerData(damager.getUniqueId());
                damdata.setKills(damdata.getKills() + 1);
                MCMagicCore.gameManager.broadcast(CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).getColor() +
                        player.getName() + ChatColor.GREEN + " was killed by " +
                        CaptureTheFlag.gameUtil.getTeam(damager.getUniqueId()).getColor() + damager.getName());
                CaptureTheFlag.scoreboardUtil.updateKills(damager);
                GameUtil.addMoney(damdata, 1);
            } else {
                MCMagicCore.gameManager.broadcast(CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).getColor() +
                        player.getName() + ChatColor.GREEN + " was killed!");
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameDamage(GameDamageByPlayerEvent event) {
        Player player = event.getPlayer();
        Player damager = event.getDamager();
        if (CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).equals(GameTeam.SPECTATOR)) {
            event.setCancelled(true);
            return;
        }
        if (spectators.contains(damager.getUniqueId()) || spectators.contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        if (player.getHealth() - event.getDamageReduced() <= 0) {
            event.setCancelled(true);
            if (event.getDamager() instanceof Player) {
                PlayerData damdata = CaptureTheFlag.getPlayerData(damager.getUniqueId());
                MCMagicCore.gameManager.broadcast(CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).getColor() +
                        player.getName() + ChatColor.GREEN + " was killed by " +
                        CaptureTheFlag.gameUtil.getTeam(damager.getUniqueId()).getColor() + damager.getName());
                damdata.setKills(damdata.getKills() + 1);
                CaptureTheFlag.scoreboardUtil.updateKills(player);
            } else {
                MCMagicCore.gameManager.broadcast(CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).getColor() +
                        player.getName() + ChatColor.GREEN + " was killed!");
            }
            die(player, false);
        }
    }
}