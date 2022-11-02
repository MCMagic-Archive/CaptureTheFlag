package us.mcmagic.capturetheflag.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.handlers.GameTeam;

import java.util.HashMap;

/**
 * Created by Marc on 1/2/15
 */
public class SearchClock {
    public static HashMap<String, Integer> list = new HashMap<>();
    public static int i = 1200;

    public static void start() {
        list.put("main", Bukkit.getScheduler().runTaskTimer(CaptureTheFlag.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (i == 0) {
                    CaptureTheFlag.gameUtil.endGame(GameTeam.SPECTATOR, true, ChatColor.AQUA + "Time ran out!");
                } else {
                    for (Player tp : Bukkit.getOnlinePlayers()) {
                        tp.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(ChatColor.RED +
                                PrepareClock.formatTime(i) + ChatColor.BLUE + " " + ChatColor.BOLD + "Capture The Flag");
                    }
                }
                i--;
            }
        }, 0L, 20L).getTaskId());
    }

    public static void stop() {
        Integer taskID = list.remove("main");
        if (taskID != null) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }
}
