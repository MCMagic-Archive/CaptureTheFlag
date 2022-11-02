package us.mcmagic.capturetheflag.abilities;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import us.mcmagic.capturetheflag.CaptureTheFlag;

import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 8/15/15
 */
public class MeridaAbility extends SpecialAbility {

    @Override
    public void runAbility(Player player, int level, List<UUID> ignore) {
        Arrow arrow = player.launchProjectile(Arrow.class, player.getLocation().getDirection().multiply(2));
        arrow.setMetadata("magicexplode", new FixedMetadataValue(CaptureTheFlag.getInstance(), true));
    }

    @Override
    public long getCooldown() {
        return 60;
    }
}