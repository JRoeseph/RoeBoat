package com.jroeseph.roeboat;

import com.jroeseph.roeboat.events.PlayerTransferEvents;
import com.jroeseph.roeboat.events.PlayerEvents;
import com.jroeseph.roeboat.events.PlayerTransferHandler;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.util.*;


public class RoeBoat extends JavaPlugin {
    private final WorldCreator hubCreator = WorldCreator.name("hub_world");
    private final WorldCreator worldCreator = WorldCreator.name("world");
    public static World hubWorld;
    public static World survivalWorld;
    public static Commands commands;
    public static HashSet<Player> yesVotes = new HashSet<>();
    public static HashSet<Player> noVotes = new HashSet<>();
    public static RoeBoat instance;
    public static String motd;
    public static Dictionary<Integer, ArrayList<UUID>> featureVotes;

    @Override
    public void onEnable() {
        hubWorld = hubCreator.createWorld();
        survivalWorld = worldCreator.createWorld();
        instance = this;

        getServer().getPluginManager().registerEvents(new PlayerTransferEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        initCommands();
        initData();
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[TestPlugin]: Finished initialization");
    }

    @Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers())
            if (player.getWorld() == survivalWorld)
                PlayerTransferHandler.saveSurvivalPlayerData(player);

        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[TestPlugin]: Finished shutting down");
    }

    public void initCommands() {
        commands = new Commands();
        PluginCommand hub = getCommand("hub");
        if (hub != null)
            hub.setExecutor(commands);
        else
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: Failed to initialize command \"hub\"");
        PluginCommand survival = getCommand("survival");
        if (survival != null)
            survival.setExecutor(commands);
        else
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: Failed to initialize command \"survival\"");
        PluginCommand spawngrave = getCommand("spawngrave");
        if (spawngrave != null)
            spawngrave.setExecutor(commands);
        else
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: Failed to initialize command \"spawngrave\"");
        PluginCommand sheeplog = getCommand("sheeplog");
        if (sheeplog != null)
            sheeplog.setExecutor(commands);
        else
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: Failed to initialize command \"sheeplog\"");
        PluginCommand timevote = getCommand("timevote");
        if (timevote != null)
            timevote.setExecutor(commands);
        else
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: Failed to initialize command \"timevote\"");
        PluginCommand cc = getCommand("cc");
        if (cc != null)
            cc.setExecutor(commands);
        else
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: Failed to initialize command \"cc\"");
    }

    public void initData() {
        File graves = new File("roeboat_data\\player_save_data\\survival_world\\graves");
        if (!graves.mkdirs())
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: Failed to make graves directory");
        File playerstate = new File("roeboat_data\\player_save_data\\survival_world\\player_state");
        if (!playerstate.mkdirs())
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: Failed to make player_state directory");
        File motdFile = new File("roeboat_data\\MOTD.txt");
        try (Scanner scanner = new Scanner(new FileReader(motdFile))) {
           motd = scanner.useDelimiter("\\Z").next();
        } catch (Exception e) {
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: Error reading MOTD.txt: " + e.getMessage());
        }
        File featurevote = new File("roeboat_data\\featurevote.data");
    }
}
