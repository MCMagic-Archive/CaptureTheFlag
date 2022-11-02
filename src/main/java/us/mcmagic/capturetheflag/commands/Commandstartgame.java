package us.mcmagic.capturetheflag.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;

/**
 * Created by Marc on 8/8/15
 */
public class Commandstartgame implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (Bukkit.getOnlinePlayers().size() < 12) {
            player.sendMessage(ChatColor.RED + "You need at least 12 players to make this game fun!");
            return true;
        }
        MCMagicCore.gameManager.startCountdown();
        return true;
    }
}
