package us.mcmagic.capturetheflag.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.bungee.BungeeUtil;

/**
 * Created by Marc on 8/7/15
 */
public class Commandhub implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        player.sendMessage(ChatColor.AQUA + "Now returning to " + ChatColor.BLUE + "Arcade...");
        BungeeUtil.sendToServer(player, "Arcade");
        return true;
    }
}