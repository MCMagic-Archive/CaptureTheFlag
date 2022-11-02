package us.mcmagic.capturetheflag.utils;

import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.mcmagic.capturetheflag.CaptureTheFlag;
import us.mcmagic.capturetheflag.abilities.*;
import us.mcmagic.capturetheflag.handlers.*;
import us.mcmagic.capturetheflag.threads.*;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.arcade.GameStartEvent;
import us.mcmagic.mcmagiccore.arcade.ServerState;
import us.mcmagic.mcmagiccore.arcade.ctf.CTFKit;
import us.mcmagic.mcmagiccore.bungee.BungeeUtil;
import us.mcmagic.mcmagiccore.title.TitleObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 12/30/14
 */
public class GameUtil implements Listener {
    private List<UUID> red = new ArrayList<>();
    private List<UUID> yellow = new ArrayList<>();
    private List<UUID> green = new ArrayList<>();
    private List<UUID> blue = new ArrayList<>();
    private List<UUID> spectator = new ArrayList<>();
    private List<UUID> ingame = new ArrayList<>();
    private int minPlayers;
    private int maxPlayers;
    private HashMap<GameTeam, Location> spawns = new HashMap<>();
    public HashMap<BannerPlace, Banner> banners = new HashMap<>();
    public HashMap<BannerPlace, Location> bannerLocs = new HashMap<>();
    public Location mapMin;
    public Location mapMax;
    public Location lobby;
    private List<UUID> cooldown = new ArrayList<>();
    public List<SafeFurnace> safeFurnaces = new ArrayList<>();

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Location getMapMin() {
        return mapMin;
    }

    public Location getMapMax() {
        return mapMax;
    }

