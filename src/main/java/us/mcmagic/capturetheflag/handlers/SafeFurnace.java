package us.mcmagic.capturetheflag.handlers;

import java.util.UUID;

/**
 * Created by Marc on 10/9/15
 */
public class SafeFurnace {
    private UUID uuid;
    private int x;
    private int y;
    private int z;

    public SafeFurnace(UUID uuid, int x, int y, int z) {
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SafeFurnace)) {
            return false;
        }
        SafeFurnace c = (SafeFurnace) obj;
        return c.getX() == x && c.getY() == y && c.getZ() == z;
    }
}