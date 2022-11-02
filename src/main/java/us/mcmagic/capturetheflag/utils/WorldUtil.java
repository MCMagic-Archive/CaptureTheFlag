package us.mcmagic.capturetheflag.utils;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Marc on 1/1/15
 */
public class WorldUtil {

    public static void replaceBlocks(Location min, Location max, Material replace, Material with) {
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Block blk = min.getWorld().getBlockAt(new Location(min.getWorld(), x, y, z));
                    if (blk.getType() == replace) {
                        blk.setType(with);
                    }
                }
            }
        }
    }

    public static void loadMap(Location min, Location max) {
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                Chunk ch = new Location(min.getWorld(), x, 10, z).getChunk();
                if (ch.isLoaded()) {
                    continue;
                }
                ch.load();
            }
        }
    }
}