package us.mcmagic.capturetheflag.abilities;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.arcade.GameDamageByPlayerEvent;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 8/15/15
 */
public class BoltAbility extends SpecialAbility {
    public static final DirectionFacing[] radial = {DirectionFacing.SOUTH, DirectionFacing.SOUTHWEST,
            DirectionFacing.WEST, DirectionFacing.NORTHWEST, DirectionFacing.NORTH, DirectionFacing.NORTHEAST,
            DirectionFacing.EAST, DirectionFacing.SOUTHEAST};
    private Integer taskID;
    private Integer taskID2;
    private List<UUID> damaged = new ArrayList<>();

    @Override
    public void runAbility(final Player player, int level, final List<UUID> ignore) {
        double damage = 0;
        switch (level) {
            case 1:
                damage = 2;
                break;
            case 2:
                damage = 3.85;
                break;
            case 3:
                damage = 5.7;
                break;
            case 4:
                damage = 7.55;
                break;
            case 5:
                damage = 9.4;
                break;
            case 6:
                damage = 12.18;
                break;
            case 7:
                damage = 15;
                break;
        }
        final double finalDamage = damage;
        taskID = Bukkit.getScheduler().runTaskTimer(MCMagicCore.getInstance(), new Runnable() {
            int i = 0;

            @Override
            public void run() {
                switch (i) {
                    case 0:
                        player.getWorld().playSound(player.getLocation(), Sound.WOLF_GROWL, 3f, 1f);
                        break;
                    case 6:
                        superBark(player, ignore, finalDamage);
                        player.getWorld().playSound(player.getLocation(), Sound.WOLF_BARK, 3f, 1f);
                        break;
                    case 7:
                        player.getWorld().playSound(player.getLocation(), Sound.WOLF_BARK, 3f, 1f);
                        player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 3f, 1f);
                        break;
                    case 8:
                        player.getWorld().playSound(player.getLocation(), Sound.WOLF_BARK, 3f, 1f);
                        break;
                    case 9:
                        player.getWorld().playSound(player.getLocation(), Sound.WOLF_BARK, 3f, 1f);
                        break;
                    case 10:
                        stop();
                }
                i++;
            }
        }, 0L, 2L).getTaskId();
    }

    private void superBark(final Player player, final List<UUID> ignore, final double damage) {
        final NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.DOWN);
        format.setMaximumFractionDigits(2);
        final DirectionFacing facing = getDirectionFacing(player);
        final Location loc = player.getLocation().clone();
        taskID2 = Bukkit.getScheduler().runTaskTimer(MCMagicCore.getInstance(), new Runnable() {
            int i = 0;

            @Override
            public void run() {
                if (i > 0) {
                    nextLoc(loc, facing);
                }
                switch (i) {
                    case 0:
                        launchBlock(loc.clone().add(0, -1, 0));
                        break;
                    case 1:
                        for (Player tp : Bukkit.getOnlinePlayers()) {
                            if (ignore.contains(tp.getUniqueId())) {
                                continue;
                            }
                            if (!damaged.contains(tp.getUniqueId()) && tp.getLocation().distance(loc) <= 3) {
                                GameDamageByPlayerEvent event = new GameDamageByPlayerEvent(tp, player, damage);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    tp.damage(event.getDamageReduced());
                                    player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.BLUE + "Super Bark " +
                                            ChatColor.GREEN + "dealt " + ChatColor.YELLOW +
                                            format.format(event.getDamageReduced()) + " damage " + ChatColor.GREEN +
                                            "to " + tp.getName());
                                    tp.sendMessage(ChatColor.GREEN + player.getName() + "'s " + ChatColor.BLUE +
                                            "Super Bark " + ChatColor.GREEN + "dealt " + ChatColor.YELLOW +
                                            format.format(event.getDamageReduced()) + " damage " + ChatColor.GREEN +
                                            "to you!");
                                    damaged.add(tp.getUniqueId());
                                }
                            }
                        }
                        launchBlock(loc.clone().add(0, -1, 0));
                        switch (facing) {
                            case NORTH:
                            case SOUTH:
                                launchBlock(loc.clone().add(-1, -1, 0));
                                launchBlock(loc.clone().add(1, -1, 0));
                                break;
                            case EAST:
                            case WEST:
                                launchBlock(loc.clone().add(0, -1, -1));
                                launchBlock(loc.clone().add(0, -1, 1));
                                break;
                            case NORTHEAST:
                                launchBlock(loc.clone().add(-1, -1, 0));
                                launchBlock(loc.clone().add(0, -1, 1));
                            case SOUTHWEST:
                                launchBlock(loc.clone().add(1, -1, 0));
                                launchBlock(loc.clone().add(0, -1, -1));
                                break;
                            case NORTHWEST:
                                launchBlock(loc.clone().add(1, -1, 0));
                                launchBlock(loc.clone().add(0, -1, 1));
                            case SOUTHEAST:
                                launchBlock(loc.clone().add(-1, -1, 0));
                                launchBlock(loc.clone().add(0, -1, -1));
                                break;
                        }
                        break;
                    case 2:
                        for (Player tp : Bukkit.getOnlinePlayers()) {
                            if (ignore.contains(tp.getUniqueId())) {
                                continue;
                            }
                            if (!damaged.contains(tp.getUniqueId()) && tp.getLocation().distance(loc) <= 3) {
                                GameDamageByPlayerEvent event = new GameDamageByPlayerEvent(tp, player, damage);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    tp.damage(event.getDamageReduced());
                                    player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.BLUE + "Super Bark " +
                                            ChatColor.GREEN + "dealt " + ChatColor.YELLOW +
                                            format.format(event.getDamageReduced()) + " damage " + ChatColor.GREEN +
                                            "to " + tp.getName());
                                    tp.sendMessage(ChatColor.GREEN + player.getName() + "'s " + ChatColor.BLUE +
                                            "Super Bark " + ChatColor.GREEN + "dealt " + ChatColor.YELLOW +
                                            format.format(event.getDamageReduced()) + " damage " + ChatColor.GREEN +
                                            "to you!");
                                    damaged.add(tp.getUniqueId());
                                }
                            }
                        }
                        launchBlock(loc.clone().add(0, -1, 0));
                        switch (facing) {
                            case NORTH:
                            case SOUTH:
                                launchBlock(loc.clone().add(-1, -1, 0));
                                launchBlock(loc.clone().add(1, -1, 0));
                                launchBlock(loc.clone().add(-2, -1, 0));
                                launchBlock(loc.clone().add(2, -1, 0));
                                break;
                            case EAST:
                            case WEST:
                                launchBlock(loc.clone().add(0, -1, -1));
                                launchBlock(loc.clone().add(0, -1, 1));
                                launchBlock(loc.clone().add(0, -1, -2));
                                launchBlock(loc.clone().add(0, -1, 2));
                                break;
                            case NORTHEAST:
                                launchBlock(loc.clone().add(-1, -1, 0));
                                launchBlock(loc.clone().add(0, -1, 1));
                                launchBlock(loc.clone().add(-2, -1, 0));
                                launchBlock(loc.clone().add(0, -1, 2));
                                break;
                            case SOUTHWEST:
                                launchBlock(loc.clone().add(1, -1, 0));
                                launchBlock(loc.clone().add(0, -1, -1));
                                launchBlock(loc.clone().add(2, -1, 0));
                                launchBlock(loc.clone().add(0, -1, -2));
                                break;
                            case NORTHWEST:
                                launchBlock(loc.clone().add(1, -1, 0));
                                launchBlock(loc.clone().add(0, -1, 1));
                                launchBlock(loc.clone().add(2, -1, 0));
                                launchBlock(loc.clone().add(0, -1, 2));
                                break;
                            case SOUTHEAST:
                                launchBlock(loc.clone().add(-1, -1, 0));
                                launchBlock(loc.clone().add(0, -1, -1));
                                launchBlock(loc.clone().add(-2, -1, 0));
                                launchBlock(loc.clone().add(0, -1, -2));
                                break;
                        }
                        break;
                    case 3:
                        for (Player tp : Bukkit.getOnlinePlayers()) {
                            if (ignore.contains(tp.getUniqueId())) {
                                continue;
                            }
                            if (!damaged.contains(tp.getUniqueId()) && tp.getLocation().distance(loc) <= 3) {
                                GameDamageByPlayerEvent event = new GameDamageByPlayerEvent(tp, player, damage);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    tp.damage(event.getDamageReduced());
                                    player.sendMessage(ChatColor.GREEN + "Your " + ChatColor.BLUE + "Super Bark " +
                                            ChatColor.GREEN + "dealt " + ChatColor.YELLOW +
                                            format.format(event.getDamageReduced()) + " damage " + ChatColor.GREEN +
                                            "to " + tp.getName());
                                    tp.sendMessage(ChatColor.GREEN + player.getName() + "'s " + ChatColor.BLUE +
                                            "Super Bark " + ChatColor.GREEN + "dealt " + ChatColor.YELLOW +
                                            format.format(event.getDamageReduced()) + " damage " + ChatColor.GREEN +
                                            "to you!");
                                    damaged.add(tp.getUniqueId());
                                }
                            }
                        }
                        launchBlock(loc.clone().add(0, -1, 0));
                        switch (facing) {
                            case NORTH:
                            case SOUTH:
                                launchBlock(loc.clone().add(-1, -1, 0));
                                launchBlock(loc.clone().add(1, -1, 0));
                                launchBlock(loc.clone().add(-2, -1, 0));
                                launchBlock(loc.clone().add(2, -1, 0));
                                launchBlock(loc.clone().add(-3, -1, 0));
                                launchBlock(loc.clone().add(3, -1, 0));
                                break;
                            case EAST:
                            case WEST:
                                launchBlock(loc.clone().add(0, -1, -1));
                                launchBlock(loc.clone().add(0, -1, 1));
                                launchBlock(loc.clone().add(0, -1, -2));
                                launchBlock(loc.clone().add(0, -1, 2));
                                launchBlock(loc.clone().add(0, -1, -3));
                                launchBlock(loc.clone().add(0, -1, 3));
                                break;
                            case NORTHEAST:
                                launchBlock(loc.clone().add(-1, -1, 0));
                                launchBlock(loc.clone().add(0, -1, 1));
                                launchBlock(loc.clone().add(-2, -1, 0));
                                launchBlock(loc.clone().add(0, -1, 2));
                                launchBlock(loc.clone().add(-3, -1, 0));
                                launchBlock(loc.clone().add(0, -1, 3));
                                break;
                            case SOUTHWEST:
                                launchBlock(loc.clone().add(1, -1, 0));
                                launchBlock(loc.clone().add(0, -1, -1));
                                launchBlock(loc.clone().add(2, -1, 0));
                                launchBlock(loc.clone().add(0, -1, -2));
                                launchBlock(loc.clone().add(3, -1, 0));
                                launchBlock(loc.clone().add(0, -1, -3));
                                break;
                            case NORTHWEST:
                                launchBlock(loc.clone().add(1, -1, 0));
                                launchBlock(loc.clone().add(0, -1, 1));
                                launchBlock(loc.clone().add(2, -1, 0));
                                launchBlock(loc.clone().add(0, -1, 2));
                                launchBlock(loc.clone().add(3, -1, 0));
                                launchBlock(loc.clone().add(0, -1, 3));
                                break;
                            case SOUTHEAST:
                                launchBlock(loc.clone().add(-1, -1, 0));
                                launchBlock(loc.clone().add(0, -1, -1));
                                launchBlock(loc.clone().add(-2, -1, 0));
                                launchBlock(loc.clone().add(0, -1, -2));
                                launchBlock(loc.clone().add(-3, -1, 0));
                                launchBlock(loc.clone().add(0, -1, -3));
                                break;
                        }
                        break;
                    case 4:
                        stop2();
                        break;
                }
                i++;
            }
        }, 0L, 2L).getTaskId();
    }

    private void nextLoc(Location loc, DirectionFacing facing) {
        switch (facing) {
            case NORTH:
                loc.add(0, 0, -1);
                break;
            case NORTHEAST:
                loc.add(1, 0, -1);
                break;
            case EAST:
                loc.add(1, 0, 0);
                break;
            case SOUTHEAST:
                loc.add(1, 0, 1);
                break;
            case SOUTH:
                loc.add(0, 0, 1);
                break;
            case SOUTHWEST:
                loc.add(-1, 0, 1);
                break;
            case WEST:
                loc.add(-1, 0, 0);
                break;
            case NORTHWEST:
                loc.add(-1, 0, -1);
                break;
        }
    }

    @SuppressWarnings("deprecation")
    private void launchBlock(Location loc) {
        Block b = loc.getBlock();
        if (b.getType().equals(Material.GOLD_BLOCK) || b.getType().name().toLowerCase().contains("banner") ||
                b.getType().equals(Material.BEDROCK) || b.getType().equals(Material.AIR)) {
            return;
        }
        if (b.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
            return;
        }
        FallingBlock fb = loc.getWorld().spawnFallingBlock(b.getLocation(), b.getTypeId(), b.getData());
        fb.setDropItem(false);
        fb.setVelocity(new Vector(0, 0.6, 0));
        fb.setTicksLived(3);
        b.setType(Material.AIR);
    }

    private void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

    private void stop2() {
        Bukkit.getScheduler().cancelTask(taskID2);
    }

    private DirectionFacing getDirectionFacing(Player player) {
        return radial[Math.round(player.getLocation().getYaw() / 45f) & 0x7];
    }

    @Override
    public long getCooldown() {
        return 60;
    }

    private enum DirectionFacing {
        NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST
    }
}