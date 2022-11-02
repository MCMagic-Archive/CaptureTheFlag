package us.mcmagic.capturetheflag.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.handlers.GameState;
import us.mcmagic.capturetheflag.handlers.GameTeam;
import us.mcmagic.capturetheflag.handlers.PlayerData;
import us.mcmagic.capturetheflag.handlers.SafeFurnace;
import us.mcmagic.capturetheflag.utils.RegionUtil;
import us.mcmagic.mcmagiccore.arcade.ctf.CTFKit;

import java.util.*;

/**
 * Created by Marc on 12/29/14
 */
public class BlockEdit implements Listener {
    private List<BlockFace> faces = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
            BlockFace.UP, BlockFace.DOWN);

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (GameState.isState(GameState.IN_LOBBY)) {
            event.setCancelled(true);
            return;
        }
        Player player = event.getPlayer();
        if (CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).equals(GameTeam.SPECTATOR)) {
            event.setCancelled(true);
            return;
        }
        Block b = event.getBlock();
        if (GameState.isState(GameState.PREPARING)) {
            if (b.getType().equals(Material.FURNACE)) {
                UUID uuid = isProtected(b.getLocation());
                if (uuid == null) {
                    return;
                }
                if (uuid != null) {
                    if (!uuid.equals(player.getUniqueId())) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You can't break " + CaptureTheFlag.cache.get(uuid) + "'s Furnace!");
                        return;
                    } else {
                        player.sendMessage(ChatColor.GREEN + "You broke your Furnace!");
                        CaptureTheFlag.gameUtil.safeFurnaces.remove(getFurnace(b.getLocation()));
                        return;
                    }
                }
            }
        }
        if (b.getType().equals(Material.DIAMOND_BLOCK)) {
            event.setCancelled(true);
            return;
        }
        if (RegionUtil.regionIsInFort(event.getBlock().getLocation())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Forts can not be changed!");
            return;
        }
        if (isOre(b.getType())) {
            PlayerData data = CaptureTheFlag.getPlayerData(player.getUniqueId());
            if (doAbility(player, data)) {
                activateAbility(player, data, event);
            }
        }
        event.setExpToDrop(0);
        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (GameState.isState(GameState.IN_LOBBY)) {
            event.setCancelled(true);
            return;
        }
        Player player = event.getPlayer();
        if (CaptureTheFlag.gameUtil.getTeam(player.getUniqueId()).equals(GameTeam.SPECTATOR)) {
            event.setCancelled(true);
            return;
        }
        if (RegionUtil.regionIsInFort(event.getBlock().getLocation())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Forts can not be changed!");
            return;
        }
        Block b = event.getBlock();
        if (GameState.isState(GameState.PREPARING)) {
            if (b.getType().equals(Material.FURNACE)) {
                player.sendMessage(ChatColor.GREEN + "Only you can use this Furnace!");
                Location l = b.getLocation();
                CaptureTheFlag.gameUtil.safeFurnaces.add(new SafeFurnace(player.getUniqueId(), l.getBlockX(),
                        l.getBlockY(), l.getBlockZ()));
            }
        }
        event.setCancelled(false);
    }

    private void activateAbility(Player player, PlayerData data, BlockBreakEvent event) {
        CTFKit kit = data.getKit();
        Block b = event.getBlock();
        Material type = b.getType();
        switch (kit) {
            case HADES:
                if (type.equals(Material.IRON_ORE)) {
                    event.setCancelled(true);
                    player.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.IRON_INGOT, 1));
                    player.sendMessage(ChatColor.GREEN + "Your Mining Ability " + ChatColor.YELLOW +
                            "Instantly Smelted " + ChatColor.GREEN + "this " + ChatColor.YELLOW + "Iron Ore!");
                    b.setType(Material.AIR);
                }
                break;
            case BAYMAX:
                if (type.equals(Material.IRON_ORE)) {
                    event.setCancelled(true);
                    player.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.IRON_BLOCK, 1));
                    player.sendMessage(ChatColor.GREEN + "Your Mining Ability got you one " + ChatColor.YELLOW +
                            "Iron Block!");
                    b.setType(Material.AIR);
                }
                break;
            case HERCULES:
                for (BlockFace face : faces) {
                    Block rel = b.getRelative(face);
                    if (rel.getType().equals(Material.AIR)) {
                        continue;
                    }
                    rel.breakNaturally();
                    player.sendMessage(ChatColor.GREEN + "Your Mining Ability broke a second block!");
                    break;
                }
                break;
            case MERIDA:
                player.getWorld().dropItemNaturally(b.getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.ARROW, 1));
                player.sendMessage(ChatColor.GREEN + "Your Mining Ability got you an " + ChatColor.YELLOW +
                        "extra arrow!");
                break;
            case BOLT:
                if (type.equals(Material.COAL_ORE)) {
                    player.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.COAL, 1));
                } else if (type.equals(Material.DIAMOND_ORE)) {
                    player.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.DIAMOND, 1));
                } else {
                    player.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(type, 1));
                }
                player.sendMessage(ChatColor.GREEN + "Your Mining Ability gave you an " + ChatColor.YELLOW +
                        "extra drop!");
                break;
        }
    }

    public static SafeFurnace getFurnace(Location loc) {
        for (SafeFurnace furnace : new ArrayList<>(CaptureTheFlag.gameUtil.safeFurnaces)) {
            if (furnace.getX() == loc.getBlockX() && furnace.getY() == loc.getBlockY() && furnace.getZ() ==
                    loc.getBlockZ()) {
                return furnace;
            }
        }
        return null;
    }

    public static UUID isProtected(Location loc) {
        for (SafeFurnace furnace : new ArrayList<>(CaptureTheFlag.gameUtil.safeFurnaces)) {
            if (furnace.getX() == loc.getBlockX() && furnace.getY() == loc.getBlockY() && furnace.getZ() ==
                    loc.getBlockZ()) {
                return furnace.getUniqueId();
            }
        }
        return null;
    }

    private boolean doAbility(Player player, PlayerData data) {
        CTFKit kit = data.getKit();
        Random r = new Random();
        float f = r.nextFloat();
        float per = getPercent(kit, data.get(kit, 1));
        return f <= per;
    }

    private float getPercent(CTFKit kit, int i) {
        switch (kit) {
            case HADES:
                switch (i) {
                    case 1:
                        return 0.0714f;
                    case 2:
                        return 0.1429f;
                    case 3:
                        return 0.2143f;
                    case 4:
                        return 0.2857f;
                    case 5:
                        return 0.3571f;
                    case 6:
                        return 0.4286f;
                    case 7:
                        return 0.5f;
                }
                break;
            case BAYMAX:
                switch (i) {
                    case 1:
                        return 0.01f;
                    case 2:
                        return 0.025f;
                    case 3:
                        return 0.04f;
                    case 4:
                        return 0.05f;
                    case 5:
                        return 0.065f;
                    case 6:
                        return 0.08f;
                    case 7:
                        return 0.1f;
                }
                break;
            case HERCULES:
                switch (i) {
                    case 1:
                        return 0.0714f;
                    case 2:
                        return 0.1429f;
                    case 3:
                        return 0.2143f;
                    case 4:
                        return 0.2857f;
                    case 5:
                        return 0.3571f;
                    case 6:
                        return 0.4286f;
                    case 7:
                        return 0.5f;
                }
                break;
            case MERIDA:
                switch (i) {
                    case 1:
                        return 0.05f;
                    case 2:
                        return 0.15f;
                    case 3:
                        return 0.2f;
                    case 4:
                        return 0.3f;
                    case 5:
                        return 0.35f;
                    case 6:
                        return 0.4f;
                    case 7:
                        return 0.5f;
                }
                break;
            case BOLT:
                switch (i) {
                    case 1:
                        return 0.1f;
                    case 2:
                        return 0.2f;
                    case 3:
                        return 0.3f;
                    case 4:
                        return 0.4f;
                    case 5:
                        return 0.5f;
                    case 6:
                        return 0.6f;
                    case 7:
                        return 0.75f;
                }
                break;
        }
        return 0;
    }

    private boolean isOre(Material type) {
        return type.name().toLowerCase().contains("ore");
    }
}