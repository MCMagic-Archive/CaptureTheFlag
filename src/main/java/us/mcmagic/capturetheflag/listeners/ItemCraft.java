package us.mcmagic.capturetheflag.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Created by Marc on 9/25/15
 */
public class ItemCraft implements Listener {

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        Material itemType = event.getRecipe().getResult().getType();
        if (itemType.name().toLowerCase().contains("bucket")) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            for (HumanEntity he : new ArrayList<>(event.getViewers())) {
                if (he instanceof Player) {
                    he.closeInventory();
                    he.sendMessage(ChatColor.RED + "You cannot craft a bucket!");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }
}