package us.mcmagic.capturetheflag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mcmagic.capturetheflag.commands.Commandhub;
import us.mcmagic.capturetheflag.commands.Commandstartgame;
import us.mcmagic.capturetheflag.commands.Commandyell;
import us.mcmagic.capturetheflag.handlers.GameState;
import us.mcmagic.capturetheflag.handlers.PlayerData;
import us.mcmagic.capturetheflag.listeners.*;
import us.mcmagic.capturetheflag.threads.CaptureMessageThread;
import us.mcmagic.capturetheflag.utils.GameUtil;
import us.mcmagic.capturetheflag.utils.ScoreboardUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.arcade.ServerState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class CaptureTheFlag extends JavaPlugin {
    private static CaptureTheFlag instance;
    public static GameUtil gameUtil;
    public static ScoreboardUtil scoreboardUtil;
    private static HashMap<UUID, PlayerData> playerData = new HashMap<>();
    public static World gameWorld;
    public static HashMap<UUID, String> cache = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        GameState.setState(GameState.SERVER_STARTING);
        gameWorld = Bukkit.getWorlds().get(0);
        gameUtil = new GameUtil();
        registerListeners();
        registerCommands();
        scoreboardUtil = new ScoreboardUtil();
        CaptureMessageThread.start();
        GameState.setState(GameState.IN_LOBBY);
        String c = ChatColor.GOLD.toString();
        String s = "          ";
        MCMagicCore.gameManager.setGameData("Capture The Flag", ChatColor.GREEN + s + "              Capture The Flag",
                new String[]{c + s + "      Capture all other Flags before", c + s +
                        "     time runs out! Protect your flags", c + s + "   from being captured by other teams!"},
                16, 40, 30);
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                MCMagicCore.gameManager.setState(MCMagicCore.getMCMagicConfig().serverName, ServerState.ONLINE);
            }
        }, 100L);
    }

    @Override
    public void onDisable() {
        MCMagicCore.gameManager.setState(MCMagicCore.getMCMagicConfig().serverName, ServerState.RESTARTING);
        MCMagicCore.gameManager.setPlayerCount(MCMagicCore.getMCMagicConfig().serverName, 0);
    }

    public static CaptureTheFlag getInstance() {
        return instance;
    }

    public void registerCommands() {
        getCommand("hub").setExecutor(new Commandhub());
        getCommand("hub").setAliases(Arrays.asList("lobby", "return"));
        getCommand("startgame").setExecutor(new Commandstartgame());
        getCommand("yell").setExecutor(new Commandyell());
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlockEdit(), this);
        pm.registerEvents(new Weather(), this);
        pm.registerEvents(new PlayerJoinAndLeave(), this);
        pm.registerEvents(new ItemCraft(), this);
        pm.registerEvents(new PlayerMove(), this);
        pm.registerEvents(new FoodLevel(), this);
        pm.registerEvents(new PlayerChat(), this);
        pm.registerEvents(new PlayerDropItem(), this);
        pm.registerEvents(new ChunkUnload(), this);
        pm.registerEvents(new PlayerDamage(), this);
        pm.registerEvents(new PlayerInteract(), this);
        pm.registerEvents(new ProjectileHit(), this);
        pm.registerEvents(new PlayerPickupItem(), this);
        pm.registerEvents(gameUtil, this);
    }

    public static void login(UUID uuid, PlayerData data) {
        playerData.remove(uuid);
        playerData.put(uuid, data);
    }

    public static void logout(UUID uuid) {
        playerData.remove(uuid);
    }

    public static HashMap<UUID, PlayerData> getPlayerData() {
        return new HashMap<>(playerData);
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }
}