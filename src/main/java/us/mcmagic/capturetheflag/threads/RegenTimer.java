package us.mcmagic.capturetheflag.threads;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 10/2/15
 */
public class RegenTimer implements Runnable {

    @Override
    public void run() {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getGameMode().equals(GameMode.CREATIVE) || tp.getGameMode().equals(GameMode.SPECTATOR)) {
                continue;
            }
            if (tp.getFoodLevel() < 19 || tp.getAllowFlight()) {
                continue;
            }
            double amount = tp.getHealth() + 0.5;
            if (amount > 40.0) {
                amount = 40.0;
            }
            tp.setHealth(amount);
        }
    }
}