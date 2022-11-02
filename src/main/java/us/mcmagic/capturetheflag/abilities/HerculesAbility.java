package us.mcmagic.capturetheflag.abilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.mcmagic.capturetheflag.listeners.PlayerDamage;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 8/15/15
 */
public class HerculesAbility extends SpecialAbility {
    private long stop;
    private Integer taskID;

    @Override
    public void runAbility(final Player player, int level, List<UUID> ignore) {
        double mult = 1;
        double length = 1;
        switch (level) {
            case 1:
                mult = 1.25;
                length = 3;
                break;
            case 2:
                mult = 1.5;
                length = 3;
                break;
            case 3:
                mult = 1.75;
                length = 3.5;
                break;
            case 4:
                mult = 2;
                length = 4;
                break;
            case 5:
                mult = 2.25;
                length = 4;
                break;
            case 6:
                mult = 2.5;
                length = 4.5;
                break;
            case 7:
                mult = 3;
                length = 5;
                break;
        }
        player.sendMessage(ChatColor.GREEN + "You activated your " + ChatColor.RED + "Damage Multiplier!");
        stop = (long) ((System.currentTimeMillis()) + (length * 1000));
        taskID = Bukkit.getScheduler().runTaskTimer(MCMagicCore.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() >= stop) {
                    stop();
                    PlayerDamage.damageMultiplier.remove(player.getUniqueId());
                    return;
                }
                ParticleUtil.spawnParticle(ParticleEffect.LAVA, player.getLocation().clone().add(0, 1, 0), 0.2f, 0.5f,
                        0.2f, 0, 10);
            }
        }, 0L, 10L).getTaskId();
    }

    private void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

    @Override
    public long getCooldown() {
        return 60;
    }
}