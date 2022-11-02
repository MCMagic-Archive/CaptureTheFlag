package us.mcmagic.capturetheflag.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.handlers.GameTeam;
import us.mcmagic.mcmagiccore.actionbar.ActionBarManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 12/31/14
 */
public class CaptureMessageThread {
    public static HashMap<String, Integer> list = new HashMap<>();
    private static List<GameTeam> message = new ArrayList<>();
    private static boolean b = false;

    public static void start() {
        list.put("main", Bukkit.getScheduler().runTaskTimer(CaptureTheFlag.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (message.isEmpty()) {
                    return;
                }
                if (b) {
                    for (GameTeam team : new ArrayList<>(message)) {
                        for (UUID uuid : CaptureTheFlag.gameUtil.getTeamMembers(team)) {
                            ActionBarManager.sendMessage(Bukkit.getPlayer(uuid), ChatColor.RED + "" + ChatColor.BOLD +
                                    "Your Flag has been captured!");
                        }
                    }
                } else {
                    for (GameTeam team : new ArrayList<>(message)) {
                        for (UUID uuid : CaptureTheFlag.gameUtil.getTeamMembers(team)) {
                            ActionBarManager.sendMessage(Bukkit.getPlayer(uuid), ChatColor.YELLOW + "" + ChatColor.BOLD +
                                    "Your Flag has been captured!");
                        }
                    }
                }
                b = !b;
            }
        }, 0L, 20L).getTaskId());
    }

    public static void addTeam(GameTeam team) {
        if (!message.contains(team)) {
            message.add(team);
        }
    }

    public static void stop() {
        Bukkit.getScheduler().cancelTask(list.get("main"));
    }

    public static void removeTeam(GameTeam team) {
        message.remove(team);
    }
}