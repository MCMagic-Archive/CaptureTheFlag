package us.mcmagic.capturetheflag.abilities;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 8/15/15
 */
public abstract class SpecialAbility {

    public abstract void runAbility(Player player, int level, List<UUID> ignore);

    public abstract long getCooldown();
}