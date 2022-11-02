package us.mcmagic.capturetheflag.handlers;

/**
 * Created by Marc on 8/5/15
 */
public enum BannerPlace {
    RED, YELLOW, GREEN, BLUE, CENTER;

    public static BannerPlace fromString(String teamname) {
        switch (teamname.toLowerCase()) {
            case "red":
                return RED;
            case "yellow":
                return YELLOW;
            case "green":
                return GREEN;
            case "blue":
                return BLUE;
            case "center":
                return CENTER;
        }
        return null;
    }
}