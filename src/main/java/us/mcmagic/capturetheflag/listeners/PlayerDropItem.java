package us.mcmagic.capturetheflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import us.mcmagic.capturetheflag.handlers.GameState;

/**
 * Created by Marc on 9/14/15
 */
public class PlayerDropItem implements Listener {

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (GameState.isState(GameState.IN_LOBBY)) {
            event.setCancelled(true);
            return;
        }
        if (PlayerDamage.isSpectator(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}