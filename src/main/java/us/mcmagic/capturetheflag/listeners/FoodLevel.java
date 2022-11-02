package us.mcmagic.capturetheflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import us.mcmagic.capturetheflag.handlers.GameState;

/**
 * Created by Marc on 12/30/14
 */
public class FoodLevel implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!GameState.isState(GameState.SEARCHING)) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntity().getHealth() >= event.getEntity().getMaxHealth()) {
            event.setCancelled(true);
        }
    }
}
