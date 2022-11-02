package us.mcmagic.capturetheflag.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.handlers.GameState;
import us.mcmagic.capturetheflag.handlers.GameTeam;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;

/**
 * Created by Marc on 10/2/15
 */
public class Commandyell implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (GameState.isState(GameState.IN_LOBBY) || GameState.isState(GameState.PREPARING)) {
            player.sendMessage(ChatColor.RED + "You can't use this right now!");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/yell [msg]");
            return true;
        }
        String msg = "";
        for (int i = 0; i < args.length; i++) {
            msg += args[i];
            if (i < (args.length - 1)) {
                msg += " ";
            }
        }
        Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
        GameTeam team = CaptureTheFlag.gameUtil.getTeam(player.getUniqueId());
        String yell = ChatColor.DARK_AQUA + "[YELL] ";
        String message;
        if (rank.getRankId() >= Rank.CASTMEMBER.getRankId()) {
            message = yell + team.getNameWithBrackets() + " " + rank.getNameWithBrackets() + " " + ChatColor.GRAY +
                    player.getName() + ": " + rank.getChatColor() + ChatColor.translateAlternateColorCodes('&',
                    msg);
        } else {
            message = yell + team.getNameWithBrackets() + " " + rank.getNameWithBrackets() + " " + ChatColor.GRAY +
                    player.getName() + ": " + rank.getChatColor() + msg;
        }
        Bukkit.broadcastMessage(message);
        return true;
    }
}