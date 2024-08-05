package com.jroeseph.roeboat.events;

import com.jroeseph.roeboat.Menus;
import com.jroeseph.roeboat.RoeBoat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.*;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class PlayerTransferHandler {
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(ChatColor.AQUA + player.getDisplayName() + " joined the server!");
        joinHub(player);
    }

    public static void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        switch (player.getWorld().getName()) {
            case "hub_world": {
                break;
            }
            case "world":
            case "world_nether":
            case "world_the_end": {
                saveSurvivalPlayerData(player);
                break;
            }
            default: {
                getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: ERROR player quit in unknown world");
            }
        }
    }

    public static void saveSurvivalPlayerData(Player player) {
        String filename = "roeboat_data\\player_save_data\\survival_world\\player_state\\" + player.getUniqueId() + ".data";
        File file = new File(filename);
        try {
            if (file.createNewFile()) {
                getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[TestPlugin]: File for survival data successfully made for player " + player.getDisplayName());
            }
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.set("items", player.getInventory().getContents());
            configuration.set("health", player.getHealth());
            configuration.set("food", player.getFoodLevel());
            configuration.set("experience", player.getTotalExperience());
            configuration.set("hunger", player.getFoodLevel());
            configuration.set("effects", player.getActivePotionEffects());
            configuration.set("position", player.getLocation());
            configuration.set("respawn", SurvivalEventHandler.survivalRespawnPoints.get(player.getUniqueId()));
            configuration.save(file);
        } catch (Exception e) {
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: ERROR writing player data to file: " + e);
        }
    }

    public static void loadSurvivalPlayerData(Player player) {
        String filename = "roeboat_data\\player_save_data\\survival_world\\player_state\\" + player.getUniqueId() + ".data";
        File file = new File(filename);

        try {
            if (file.getParentFile().mkdirs() && file.createNewFile()) {
                getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[TestPlugin]: File for survival data successfully made for player " + player.getDisplayName());
                return;
            }
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            Object position = configuration.get("position");
            player.teleport((position != null) ? (Location) position : RoeBoat.survivalWorld.getSpawnLocation());
            Object respawn = configuration.get("respawn");
            if (respawn instanceof Location loc) {
                SurvivalEventHandler.survivalRespawnPoints.put(player.getUniqueId(), loc);
            } else {
                SurvivalEventHandler.survivalRespawnPoints.remove(player.getUniqueId());
            }
            player.setHealth(configuration.getDouble("health", 20));
            player.setFoodLevel(configuration.getInt("food", 20));
            player.setTotalExperience(configuration.getInt("experience", 0));
            player.setSaturation((float) configuration.getDouble("hunger", 20));
            // Here are down below's warnings are ignored since they need to be cast to List<ItemStack> but when casting
            // the T type is lost, so you can only type to type List<?>, but then the "toArray" call develops a different
            // warning
            @SuppressWarnings("unchecked") List<ItemStack> contents = (List<ItemStack>) configuration.get("items");
            if (contents != null) {
                ItemStack[] contentsArray = new ItemStack[contents.size()];
                contentsArray = contents.toArray(contentsArray);
                player.getInventory().setContents(contentsArray);
            }
            player.getActivePotionEffects().clear();
            @SuppressWarnings("unchecked") List<PotionEffect> effects = (List<PotionEffect>) configuration.get("effects");
            if (effects != null)
                player.addPotionEffects(effects);
        } catch (Exception e) {
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: ERROR loading player data from file: " + e);
        }
    }

    public static void joinHub(Player player) {
        player.teleport(new Location(RoeBoat.hubWorld, 0.5, 129, 0.5));

        Inventory playerInv = player.getInventory();
        playerInv.clear();
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
        playerInv.setItem(0, Menus.createItemStack(
                Material.NETHER_STAR,
                1,
                ChatColor.BLUE + "Hub Menu",
                "Right click with this in your hand to open the menu!"));
        player.setInvulnerable(true);
        Menus.openHubMenu(player);
    }

    public static void joinSurvival(Player player) {
        Inventory playerInv = player.getInventory();
        playerInv.clear();
        player.setInvulnerable(false);
        PlayerTransferHandler.loadSurvivalPlayerData(player);
        String motd = ChatColor.DARK_BLUE +
                "################################\n" +
                "#+++++++++++++" +
                ChatColor.GOLD +
                "MOTD" +
                ChatColor.DARK_BLUE +
                "+++++++++++++#\n" +
                "################################\n" +
                ChatColor.AQUA +
                RoeBoat.motd;
        player.sendMessage(motd);
    }
}
