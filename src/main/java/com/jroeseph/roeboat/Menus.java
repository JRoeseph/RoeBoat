package com.jroeseph.roeboat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

import static org.bukkit.Bukkit.createInventory;

public class Menus {

    public static void openHubMenu(Player player) {
        Inventory hubMenu = createInventory(null, 54, ChatColor.DARK_AQUA + "Hub Menu");
        for (int i = 0; i < hubMenu.getSize(); i++) {
            hubMenu.setItem(i, createItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1 , " ", " "));
        }
        hubMenu.setItem(22, createItemStack(
                Material.DIAMOND_PICKAXE,
                1,
                ChatColor.LIGHT_PURPLE + "Survival World",
                "Click here to join the survival world!"));
        player.openInventory(hubMenu);
    }

    public static ItemStack createItemStack(Material material, int amount, String name, String... lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }
}
