package us.mcmagic.capturetheflag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.handlers.BannerPlace;
import us.mcmagic.capturetheflag.handlers.GameState;
import us.mcmagic.capturetheflag.handlers.GameTeam;
import us.mcmagic.capturetheflag.threads.CaptureMessageThread;
import us.mcmagic.capturetheflag.utils.RegionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 12/30/14
 */
public class PlayerMove implements Listener {
    private List<UUID> cooldown = new ArrayList<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (GameState.isState(GameState.IN_LOBBY)) {
            return;
        }
        final Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (CaptureTheFlag.gameUtil.getTeam(event.getPlayer().getUniqueId()).equals(GameTeam.SPECTATOR)) {
            if (to.getBlockY() <= 0) {
                player.teleport(CaptureTheFlag.gameUtil.getFlagLocation(BannerPlace.CENTER));
            }
            return;
        }
        Location min = CaptureTheFlag.gameUtil.getMapMin().clone().add(-1, -1, -1);
        Location max = CaptureTheFlag.gameUtil.getMapMax().clone().add(1, 1, 1);
        if (to.getX() < min.getX() || to.getX() > max.getX() || to.getZ() < min.getZ() || to.getZ() > max.getZ()) {
            event.setCancelled(true);
            if (!cooldown.contains(player.getUniqueId())) {
                cooldown.add(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "You can't exit the map!");
                Bukkit.getScheduler().runTaskLater(CaptureTheFlag.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        cooldown.remove(player.getUniqueId());
                    }
                }, 20L);
            }
            return;
        }
        if (GameState.isState(GameState.PREPARING)) {
            if ((from.getBlockX() != to.getBlockX()) || (from.getBlockZ() != to.getBlockZ())) {
                if (!RegionUtil.regionsForLocation(to).contains(CaptureTheFlag.gameUtil.getTeam(player.getUniqueId())
                        .toString().toLowerCase())) {
                    event.setCancelled(true);
                    if (!cooldown.contains(player.getUniqueId())) {
                        cooldown.add(player.getUniqueId());
                        player.sendMessage(ChatColor.RED + "You can't go over there!");
                        Bukkit.getScheduler().runTaskLater(CaptureTheFlag.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                cooldown.remove(player.getUniqueId());
                            }
                        }, 20L);
                    }
                }
            }
            return;
        }
        if (GameState.isState(GameState.SEARCHING)) {
            if ((from.getBlockX() != to.getBlockX()) || (from.getBlockY() != to.getBlockY()) || (from.getBlockZ() != to.getBlockZ())) {
                Block b = to.clone().add(0, -1, 0).getBlock();
                if (!b.getType().equals(Material.GOLD_BLOCK)) {
                    return;
                }
                if (RegionUtil.regionIsInFort(b.getLocation())) {
                    if (!RegionUtil.regionsForLocation(event.getTo()).contains(CaptureTheFlag.gameUtil
                            .getTeam(player.getUniqueId()).name().toLowerCase() + "fort")) {
                        if (!cooldown.contains(player.getUniqueId())) {
                            player.sendMessage(ChatColor.RED + "You have to bring Flags back to " +
                                    ChatColor.ITALIC + "your" + ChatColor.RED + " Fort!");
                        }
                        return;
                    }
                    if (player.hasMetadata("hasflag") && player.getMetadata("hasflag").get(0).asBoolean()) {
                        BannerPlace flag = BannerPlace.fromString(player.getMetadata("flag").get(0).asString());
                        player.removeMetadata("hasflag", CaptureTheFlag.getInstance());
                        player.removeMetadata("flag", CaptureTheFlag.getInstance());
                        GameTeam team = CaptureTheFlag.gameUtil.getTeam(player.getUniqueId());
                        CaptureMessageThread.removeTeam(team);
                        CaptureTheFlag.gameUtil.captureFlag(player, flag);
                    } else {
                        if (!cooldown.contains(player.getUniqueId())) {
                            cooldown.add(player.getUniqueId());
                            player.sendMessage(ChatColor.RED + "Capture a Flag and bring it back here to earn points!");
                            Bukkit.getScheduler().runTaskLater(CaptureTheFlag.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    cooldown.remove(player.getUniqueId());
                                }
                            }, 20L);
                        }
                    }
                }
            }
        }
    }
}