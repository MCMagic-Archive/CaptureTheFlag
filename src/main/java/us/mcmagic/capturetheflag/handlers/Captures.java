package us.mcmagic.capturetheflag.handlers;

/**
 * Created by Marc on 8/12/15
 */
public class Captures {
    private GameTeam team;
    private boolean flag1 = false;
    private boolean flag2 = false;
    private boolean flag3 = false;
    private boolean center = false;

    public Captures(GameTeam team) {
        this.team = team;
    }

    public GameTeam getTeam() {
        return team;
    }

    public void setFlag1(boolean flag1) {
        this.flag1 = flag1;
    }

    public void setFlag2(boolean flag2) {
        this.flag2 = flag2;
    }

    public void setFlag3(boolean flag3) {
        this.flag3 = flag3;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }

    public boolean getFlag1() {
        return flag1;
    }

    public boolean getFlag2() {
        return flag2;
    }

    public boolean getFlag3() {
        return flag3;
    }

    public boolean getCenter() {
        return center;
    }

    public String getMarker(int flag) {
        switch (flag) {
            case 1:
                return flag1 ? "X" : "_";
            case 2:
                return flag2 ? "X" : "_";
            case 3:
                return flag3 ? "X" : "_";
        }
        return "";
    }

    public String getCMarker() {
        return center ? "X" : "_";
    }

    public boolean getFlag(int i) {
        switch (i) {
            case 1:
                return flag1;
            case 2:
                return flag2;
            case 3:
                return flag3;
            case 4:
                return center;
        }
        return false;
    }

    public void setFlag(int i, boolean b) {
        switch (i) {
            case 1:
                flag1 = b;
                break;
            case 2:
                flag2 = b;
                break;
            case 3:
                flag3 = b;
                break;
            case 4:
                center = b;
        }
    }

    public boolean isFinished() {
        return flag1 && flag2 && flag3 && center;
    }
}