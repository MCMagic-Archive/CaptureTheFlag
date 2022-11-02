package us.mcmagic.capturetheflag.handlers;

import org.bukkit.ChatColor;

/**
 * Created by Marc on 12/30/14
 */
public enum GameTeam {
    RED(ChatColor.RED, "Red"), YELLOW(ChatColor.YELLOW, "Yellow"), GREEN(ChatColor.DARK_GREEN, "Green"),
    BLUE(ChatColor.BLUE, "Blue"), SPECTATOR(ChatColor.GRAY, "Spectator");

    public ChatColor color;
    public String prefix;


    GameTeam(ChatColor color, String prefix) {
        this.color = color;
        this.prefix = prefix;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getNameWithBrackets() {
        return ChatColor.WHITE + "[" + color + prefix + ChatColor.WHITE + "]";
    }

    public String getColoredName() {
        return color + prefix;
    }

    public String getName() {
        return prefix;
    }

    public static GameTeam fromString(String name) {
        switch (name.toLowerCase()) {
            case "red":
                return RED;
            case "yellow":
                return YELLOW;
            case "green":
                return GREEN;
            case "blue":
                return BLUE;
            case "spectator":
                return SPECTATOR;
            default:
                return SPECTATOR;
        }
    }
}
