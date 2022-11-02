package us.mcmagic.capturetheflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * Created by Marc on 10/2/15
 */
public class ChunkUnload implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }
}