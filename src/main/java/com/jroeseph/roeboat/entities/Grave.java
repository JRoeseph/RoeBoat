package com.jroeseph.roeboat.entities;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import java.io.File;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class Grave {

    public static void makeGrave(Location location, Player player, String deathMessage) {
        if (player.getInventory().isEmpty())
            return;

        World world = location.getWorld();
        if (world == null) {
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: Failed to form grave due to unknown world");
            return;
        }
        String tag = player.getUniqueId().toString() + "-" + System.currentTimeMillis();

        location.setPitch(0);
        location.setYaw(0);
        Marker marker = location.getWorld().spawn(location, Marker.class);
        marker.addScoreboardTag("grave");
        marker.addScoreboardTag(player.getUniqueId().toString());
        marker.addScoreboardTag(tag);

        BlockDisplay dirt = location.getWorld().spawn(location, BlockDisplay.class);
        dirt.setBlock(Bukkit.createBlockData(Material.DIRT));
        Transformation dirtTransformation = dirt.getTransformation();
        dirtTransformation.getTranslation().x -= 1f;
        dirtTransformation.getTranslation().z -= 0.5f;
        dirtTransformation.getScale().x = 2f;
        dirtTransformation.getScale().y = 0.1f;
        dirt.setTransformation(dirtTransformation);
        dirt.addScoreboardTag(tag);

        BlockDisplay stone = location.getWorld().spawn(location, BlockDisplay.class);
        stone.setBlock(Bukkit.createBlockData(Material.STONE));
        Transformation stoneTransformation = dirt.getTransformation();
        stoneTransformation.getTranslation().x += .05f;
        stoneTransformation.getTranslation().y += .1f;
        stoneTransformation.getTranslation().z += .05f;
        stoneTransformation.getScale().x = 0.5f;
        stoneTransformation.getScale().y = 1.3f;
        stoneTransformation.getScale().z = 0.9f;
        stone.setTransformation(stoneTransformation);
        stone.addScoreboardTag(tag);

        TextDisplay textForward = location.getWorld().spawn(location, TextDisplay.class);
        textForward.setText(deathMessage);
        Transformation textFTransformation = textForward.getTransformation();
        textFTransformation.getLeftRotation().y += 1;
        textFTransformation.getTranslation().y += 1.6f;
        textFTransformation.getScale().x = 0.5f;
        textFTransformation.getScale().y = 0.5f;
        textFTransformation.getScale().z = 0.5f;
        textForward.setTransformation(textFTransformation);
        textForward.addScoreboardTag(tag);

        TextDisplay textBackward = location.getWorld().spawn(location, TextDisplay.class);
        textBackward.setText(deathMessage);
        Transformation textBTransformation = textBackward.getTransformation();
        textBTransformation.getTranslation().y += 1.6f;
        textBTransformation.getLeftRotation().y -= 1;
        textBTransformation.getScale().x = 0.5f;
        textBTransformation.getScale().y = 0.5f;
        textBTransformation.getScale().z = 0.5f;
        textBackward.setTransformation(textBTransformation);
        textBackward.addScoreboardTag(tag);

        String filename = "roeboat_data\\player_save_data\\survival_world\\graves\\" + tag + ".data";
        File file = new File(filename);
        if (file.getParentFile().mkdirs())
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[TestPlugin]: Directory for graves successfully made!");
        try {
            if (file.createNewFile()) {
                getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[TestPlugin]: Grave data (ID:" + tag + ") successfully made for player " + player.getDisplayName());
            }
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.set("items", player.getInventory().getContents());
            configuration.save(file);
        } catch (Exception e) {
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: ERROR writing player data to file: " + e);
        }
    }

    public static void checkRemoveGrave(Player player, Marker marker) {
        if (marker.getScoreboardTags().contains(player.getUniqueId().toString()) || marker.getScoreboardTags().contains("TestGrave")) {
            marker.getScoreboardTags().remove(player.getUniqueId().toString());
            marker.getScoreboardTags().remove("TestGrave");
            marker.getScoreboardTags().remove("grave");
            String uniqueTag = marker.getScoreboardTags().iterator().next();
            for (Entity entity : player.getNearbyEntities(5,5,5)) {
                if (entity.getScoreboardTags().contains(uniqueTag))
                    entity.remove();
            }
            String filename = "roeboat_data\\player_save_data\\survival_world\\graves\\" + uniqueTag + ".data";
            File file = new File(filename);
            if (file.getParentFile().mkdirs())
                getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[TestPlugin]: Directory for graves successfully made!");
            try {
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                @SuppressWarnings("unchecked") List<ItemStack> contents = (List<ItemStack>) configuration.get("items");
                if (contents != null) {
                    for (int i = 0; i < contents.size(); i++) {
                        if (contents.get(i) == null)
                            continue;
                        if (player.getInventory().getItem(i) == null) {
                            player.getInventory().setItem(i, contents.get(i));
                        } else {
                            World world = player.getWorld();
                            Item item = world.spawn(player.getLocation(), Item.class);
                            item.setItemStack(contents.get(i));
                            item.setPickupDelay(0);
                        }
                    }
                }
                if (!file.delete())
                    getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: ERROR failed to delete grave " + uniqueTag);
            } catch (Exception e) {
                getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: ERROR loading player data from file: " + e);
            }
        } else {
            player.sendMessage(ChatColor.GRAY + "This is not your grave!");
        }
    }
}
