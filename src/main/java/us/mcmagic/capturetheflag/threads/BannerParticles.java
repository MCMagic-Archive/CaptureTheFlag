package us.mcmagic.capturetheflag.threads;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.handlers.BannerPlace;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Marc on 12/30/14
 */
public class BannerParticles {
    public static HashMap<String, Integer> list = new HashMap<>();

    public static void start() {
        list.put("main", Bukkit.getScheduler().runTaskTimer(CaptureTheFlag.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        for (Map.Entry<BannerPlace, Location> entry : new HashSet<>(CaptureTheFlag.gameUtil.bannerLocs.entrySet())) {
                            final Location value = entry.getValue();
                            if (!value.getBlock().getType().equals(Material.STANDING_BANNER)) {
                                continue;
                            }
                            Location loc = new Location(CaptureTheFlag.gameWorld, value.getX() + 0.5, value.getY() + 1.5,
                                    value.getZ() + 0.5);
                            ParticleUtil.spawnParticle(ParticleEffect.ENCHANTMENT_TABLE, loc, (float) 0.1, (float) 0.1,
                                    (float) 0.1, (float) 1.37, 25);
                        }
                    }
                }, 0L, 10L).getTaskId()
        );
    }

    public static void stop() {
        Integer taskID = list.remove("main");
        if (taskID != null) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }
}
