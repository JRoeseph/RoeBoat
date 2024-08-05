package com.jroeseph.roeboat.events;

import com.jroeseph.roeboat.Menus;
import com.jroeseph.roeboat.RoeBoat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static org.bukkit.Bukkit.getServer;

public class HubEventHandler {
    public static void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();

        if (Material.NETHER_STAR == item.getType()) {
            Menus.openHubMenu(player);
        }
        event.setCancelled(true);
    }

    public static void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    public static void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null)
            return;

        if (Material.DIAMOND_PICKAXE == item.getType()) {
            PlayerTransferHandler.joinSurvival(player);
        } else {
            Menus.openHubMenu(player);
        }
    }

    public static void onDeath(EntityDeathEvent event) {
        Player player = (Player) event.getEntity();
        getServer().broadcastMessage(ChatColor.RED + player.getDisplayName() + " needs to stop breaking my server >:(");
        player.setRespawnLocation(new Location(RoeBoat.hubWorld, 0.5, 129, 0.5), true);
    }
}
