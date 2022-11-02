package us.mcmagic.capturetheflag.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.handlers.BannerPlace;
import us.mcmagic.capturetheflag.handlers.Captures;
import us.mcmagic.capturetheflag.handlers.GameTeam;
import us.mcmagic.capturetheflag.handlers.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 12/30/14
 */
public class ScoreboardUtil {
    public ScoreboardManager sbm = Bukkit.getScoreboardManager();
    private List<UUID> spectators = new ArrayList<>();
    public Captures redScore = new Captures(GameTeam.RED);
    public Captures yellowScore = new Captures(GameTeam.YELLOW);
    public Captures greenScore = new Captures(GameTeam.GREEN);
    public Captures blueScore = new Captures(GameTeam.BLUE);

    private void setTeamValues(GameTeam team, Objective obj) {
        Score r = obj.getScore(getString(redScore, team));
        Score y = obj.getScore(getString(yellowScore, team));
        Score g = obj.getScore(getString(greenScore, team));
        Score b = obj.getScore(getString(blueScore, team));
        Score blank3 = obj.getScore("  ");
        Score kills = obj.getScore(ChatColor.GREEN + "Kills: 0");
        Score deaths = obj.getScore(ChatColor.GREEN + "Deaths: 0");
        Score captures = obj.getScore(ChatColor.GREEN + "Captures: 0");
        Score blank2 = obj.getScore(" ");
        Score flags = obj.getScore(ChatColor.GREEN + "" + ChatColor.BOLD + "Flag Locations:");
        Score red = obj.getScore(ChatColor.RED + "Red: " + ChatColor.GREEN + "Fort");
        Score yel = obj.getScore(ChatColor.YELLOW + "Yellow: " + ChatColor.GREEN + "Fort");
        Score gre = obj.getScore(ChatColor.DARK_GREEN + "Green: " + ChatColor.GREEN + "Fort");
        Score blu = obj.getScore(ChatColor.BLUE + "Blue: " + ChatColor.GREEN + "Fort");
        Score cen = obj.getScore(ChatColor.LIGHT_PURPLE + "Center: " + ChatColor.GREEN + "Fort");
        r.setScore(15);
        y.setScore(14);
        g.setScore(13);
        b.setScore(12);
        blank3.setScore(11);
        kills.setScore(10);
        deaths.setScore(9);
        captures.setScore(8);
        blank2.setScore(7);
        flags.setScore(6);
        red.setScore(5);
        yel.setScore(4);
        gre.setScore(3);
        blu.setScore(2);
        cen.setScore(1);
        Captures score;
    }

