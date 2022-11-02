package us.mcmagic.capturetheflag.utils;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 12/31/14
 */
public class RegionUtil {

    public static List<String> regionsForLocation(Location location) {
        List<String> rlist = new ArrayList<>();
        ApplicableRegionSet list = WorldGuardPlugin.inst().getRegionManager(location.getWorld()).getApplicableRegions(location);
        for (ProtectedRegion region : list) {
            rlist.add(region.getId());
        }
        return rlist;
    }

    public static boolean regionIsInFort(Location location) {
        ApplicableRegionSet list = WorldGuardPlugin.inst().getRegionManager(location.getWorld()).getApplicableRegions(location);
        for (ProtectedRegion region : list) {
            if (region.getId().toLowerCase().contains("fort")) {
                return true;
            }
        }
        return false;
    }
}
