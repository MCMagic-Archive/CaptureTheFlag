package us.mcmagic.capturetheflag.abilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.arcade.GameDamageByPlayerEvent;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 8/15/15
 */
public class HadesAbility extends SpecialAbility {

    @Override
    public void runAbility(Player player, int level, List<UUID> ignore) {
        NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.DOWN);
        format.setMaximumFractionDigits(2);
        double mult = 1.4285714286;
        double damage = 5 + (5 * ((mult * level) / 10));
        ParticleUtil.spawnParticle(ParticleEffect.FLAME, player.getLocation().clone().add(0, 1.25, 0), 0, 0, 0, 0.4f, 400);
        ParticleUtil.spawnParticle(ParticleEffect.LAVA, player.getLocation().clone().add(0, 1.25, 0), 0.5f, 0.5f, 0.5f, 1, 50);
        player.getWorld().playSound(player.getLocation(), Sound.WITHER_DEATH, 5f, 2f);
        Location loc = player.getLocation();
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(tp.getUniqueId()) || ignore.contains(tp.getUniqueId()) || tp.getAllowFlight()) {
                continue;
            }
            if (tp.getLocation().distance(loc) <= 5) {
                GameDamageByPlayerEvent event = new GameDamageByPlayerEvent(tp, player, damage);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    tp.damage(event.getDamageReduced());
                    player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.RED + "Flame Burst " + ChatColor.GREEN +
                            "dealt " + ChatColor.YELLOW + format.format(event.getDamageReduced()) + " damage " +
                            ChatColor.GREEN + "to " + tp.getName());
                    tp.sendMessage(ChatColor.GREEN + player.getName() + "'s " + ChatColor.RED + "Flame Burst " +
                            ChatColor.GREEN + "dealt " + ChatColor.YELLOW + format.format(event.getDamageReduced()) +
                            " damage " + ChatColor.GREEN + "to you!");
                }
            }
        }
    }

    @Override
    public long getCooldown() {
        return 60;
    }
}