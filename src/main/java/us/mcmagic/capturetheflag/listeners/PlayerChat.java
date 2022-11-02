package us.mcmagic.capturetheflag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.handlers.GameState;
import us.mcmagic.capturetheflag.handlers.GameTeam;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.util.UUID;

/**
 * Created by Marc on 12/30/14
 */
public class PlayerChat implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);
        if (!GameState.isState(GameState.SEARCHING) && !GameState.isState(GameState.PREPARING)) {
            MCMagicCore.chatManager.chatMessage(player, event.getMessage());
            return;
        }
        Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
        GameTeam team = CaptureTheFlag.gameUtil.getTeam(player.getUniqueId());
        String message;
        if (rank.getRankId() >= Rank.CASTMEMBER.getRankId()) {
            message = team.getNameWithBrackets() + " " + rank.getNameWithBrackets() + " " + ChatColor.GRAY +
                    player.getName() + ": " + rank.getChatColor() + ChatColor.translateAlternateColorCodes('&',
                    event.getMessage());
        } else {
            message = team.getNameWithBrackets() + " " + rank.getNameWithBrackets() + " " + ChatColor.GRAY +
                    player.getName() + ": " + rank.getChatColor() + event.getMessage();
        }
        for (UUID uuid : CaptureTheFlag.gameUtil.getTeamMembers(team)) {
            Bukkit.getPlayer(uuid).sendMessage(message);
        }
    }
}
