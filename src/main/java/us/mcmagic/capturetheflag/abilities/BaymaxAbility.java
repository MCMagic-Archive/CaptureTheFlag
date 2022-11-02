package us.mcmagic.capturetheflag.abilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 8/15/15
 */
public class BaymaxAbility extends SpecialAbility {

    @Override
    public void runAbility(Player player, int level, List<UUID> ignore) {
        int radius = 0;
        switch (level) {
            case 1:
                radius = 4;
                break;
            case 2:
            case 3:
                radius = 5;
                break;
            case 4:
            case 5:
                radius = 6;
                break;
            case 6:
            case 7:
                radius = 7;
                break;
        }
        int health = 0;
        switch (level) {
            case 1:
            case 2:
                health = 5;
                break;
            case 3:
            case 4:
                health = 6;
                break;
            case 5:
            case 6:
                health = 7;
                break;
            case 7:
                health = 8;
                break;
        }
        Location loc = player.getLocation();
        for (UUID uuid : ignore) {
            Player tp = Bukkit.getPlayer(uuid);
            if (tp == null) {
                continue;
            }
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            if (loc.distance(tp.getLocation()) <= radius) {
                double n = health;
                if (n + tp.getHealth() > 40) {
                    n = (40 - tp.getHealth());
                }
                tp.sendMessage(ChatColor.GREEN + player.getName() + "'s Healing Ability healed you for " +
                        ChatColor.RED + n + "❤");
                player.sendMessage(ChatColor.GREEN + "You healed " + tp.getName() + " for " + ChatColor.RED + n + "❤");
                tp.setHealth(tp.getHealth() + n);
                ParticleUtil.spawnParticle(ParticleEffect.HEART, tp.getLocation().clone().add(0, 1, 0), 0.4f, 0.5f,
                        0.4f, 0, 25);
            }
        }
        player.getWorld().playSound(player.getLocation(), Sound.BLAZE_BREATH, 10f, 2f);
        double n = health;
        if (n + player.getHealth() > 40) {
            n = (40 - player.getHealth());
        }
        player.sendMessage(ChatColor.GREEN + "Your Healing Ability healed you for " + ChatColor.RED + n + "❤");
        player.setHealth(player.getHealth() + n);
    }

    @Override
    public long getCooldown() {
        return 60;
    }
}