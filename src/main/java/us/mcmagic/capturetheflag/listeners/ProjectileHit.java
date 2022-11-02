package us.mcmagic.capturetheflag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.handlers.GameTeam;
import us.mcmagic.capturetheflag.handlers.PlayerData;
import us.mcmagic.mcmagiccore.arcade.GameDamageByPlayerEvent;
import us.mcmagic.mcmagiccore.arcade.ctf.CTFKit;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 9/20/15
 */
public class ProjectileHit implements Listener {

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.DOWN);
        format.setMaximumFractionDigits(2);
        if (!event.getEntityType().equals(EntityType.ARROW)) {
            return;
        }
        Arrow arrow = (Arrow) event.getEntity();
        Player player = (Player) arrow.getShooter();
        PlayerData data = CaptureTheFlag.getPlayerData(player.getUniqueId());
        if (arrow.hasMetadata("magicexplode") && arrow.getMetadata("magicexplode").get(0).asBoolean()) {
            if (!data.getKit().equals(CTFKit.MERIDA)) {
                return;
            }
            int level = data.get(CTFKit.MERIDA, 1);
            double radius = 0;
            double damage = 0;
            switch (level) {
                case 1:
                    radius = 2;
                    damage = 3;
                    break;
                case 2:
                    radius = 2;
                    damage = 4;
                    break;
                case 3:
                    radius = 3;
                    damage = 4;
                    break;
                case 4:
                    radius = 3;
                    damage = 6;
                    break;
                case 5:
                    radius = 4;
                    damage = 6;
                    break;
                case 6:
                    radius = 4;
                    damage = 8;
                    break;
                case 7:
                    radius = 5;
                    damage = 10;
                    break;
            }
            final Location loc = arrow.getLocation();
            ParticleUtil.spawnParticle(ParticleEffect.HUGE_EXPLOSION, loc, 0.5f, 0.5f, 0.5f, 0, 2);
            loc.getWorld().playSound(loc, Sound.EXPLODE, 3f, 1f);
            List<UUID> list = CaptureTheFlag.gameUtil.getTeamMembers(CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()));
            for (UUID uuid : CaptureTheFlag.gameUtil.getTeamMembers(GameTeam.SPECTATOR)) {
                list.add(uuid);
            }
            arrow.remove();
            for (Player tp : Bukkit.getOnlinePlayers()) {
                if (list.contains(tp.getUniqueId())) {
                    continue;
                }
                if (tp.getLocation().distance(loc) <= radius) {
                    GameDamageByPlayerEvent e = new GameDamageByPlayerEvent(tp, player, damage);
                    Bukkit.getPluginManager().callEvent(e);
                    if (!e.isCancelled()) {
                        tp.damage(e.getDamageReduced());
                        player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.RED + "Explosive Arrow " +
                                ChatColor.GREEN + "dealt " + ChatColor.YELLOW + format.format(e.getDamageReduced()) +
                                " damage " + ChatColor.GREEN + "to " + tp.getName());
                        tp.sendMessage(ChatColor.GREEN + player.getName() + "'s " + ChatColor.RED + "Explosive Arrow " +
                                ChatColor.GREEN + "dealt " + ChatColor.YELLOW + format.format(e.getDamageReduced()) +
                                " damage " + ChatColor.GREEN + "to you!");
                    }
                }
            }
        }
    }
}