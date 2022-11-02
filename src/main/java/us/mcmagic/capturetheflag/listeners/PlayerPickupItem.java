package us.mcmagic.capturetheflag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.handlers.BannerPlace;
import us.mcmagic.capturetheflag.handlers.GameTeam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 1/2/15
 */
public class PlayerPickupItem implements Listener {
    public static HashMap<String, Location> list = new HashMap<>();
    public static List<UUID> cooldown = new ArrayList<>();

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        if (CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).equals(GameTeam.SPECTATOR)) {
            event.setCancelled(true);
            return;
        }
        if (PlayerDamage.isSpectator(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        ItemStack stack = event.getItem().getItemStack();
        if (stack.getType().equals(Material.BANNER)) {
            if (event.getItem().hasMetadata("banner")) {
                String teamname = event.getItem().getMetadata("banner").get(0).asString();
                event.setCancelled(true);
                if (player.hasMetadata("hasflag")) {
                    if (player.getMetadata("hasflag").get(0).asBoolean()) {
                        if (!cooldown.contains(player.getUniqueId())) {
                            player.sendMessage(ChatColor.RED + "You already have a Flag, bring it back to your Fort!");
                            cooldown.add(player.getUniqueId());
                            Bukkit.getScheduler().runTaskLater(CaptureTheFlag.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    cooldown.remove(player.getUniqueId());
                                }
                            }, 20L);
                        }
                        return;
                    }
                }
                event.getItem().remove();
                Location loc = list.get(teamname);
                if (teamname.equalsIgnoreCase("center")) {
                    CaptureTheFlag.gameUtil.grabFlag(player, BannerPlace.CENTER, true);
                } else {
                    CaptureTheFlag.gameUtil.grabFlag(player, BannerPlace.fromString(teamname), true);
                }
            }
        }
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        event.setCancelled(event.getEntity().hasMetadata("banner"));
    }
}