    private String getString(Captures c, GameTeam team) {
        GameTeam t = c.getTeam();
        String s = "";
        switch (t) {
            case RED:
                if (team.equals(GameTeam.RED)) {
                    s = ChatColor.RED + "" + ChatColor.BOLD + "Red:  " + ChatColor.YELLOW + "[" +
                            c.getMarker(1) + "] " + ChatColor.DARK_GREEN + "[" + c.getMarker(2) + "] " +
                            ChatColor.BLUE + "[" + c.getMarker(3) + "] " + ChatColor.LIGHT_PURPLE + "[" +
                            c.getCMarker() + "]";
                } else {
                    s = ChatColor.RED + "Red:    " + ChatColor.YELLOW + "[" + c.getMarker(1) + "] " +
                            ChatColor.DARK_GREEN + "[" + c.getMarker(2) + "] " + ChatColor.BLUE + "[" +
                            c.getMarker(3) + "] " + ChatColor.LIGHT_PURPLE + "[" + c.getCMarker() + "]";
                }
                break;
            case YELLOW:
                if (team.equals(GameTeam.YELLOW)) {
                    s = ChatColor.YELLOW + "" + ChatColor.BOLD + "Yellow: " + ChatColor.RED + "[" +
                            c.getMarker(1) + "] " + ChatColor.DARK_GREEN + "[" + c.getMarker(2) + "] " +
                            ChatColor.BLUE + "[" + c.getMarker(3) + "] " + ChatColor.LIGHT_PURPLE + "[" +
                            c.getCMarker() + "]";
                } else {
                    s = ChatColor.YELLOW + "Yellow: " + ChatColor.RED + "[" + c.getMarker(1) + "] " +
                            ChatColor.DARK_GREEN + "[" + c.getMarker(2) + "] " + ChatColor.BLUE + "[" +
                            c.getMarker(3) + "] " + ChatColor.LIGHT_PURPLE + "[" + c.getCMarker() + "]";
                }
                break;
            case GREEN:
                if (team.equals(GameTeam.GREEN)) {
                    s = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Green: " + ChatColor.RED + "[" +
                            c.getMarker(1) + "] " + ChatColor.YELLOW + "[" + c.getMarker(2) + "] " +
                            ChatColor.BLUE + "[" + c.getMarker(3) + "] " + ChatColor.LIGHT_PURPLE + "[" +
                            c.getCMarker() + "]";
                } else {
                    s = ChatColor.DARK_GREEN + "Green: " + ChatColor.RED + "[" + c.getMarker(1) + "] " +
                            ChatColor.YELLOW + "[" + c.getMarker(2) + "] " + ChatColor.BLUE + "[" +
                            c.getMarker(3) + "] " + ChatColor.LIGHT_PURPLE + "[" + c.getCMarker() + "]";
                }
                break;
            case BLUE:
                if (team.equals(GameTeam.BLUE)) {
                    s = ChatColor.BLUE + "" + ChatColor.BOLD + "Blue:   " + ChatColor.RED + "[" +
                            c.getMarker(1) + "] " + ChatColor.YELLOW + "[" + c.getMarker(2) + "] " +
                            ChatColor.DARK_GREEN + "[" + c.getMarker(3) + "] " + ChatColor.LIGHT_PURPLE + "[" +
                            c.getCMarker() + "]";
                } else {
                    s = ChatColor.BLUE + "Blue:   " + ChatColor.RED + "[" + c.getMarker(1) + "] " +
                            ChatColor.YELLOW + "[" + c.getMarker(2) + "] " + ChatColor.DARK_GREEN + "[" +
                            c.getMarker(3) + "] " + ChatColor.LIGHT_PURPLE + "[" + c.getCMarker() + "]";
                }
                break;
        }
        return s;
    }

    public void setObjective(Player tp) {
        Objective obj = tp.getScoreboard().registerNewObjective("main", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Objective health = tp.getScoreboard().registerNewObjective("health", "health");
        health.setDisplayName(ChatColor.RED + "❤");
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);
        GameTeam team = CaptureTheFlag.gameUtil.getTeam(tp.getUniqueId());
        setTeamValues(team, obj);
    }

    public void setupPlayer(Player tp) {
        Scoreboard sb = sbm.getNewScoreboard();
        Team red = sb.registerNewTeam("red");
        Team yellow = sb.registerNewTeam("yellow");
        Team green = sb.registerNewTeam("green");
        Team blue = sb.registerNewTeam("blue");
        Team spectator = sb.registerNewTeam("spectator");
        red.setAllowFriendlyFire(false);
        red.setCanSeeFriendlyInvisibles(true);
        red.setPrefix(ChatColor.RED + "");
        yellow.setAllowFriendlyFire(false);
        yellow.setCanSeeFriendlyInvisibles(true);
        yellow.setPrefix(ChatColor.YELLOW + "");
        green.setAllowFriendlyFire(false);
        green.setCanSeeFriendlyInvisibles(true);
        green.setPrefix(ChatColor.DARK_GREEN + "");
        blue.setAllowFriendlyFire(false);
        blue.setCanSeeFriendlyInvisibles(true);
        blue.setPrefix(ChatColor.BLUE + "");
        spectator.setAllowFriendlyFire(false);
        spectator.setCanSeeFriendlyInvisibles(true);
        spectator.setPrefix(ChatColor.GRAY + "");
        spectator.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
        tp.setScoreboard(sb);
    }

    public void joinSpectator(Player player) {
        PotionEffect invis = new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0);
        player.addPotionEffect(invis);
        spectators.add(player.getUniqueId());
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            tp.getScoreboard().getTeam("spectator").addEntry(player.getName());
            if (!spectators.contains(tp.getUniqueId())) {
                tp.hidePlayer(player);
            }
        }
        setObjective(player);
        for (Player tp : Bukkit.getOnlinePlayers()) {
            GameTeam team = CaptureTheFlag.gameUtil.getTeam(tp.getUniqueId());
            player.getScoreboard().getTeam(team.name().toLowerCase()).addEntry(tp.getName());
        }
    }

    private GameTeam getTeam(BannerPlace place) {
        switch (place) {
            case RED:
                return GameTeam.RED;
            case YELLOW:
                return GameTeam.YELLOW;
            case GREEN:
                return GameTeam.GREEN;
            case BLUE:
                return GameTeam.BLUE;
        }
        return null;
    }

    public int objInt(GameTeam team) {
        switch (team) {
            case RED:
                return 10;
            case YELLOW:
                return 7;
            case GREEN:
                return 4;
            case BLUE:
                return 1;
            default:
                return 0;
        }
    }

    public void joinTeam(UUID uuid, GameTeam team) {
        Player player = Bukkit.getPlayer(uuid);
        switch (team) {
            case RED:
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    tp.getScoreboard().getTeam("red").addEntry(player.getName());
                }
                break;
            case YELLOW:
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    tp.getScoreboard().getTeam("yellow").addEntry(player.getName());
                }
                break;
            case GREEN:
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    tp.getScoreboard().getTeam("green").addEntry(player.getName());
                }
                break;
            case BLUE:
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    tp.getScoreboard().getTeam("blue").addEntry(player.getName());
                }
                break;
            case SPECTATOR:
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    tp.getScoreboard().getTeam("spectator").addEntry(player.getName());
                }
                break;
        }
    }

    public Captures getScore(GameTeam team) {
        switch (team) {
            case RED:
                return redScore;
            case YELLOW:
                return yellowScore;
            case GREEN:
                return greenScore;
            case BLUE:
                return blueScore;
        }
        return null;
    }

    public void capture(Player player, BannerPlace place) {
        GameTeam t = CaptureTheFlag.gameUtil.getTeam(player.getUniqueId());
        PlayerData data = CaptureTheFlag.getPlayerData(player.getUniqueId());
        data.setCaptures(data.getCaptures() + 1);
        updateCaptures(player);
        Captures score = getScore(t);
        int i = CaptureTheFlag.gameUtil.getPlace(t, place);
        score.setFlag(i, true);
        int sc = getScoreForTeam(t);
        for (Player tp : Bukkit.getOnlinePlayers()) {
            GameTeam team = CaptureTheFlag.gameUtil.getTeam(tp.getUniqueId());
            Scoreboard sb = tp.getScoreboard();
            Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
            for (String s : sb.getEntries()) {
                if (obj.getScore(s).getScore() != sc) {
                    continue;
                }
                sb.resetScores(s);
                break;
            }
            obj.getScore(getString(score, team)).setScore(sc);
        }
        setFlagLocation("Fort", place);
    }

    private int getScoreForTeam(GameTeam team) {
        switch (team) {
            case RED:
                return 15;
            case YELLOW:
                return 14;
            case GREEN:
                return 13;
            case BLUE:
                return 12;
            case SPECTATOR:
                break;
        }
        return 0;
    }

    public void updateDeaths(Player player) {
        Scoreboard sb = player.getScoreboard();
        Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
        PlayerData data = CaptureTheFlag.getPlayerData(player.getUniqueId());
        for (String s : sb.getEntries()) {
            if (obj.getScore(s).getScore() != 9) {
                continue;
            }
            sb.resetScores(s);
            break;
        }
        Score deaths = obj.getScore(ChatColor.GREEN + "Deaths: " + data.getDeaths());
        deaths.setScore(9);
    }

    public void updateKills(Player player) {
        Scoreboard sb = player.getScoreboard();
        Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
        PlayerData data = CaptureTheFlag.getPlayerData(player.getUniqueId());
        for (String s : sb.getEntries()) {
            if (obj.getScore(s).getScore() != 10) {
                continue;
            }
            sb.resetScores(s);
            break;
        }
        Score kills = obj.getScore(ChatColor.GREEN + "Kills: " + data.getKills());
        kills.setScore(10);
    }

    public void updateCaptures(Player player) {
        Scoreboard sb = player.getScoreboard();
        Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
        PlayerData data = CaptureTheFlag.getPlayerData(player.getUniqueId());
        for (String s : sb.getEntries()) {
            if (obj.getScore(s).getScore() != 8) {
                continue;
            }
            sb.resetScores(s);
            break;
        }
        Score captures = obj.getScore(ChatColor.GREEN + "Captures: " + data.getCaptures());
        captures.setScore(8);
    }

    public void setFlagLocation(String loc, BannerPlace place) {
        int reset = 0;
        String set = "";
        loc = ChatColor.GREEN + loc;
        switch (place) {
            case RED:
                reset = 5;
                set = ChatColor.RED + "Red: " + loc;
                break;
            case YELLOW:
                reset = 4;
                set = ChatColor.YELLOW + "Yellow: " + loc;
                break;
            case GREEN:
                reset = 3;
                set = ChatColor.DARK_GREEN + "Green: " + loc;
                break;
            case BLUE:
                reset = 2;
                set = ChatColor.BLUE + "Blue: " + loc;
                break;
            case CENTER:
                reset = 1;
                set = ChatColor.LIGHT_PURPLE + "Center: " + loc;
                break;
        }
        for (Player tp : Bukkit.getOnlinePlayers()) {
            try {
                Scoreboard sb = tp.getScoreboard();
                Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
                for (String s : sb.getEntries()) {
                    if (obj.getScore(s).getScore() != reset) {
                        continue;
                    }
                    sb.resetScores(s);
                    break;
                }
                Score score = obj.getScore(set);
                score.setScore(reset);
            } catch (Exception ignored) {
            }
        }
    }
}