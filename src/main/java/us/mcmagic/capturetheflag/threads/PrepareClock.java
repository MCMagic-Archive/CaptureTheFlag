package us.mcmagic.capturetheflag.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.title.TitleObject;

import java.util.HashMap;

/**
 * Created by Marc on 12/31/14
 */
public class PrepareClock {
    public static HashMap<String, Integer> list = new HashMap<>();
    public static int i = 480;

    public static void start() {
        list.put("main", Bukkit.getScheduler().runTaskTimer(CaptureTheFlag.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (i == 480) {
                    MCMagicCore.gameManager.broadcast("You have 8 Minutes to prepare your resources to Capture The Flags");
                }
                if (i == 420 || i == 360 || i == 300 || i == 240 || i == 180 || i == 120) {
                    TitleObject title = new TitleObject(ChatColor.GREEN + "" + (i / 60) + " Minutes",
                            ChatColor.AQUA + "until the Barriers fall!").setFadeIn(10).setStay(60).setFadeOut(10);
                    for (Player tp : Bukkit.getOnlinePlayers()) {
                        title.send(tp);
                    }
                }
                if (i == 60) {
                    TitleObject title = new TitleObject(ChatColor.GREEN + "" + "1 Minute",
                            ChatColor.AQUA + "until the Barriers fall!");
                    for (Player tp : Bukkit.getOnlinePlayers()) {
                        title.send(tp);
                    }
                }
                if (i == 0) {
                    CaptureTheFlag.gameUtil.removeBarriers();
                    stop();
                    SearchClock.start();
                } else {
                    for (Player tp : Bukkit.getOnlinePlayers()) {
                        tp.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(ChatColor.RED +
                                formatTime(i) + ChatColor.BLUE + " " + ChatColor.BOLD + "Capture The Flag");
                    }
                }
                i--;
            }
        }, 0L, 20L).getTaskId());
    }

    public static String formatTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public static void stop() {
        Integer taskID = list.remove("main");
        if (taskID != null) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }
}