    public GameUtil() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/CaptureTheFlag/config.yml"));
        lobby = new Location(CaptureTheFlag.gameWorld, config.getDouble("lobby.x"), config.getDouble("lobby.y"),
                config.getDouble("lobby.z"), config.getInt("lobby.yaw"), config.getInt("lobby.pitch"));
        Location redspawn = new Location(CaptureTheFlag.gameWorld, config.getDouble("spawn.red.x"),
                config.getDouble("spawn.red.y"), config.getDouble("spawn.red.z"), config.getInt("spawn.red.yaw"),
                config.getInt("spawn.red.pitch"));
        Location yellowspawn = new Location(CaptureTheFlag.gameWorld, config.getDouble("spawn.yellow.x"),
                config.getDouble("spawn.yellow.y"), config.getDouble("spawn.yellow.z"), config.getInt("spawn.yellow.yaw"),
                config.getInt("spawn.yellow.pitch"));
        Location greenspawn = new Location(CaptureTheFlag.gameWorld, config.getDouble("spawn.green.x"),
                config.getDouble("spawn.green.y"), config.getDouble("spawn.green.z"), config.getInt("spawn.green.yaw"),
                config.getInt("spawn.green.pitch"));
        Location bluespawn = new Location(CaptureTheFlag.gameWorld, config.getDouble("spawn.blue.x"),
                config.getDouble("spawn.blue.y"), config.getDouble("spawn.blue.z"), config.getInt("spawn.blue.yaw"),
                config.getInt("spawn.blue.pitch"));
        Location centerbloc = new Location(CaptureTheFlag.gameWorld, config.getDouble("banner.center.x"),
                config.getDouble("banner.center.y"), config.getDouble("banner.center.z"));
        Location redbloc = new Location(CaptureTheFlag.gameWorld, config.getDouble("banner.red.x"),
                config.getDouble("banner.red.y"), config.getDouble("banner.red.z"));
        Location yellowbloc = new Location(CaptureTheFlag.gameWorld, config.getDouble("banner.yellow.x"),
                config.getDouble("banner.yellow.y"), config.getDouble("banner.yellow.z"));
        Location greenbloc = new Location(CaptureTheFlag.gameWorld, config.getDouble("banner.green.x"),
                config.getDouble("banner.green.y"), config.getDouble("banner.green.z"));
        Location bluebloc = new Location(CaptureTheFlag.gameWorld, config.getDouble("banner.blue.x"),
                config.getDouble("banner.blue.y"), config.getDouble("banner.blue.z"));
        spawns.put(GameTeam.RED, redspawn);
        spawns.put(GameTeam.YELLOW, yellowspawn);
        spawns.put(GameTeam.GREEN, greenspawn);
        spawns.put(GameTeam.BLUE, bluespawn);
        bannerLocs.put(BannerPlace.CENTER, centerbloc);
        bannerLocs.put(BannerPlace.RED, redbloc);
        bannerLocs.put(BannerPlace.YELLOW, yellowbloc);
        bannerLocs.put(BannerPlace.GREEN, greenbloc);
        bannerLocs.put(BannerPlace.BLUE, bluebloc);
        mapMin = new Location(CaptureTheFlag.gameWorld, config.getDouble("map.min.x"), config.getDouble("map.min.y"),
                config.getDouble("map.min.z"));
        mapMax = new Location(CaptureTheFlag.gameWorld, config.getDouble("map.max.x"), config.getDouble("map.max.y"),
                config.getDouble("map.max.z"));
        if (config.getInt("max-players") < 4) {
            maxPlayers = 4;
            System.out.println("A minimum of 4 players are required to start a game");
        } else {
            maxPlayers = config.getInt("max-players");
        }
        if (config.getInt("min-players") < 4) {
            minPlayers = 4;
            System.out.println("A minimum of 4 players are required to start a game");
        } else {
            minPlayers = config.getInt("min-players");
        }
    }

    public List<UUID> getIngame() {
        return ingame;
    }

    public GameTeam getTeam(UUID uuid) {
        if (red.contains(uuid)) {
            return GameTeam.RED;
        }
        if (yellow.contains(uuid)) {
            return GameTeam.YELLOW;
        }
        if (green.contains(uuid)) {
            return GameTeam.GREEN;
        }
        if (blue.contains(uuid)) {
            return GameTeam.BLUE;
        }
        return GameTeam.SPECTATOR;
    }

    public void joinTeam(UUID uuid, GameTeam team) {
        Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "You joined the " + team.getNameWithBrackets() +
                ChatColor.GREEN + " Team!");
        switch (team) {
            case RED:
                red.add(uuid);
                break;
            case YELLOW:
                yellow.add(uuid);
                break;
            case GREEN:
                green.add(uuid);
                break;
            case BLUE:
                blue.add(uuid);
                break;
            case SPECTATOR:
                spectator.add(uuid);
        }
    }

    public List<UUID> getTeamMembers(GameTeam team) {
        switch (team) {
            case RED:
                return new ArrayList<>(red);
            case YELLOW:
                return new ArrayList<>(yellow);
            case GREEN:
                return new ArrayList<>(green);
            case BLUE:
                return new ArrayList<>(blue);
            default:
                return new ArrayList<>(spectator);
        }
    }

    public boolean isInGame(UUID uuid) {
        return ingame.contains(uuid);
    }

    public int getCurrentAmount() {
        return ingame.size();
    }

    public void setupTeams() {
        List<UUID> needTeams = new ArrayList<>();
        for (Player tp : Bukkit.getOnlinePlayers()) {
            needTeams.add(tp.getUniqueId());
        }
        while (!needTeams.isEmpty()) {
            String msg = ChatColor.GREEN + "You joined the ";
            Player tp = Bukkit.getPlayer(needTeams.get(0));
            needTeams.remove(tp.getUniqueId());
            GameTeam team = GameTeam.RED;
            int size = red.size();
            if (yellow.size() < size) {
                size = yellow.size();
                team = GameTeam.YELLOW;
            }
            if (green.size() < size) {
                size = green.size();
                team = GameTeam.GREEN;
            }
            if (blue.size() < size) {
                size = blue.size();
                team = GameTeam.BLUE;
            }
            joinTeam(tp.getUniqueId(), team);
            msg += team.getColoredName() + " Team!";
            MCMagicCore.gameManager.message(tp, msg);
        }
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        WorldUtil.loadMap(mapMin, mapMax);
        Bukkit.getScheduler().runTaskTimer(CaptureTheFlag.getInstance(), new RegenTimer(), 0L, 30L);
        placeFlag(BannerPlace.CENTER);
        placeFlag(BannerPlace.RED);
        placeFlag(BannerPlace.YELLOW);
        placeFlag(BannerPlace.GREEN);
        placeFlag(BannerPlace.BLUE);
        MCMagicCore.gameManager.setState(MCMagicCore.getMCMagicConfig().serverName, ServerState.INGAME);
        setupTeams();
        for (Player tp : Bukkit.getOnlinePlayers()) {
            tp.getInventory().clear();
            tp.setMaxHealth(40);
            tp.setHealth(40);
            ingame.add(tp.getUniqueId());
            CaptureTheFlag.scoreboardUtil.setObjective(tp);
        }
        for (UUID uuid : ingame) {
            Player tp = Bukkit.getPlayer(uuid);
            PlayerData data = CaptureTheFlag.getPlayerData(tp.getUniqueId());
            tp.setGameMode(GameMode.SURVIVAL);
            teleportToSpawn(tp.getUniqueId());
            PlayerInventory pi = tp.getInventory();
            GameTeam team = getTeam(tp.getUniqueId());
            CTFKit kit = data.getKit();
            int level = data.get(kit, 2);
            kit.setItems(tp, level);
            CaptureTheFlag.scoreboardUtil.joinTeam(tp.getUniqueId(), team);
        }
        if (!spectator.isEmpty()) {
            PotionEffect invis = new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0);
            for (UUID uuid : spectator) {
                Player player = Bukkit.getPlayer(uuid);
                player.addPotionEffect(invis, true);
                for (UUID tuuid : red) {
                    Bukkit.getPlayer(tuuid).hidePlayer(player);
                }
                for (UUID tuuid : yellow) {
                    Bukkit.getPlayer(tuuid).hidePlayer(player);
                }
                for (UUID tuuid : green) {
                    Bukkit.getPlayer(tuuid).hidePlayer(player);
                }
                for (UUID tuuid : blue) {
                    Bukkit.getPlayer(tuuid).hidePlayer(player);
                }
            }
        }
        GameState.setState(GameState.PREPARING);
        BannerParticles.start();
        PrepareClock.start();
    }

    public void removeBarriers() {
        WorldUtil.replaceBlocks(mapMin, mapMax, Material.BEDROCK, Material.AIR);
        GameState.setState(GameState.SEARCHING);
        TitleObject obj = new TitleObject(ChatColor.GREEN + "The Barriers fell!", ChatColor.AQUA + "Go Capture The Flags!");
        for (Player tp : Bukkit.getOnlinePlayers()) {
            obj.send(tp);
        }
    }

    public GameTeam teamFromBanner(Banner banner) {
        if (banner.getPatterns().contains(new Pattern(DyeColor.YELLOW, PatternType.RHOMBUS_MIDDLE))) {
            return GameTeam.YELLOW;
        }
        switch (banner.getBaseColor()) {
            case RED:
                return GameTeam.RED;
            case GREEN:
                return GameTeam.GREEN;
            case CYAN:
                return GameTeam.BLUE;
            default:
                return null;
        }
    }

    public ChatColor colorFromTeamName(String team) {
        switch (team.toLowerCase()) {
            case "red":
                return ChatColor.RED;
            case "yellow":
                return ChatColor.YELLOW;
            case "green":
                return ChatColor.DARK_GREEN;
            case "blue":
                return ChatColor.BLUE;
            default:
                return ChatColor.GRAY;
        }
    }

    public Location getFlagLocation(BannerPlace place) {
        return bannerLocs.get(place);
    }

    @SuppressWarnings("deprecation")
    public void placeFlag(BannerPlace place) {
        Block block = bannerLocs.get(place).getBlock();
        switch (place) {
            case RED:
                block.setType(Material.STANDING_BANNER);
                block.setData((byte) 6);
                Banner red = (Banner) block.getState();
                for (int i = 0; i < red.getPatterns().size(); i++) {
                    red.removePattern(0);
                }
                red.setBaseColor(DyeColor.RED);
                red.addPattern(new Pattern(DyeColor.LIME, PatternType.STRIPE_MIDDLE));
                red.addPattern(new Pattern(DyeColor.ORANGE, PatternType.TRIANGLES_TOP));
                red.addPattern(new Pattern(DyeColor.ORANGE, PatternType.CIRCLE_MIDDLE));
                red.update();
                break;
            case YELLOW:
                block.setType(Material.STANDING_BANNER);
                block.setData((byte) 2);
                Banner yellow = (Banner) block.getState();
                for (int i = 0; i < yellow.getPatterns().size(); i++) {
                    yellow.removePattern(0);
                }
                yellow.setBaseColor(DyeColor.RED);
                yellow.addPattern(new Pattern(DyeColor.ORANGE, PatternType.TRIANGLE_TOP));
                yellow.addPattern(new Pattern(DyeColor.ORANGE, PatternType.TRIANGLE_BOTTOM));
                yellow.addPattern(new Pattern(DyeColor.WHITE, PatternType.TRIANGLES_TOP));
                yellow.addPattern(new Pattern(DyeColor.YELLOW, PatternType.RHOMBUS_MIDDLE));
                yellow.update();
                break;
            case GREEN:
                block.setType(Material.STANDING_BANNER);
                block.setData((byte) 10);
                Banner green = (Banner) block.getState();
                for (int i = 0; i < green.getPatterns().size(); i++) {
                    green.removePattern(0);
                }
                green.setBaseColor(DyeColor.GREEN);
                green.addPattern(new Pattern(DyeColor.LIME, PatternType.TRIANGLE_TOP));
                green.addPattern(new Pattern(DyeColor.LIME, PatternType.TRIANGLE_BOTTOM));
                green.addPattern(new Pattern(DyeColor.WHITE, PatternType.TRIANGLES_TOP));
                green.addPattern(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE));
                green.update();
                break;
            case BLUE:
                block.setType(Material.STANDING_BANNER);
                block.setData((byte) 14);
                Banner blue = (Banner) block.getState();
                for (int i = 0; i < blue.getPatterns().size(); i++) {
                    blue.removePattern(0);
                }
                blue.setBaseColor(DyeColor.CYAN);
                blue.addPattern(new Pattern(DyeColor.LIGHT_BLUE, PatternType.STRIPE_MIDDLE));
                blue.addPattern(new Pattern(DyeColor.BLUE, PatternType.TRIANGLES_TOP));
                blue.addPattern(new Pattern(DyeColor.BLUE, PatternType.CIRCLE_MIDDLE));
                blue.update();
                break;
            case CENTER:
                block.setType(Material.STANDING_BANNER);
                block.setData((byte) 8);
                Banner center = (Banner) block.getState();
                for (int i = 0; i < center.getPatterns().size(); i++) {
                    center.removePattern(0);
                }
                center.setBaseColor(DyeColor.WHITE);
                Pattern r = new Pattern(DyeColor.RED, PatternType.SQUARE_TOP_LEFT);
                Pattern yel = new Pattern(DyeColor.YELLOW, PatternType.SQUARE_TOP_RIGHT);
                Pattern grn = new Pattern(DyeColor.GREEN, PatternType.SQUARE_BOTTOM_LEFT);
                Pattern blu = new Pattern(DyeColor.BLUE, PatternType.SQUARE_BOTTOM_RIGHT);
                Pattern flw = new Pattern(DyeColor.WHITE, PatternType.FLOWER);
                Pattern dot = new Pattern(DyeColor.LIGHT_BLUE, PatternType.CIRCLE_MIDDLE);
                center.addPattern(r);
                center.addPattern(r);
                center.addPattern(r);
                center.addPattern(yel);
                center.addPattern(yel);
                center.addPattern(yel);
                center.addPattern(grn);
                center.addPattern(grn);
                center.addPattern(grn);
                center.addPattern(blu);
                center.addPattern(blu);
                center.addPattern(blu);
                center.addPattern(flw);
                center.addPattern(flw);
                center.addPattern(flw);
                center.addPattern(dot);
                center.addPattern(dot);
                center.addPattern(dot);
                center.update();
                break;
        }
    }

    public String shortenString(String string, int length) {
        if (string.length() <= length) {
            return string;
        }
        String newString = "";
        for (char c : string.toCharArray()) {
            newString += c;
        }
        return newString;
    }

    public void teleportToSpawn(UUID uuid) {
        GameTeam team = getTeam(uuid);
        Player player = Bukkit.getPlayer(uuid);
        player.teleport(spawns.get(team));
    }

    public void removePlayer(UUID uuid) {
        switch (getTeam(uuid)) {
            case RED:
                red.remove(uuid);
                break;
            case YELLOW:
                yellow.remove(uuid);
                break;
            case GREEN:
                green.remove(uuid);
                break;
            case BLUE:
                blue.remove(uuid);
                break;
            case SPECTATOR:
                spectator.remove(uuid);
                break;
        }
        ingame.remove(uuid);
        if (!GameState.isState(GameState.IN_LOBBY) && !GameState.isState(GameState.POSTGAME)) {
            List<GameTeam> teams = getEmptyTeams();
            if (teams.size() >= 3) {
                endGame(GameTeam.SPECTATOR, true, ChatColor.AQUA + "Not enough Players!");
            }
        }
    }

    private List<GameTeam> getEmptyTeams() {
        List<GameTeam> list = new ArrayList<>();
        if (red.isEmpty()) {
            list.add(GameTeam.RED);
        }
        if (yellow.isEmpty()) {
            list.add(GameTeam.YELLOW);
        }
        if (green.isEmpty()) {
            list.add(GameTeam.GREEN);
        }
        if (blue.isEmpty()) {
            list.add(GameTeam.BLUE);
        }
        return list;
    }

    public BannerPlace placeFromBanner(Banner banner) {
        if (banner.getPatterns().contains(new Pattern(DyeColor.YELLOW, PatternType.RHOMBUS_MIDDLE))) {
            return BannerPlace.YELLOW;
        }
        switch (banner.getBaseColor()) {
            case WHITE:
                return BannerPlace.CENTER;
            case RED:
                return BannerPlace.RED;
            case GREEN:
                return BannerPlace.GREEN;
            case CYAN:
                return BannerPlace.BLUE;
            default:
                return null;
        }
    }

    public void setDroppedFlag(Player player, final GameTeam team, final BannerPlace flag, Location location) {
        Item item = null;
        GameTeam bteam = teamFromFlag(flag);
        item = CaptureTheFlag.gameWorld.dropItem(player.getLocation(), new ItemStack(Material.BANNER));
        item.setCustomName(getName(flag) + " Flag");
        item.setCustomNameVisible(true);
        item.setMetadata("banner", new FixedMetadataValue(CaptureTheFlag.getInstance(), bteam != null ? bteam.getName() :
                "Center"));
        Location loc = item.getLocation();
        MCMagicCore.gameManager.broadcast(team.getColor() + "" + player.getName() + ChatColor.GREEN +
                " has dropped the " + getName(flag) + " Flag! " + ChatColor.GREEN + "Coordinates: " + ChatColor.YELLOW
                + "x: " + loc.getBlockX() + " y: " + loc.getBlockY() + " z: " + loc.getBlockZ());
        CaptureTheFlag.scoreboardUtil.setFlagLocation(ChatColor.GREEN + "" + location.getBlockX() + ", " +
                location.getBlockY() + ", " + location.getBlockZ(), flag);
        final Item finalItem = item;
        Bukkit.getScheduler().runTaskLater(CaptureTheFlag.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!finalItem.isDead()) {
                    finalItem.remove();
                    MCMagicCore.gameManager.broadcast("The " + getName(flag) + ChatColor.GREEN +
                            " Flag has returned to its Fort!");
                    CaptureTheFlag.gameUtil.placeFlag(flag);
                    CaptureMessageThread.removeTeam(team);
                    CaptureTheFlag.scoreboardUtil.setFlagLocation("Fort", flag);
                }
            }
        }, 200L);
    }

    private GameTeam teamFromFlag(BannerPlace flag) {
        switch (flag) {
            case RED:
                return GameTeam.RED;
            case YELLOW:
                return GameTeam.YELLOW;
            case GREEN:
                return GameTeam.GREEN;
            case BLUE:
                return GameTeam.BLUE;
        }
        return null;
    }

    public void captureFlag(Player player, BannerPlace place) {
        MCMagicCore.gameManager.broadcast(getTeam(player.getUniqueId()).getColor() + player.getName() +
                ChatColor.GREEN + " has captured the " + getName(place) + " Flag!");
        GameTeam team = getTeam(player.getUniqueId());
        Captures score = CaptureTheFlag.scoreboardUtil.getScore(team);
        for (UUID uuid : getTeamMembers(team)) {
            PlayerData tpdata = CaptureTheFlag.getPlayerData(uuid);
            int amount = 5;
            if (uuid.equals(player.getUniqueId())) {
                amount = 10;
            }
            addMoney(tpdata, amount);
        }
        int i = getPlace(team, place);
        placeFlag(place);
        if (score.getFlag(i)) {
            player.sendMessage(ChatColor.RED + "Your team has already captured this Flag, so you got extra money!");
            return;
        }
        CaptureTheFlag.scoreboardUtil.capture(player, place);
        Captures newscore = CaptureTheFlag.scoreboardUtil.getScore(team);
        if (newscore.isFinished()) {
            endGame(team);
        }
    }

    public static void addMoney(PlayerData tpdata, int i) {
        tpdata.setMoney(tpdata.getMoney() + i);
        Bukkit.getPlayer(tpdata.getUniqueId()).sendMessage(ChatColor.GREEN + "+$" + i);
    }

    public void endGame(GameTeam team) {
        endGame(team, false, "");
    }

    public void endGame(GameTeam team, boolean draw, String reason) {
        GameState.setState(GameState.POSTGAME);
        CaptureMessageThread.stop();
        SearchClock.stop();
        if (draw) {
            TitleObject d = new TitleObject(ChatColor.YELLOW + "IT'S A DRAW!", reason);
            MCMagicCore.gameManager.broadcast(ChatColor.YELLOW + "The Game has come to a Draw! " + reason);
            for (Player tp : Bukkit.getOnlinePlayers()) {
                tp.setGameMode(GameMode.ADVENTURE);
                d.send(tp);
            }
            stop();
            return;
        }
        MCMagicCore.gameManager.broadcast("The " + team.getColoredName() + " Team" + ChatColor.GREEN +
                " has captured all Flags!");
        TitleObject won = new TitleObject(ChatColor.GREEN + "YOU WON!", ChatColor.AQUA +
                "Congratulations!").setFadeIn(10).setStay(100).setFadeOut(10);
        TitleObject lost = new TitleObject(ChatColor.RED + "YOU LOST!", ChatColor.AQUA +
                "That's okay, try again next time!").setFadeIn(10).setStay(100).setFadeOut(10);
        List<UUID> winteam = getTeamMembers(team);
        for (UUID uuid : winteam) {
            Player tp = Bukkit.getPlayer(uuid);
            tp.setGameMode(GameMode.ADVENTURE);
            won.send(tp);
            addMoney(CaptureTheFlag.getPlayerData(tp.getUniqueId()), 15);
        }
        for (UUID uuid : ingame) {
            if (spectator.contains(uuid)) {
                continue;
            }
            Player tp = Bukkit.getPlayer(uuid);
            if (winteam.contains(uuid)) {
                tp.setGameMode(GameMode.ADVENTURE);
                continue;
            }
            tp.setGameMode(GameMode.ADVENTURE);
            lost.send(tp);
        }
        stop();
    }

    public void joinSpectator(Player player) {
        joinTeam(player.getUniqueId(), GameTeam.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.teleport(getFlagLocation(BannerPlace.CENTER));
        CaptureTheFlag.scoreboardUtil.joinSpectator(player);
    }

    private void stop() {
        for (PlayerData data : CaptureTheFlag.getPlayerData().values()) {
            try {
                MCMagicCore.economy.addBalance(data.getUniqueId(), data.getMoney());
            } catch (Exception ignored) {
            }
        }
        Bukkit.getScheduler().runTaskLater(CaptureTheFlag.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    PlayerData data = CaptureTheFlag.getPlayerData(tp.getUniqueId());
                    MCMagicCore.gameManager.moneyMessage(tp, data.getMoney());
                }
                Bukkit.getScheduler().runTaskLater(CaptureTheFlag.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        BungeeUtil.emptyServer();
                        Bukkit.getScheduler().runTaskLater(CaptureTheFlag.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                for (Player tp : Bukkit.getOnlinePlayers()) {
                                    tp.kickPlayer("Server Restarting!");
                                }
                                Bukkit.shutdown();
                            }
                        }, 100L);
                    }
                }, 150L);
            }
        }, 50L);
    }

    public int getPlace(GameTeam team, BannerPlace place) {
        switch (team) {
            case RED:
                switch (place) {
                    case YELLOW:
                        return 1;
                    case GREEN:
                        return 2;
                    case BLUE:
                        return 3;
                    case CENTER:
                        return 4;
                }
                break;
            case YELLOW:
                switch (place) {
                    case RED:
                        return 1;
                    case GREEN:
                        return 2;
                    case BLUE:
                        return 3;
                    case CENTER:
                        return 4;
                }
                break;
            case GREEN:
                switch (place) {
                    case RED:
                        return 1;
                    case YELLOW:
                        return 2;
                    case BLUE:
                        return 3;
                    case CENTER:
                        return 4;
                }
                break;
            case BLUE:
                switch (place) {
                    case RED:
                        return 1;
                    case YELLOW:
                        return 2;
                    case GREEN:
                        return 3;
                    case CENTER:
                        return 4;
                }
                break;
        }
        return 0;
    }

    public boolean grabFlag(final Player player, BannerPlace place, boolean b) {
        GameTeam team = getTeam(player.getUniqueId());
        if (team.name().equalsIgnoreCase(place.name())) {
            if (b) {
                returnFlag(player, place);
                return true;
            }
            return false;
        }
        if (player.hasMetadata("hasflag") && player.getMetadata("hasflag").get(0).asBoolean()) {
            if (!cooldown.contains(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Capture your current flag first!");
                cooldown.add(player.getUniqueId());
                Bukkit.getScheduler().runTaskLater(CaptureTheFlag.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        cooldown.remove(player.getUniqueId());
                    }
                }, 20L);
            }
            return false;
        }
        MCMagicCore.gameManager.broadcast(getTeam(player.getUniqueId()).getColor() + player.getName() +
                ChatColor.GREEN + " has grabbed the " + getName(place) + " Flag!");
        GameTeam bteam = teamFromFlag(place);
        if (bteam != null) {
            CaptureMessageThread.addTeam(bteam);
        }
        player.removeMetadata("hasflag", CaptureTheFlag.getInstance());
        player.removeMetadata("flag", CaptureTheFlag.getInstance());
        player.setMetadata("hasflag", new FixedMetadataValue(CaptureTheFlag.getInstance(), true));
        player.setMetadata("flag", new FixedMetadataValue(CaptureTheFlag.getInstance(), place.name()));
        CaptureTheFlag.scoreboardUtil.setFlagLocation(team.getColor() + player.getName(), place);
        return true;
    }

    private String getName(BannerPlace place) {
        switch (place) {
            case RED:
                return ChatColor.RED + "Red";
            case YELLOW:
                return ChatColor.YELLOW + "Yellow";
            case GREEN:
                return ChatColor.DARK_GREEN + "Green";
            case BLUE:
                return ChatColor.BLUE + "Blue";
            case CENTER:
                return ChatColor.LIGHT_PURPLE + "Center";
        }
        return "";
    }

    private void returnFlag(Player player, BannerPlace place) {
        MCMagicCore.gameManager.broadcast(getTeam(player.getUniqueId()).getColor() + player.getName() +
                ChatColor.GREEN + " has returned the " + getTeam(player.getUniqueId()).getColoredName() + " Flag!");
        placeFlag(place);
        CaptureTheFlag.scoreboardUtil.setFlagLocation("Fort", place);
        CaptureMessageThread.removeTeam(getTeam(player.getUniqueId()));
    }

    public SpecialAbility getAbility(CTFKit kit) {
        switch (kit) {
            case HADES:
                return new HadesAbility();
            case BAYMAX:
                return new BaymaxAbility();
            case HERCULES:
                return new HerculesAbility();
            case MERIDA:
                return new MeridaAbility();
            case BOLT:
                return new BoltAbility();
        }
        return null;
    }

    public void dropItems(Player player) {
        PlayerData data = CaptureTheFlag.getPlayerData(player.getUniqueId());
        List<ItemStack> kitItems = data.getKit().getItems(data.get(data.getKit(), 2));
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType().equals(Material.AIR)) {
                continue;
            }
            boolean skip = false;
            for (ItemStack i : kitItems) {
                if (i.getType().equals(item.getType())) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }
            CaptureTheFlag.gameWorld.dropItem(player.getLocation(), item);
        }
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null || item.getType().equals(Material.AIR)) {
                continue;
            }
            boolean skip = false;
            for (ItemStack i : kitItems) {
                if (i.getType().equals(item.getType())) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }
            CaptureTheFlag.gameWorld.dropItem(player.getLocation(), item);
        }
    }
}