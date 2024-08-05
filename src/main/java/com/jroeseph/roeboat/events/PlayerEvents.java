package com.jroeseph.roeboat.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import static org.bukkit.Bukkit.getServer;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        switch (event.getPlayer().getWorld().getName()) {
            case "hub_world": {
                HubEventHandler.onPlayerUse(event);
                break;
            }
            case "world":
            case "world_nether":
            case "world_the_end": {
                SurvivalEventHandler.onPlayerUse(event);
                break;
            }
            default: {
                getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: ERROR player used item in unknown world");
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        switch (event.getPlayer().getWorld().getName()) {
            case "hub_world": {
                HubEventHandler.onPlayerDropItem(event);
                break;
            }
            case "world":
            case "world_nether":
            case "world_the_end": {
                break;
            }
            default: {
                getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: ERROR player dropped item in unknown world");
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        switch (event.getWhoClicked().getWorld().getName()) {
            case "hub_world": {
                HubEventHandler.onInventoryClick(event);
                break;
            }
            case "world":
            case "world_nether":
            case "world_the_end": {
                break;
            }
            default: {
                getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: ERROR player quit in unknown world");
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        switch (event.getEntity().getWorld().getName()) {
            case "hub_world": {
                HubEventHandler.onDeath(event);
                break;
            }
            case "world":
            case "world_nether":
            case "world_the_end": {
                SurvivalEventHandler.onDeath(event);
                break;
            }
            default: {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "[TestPlugin]: ERROR player died in unknown world");
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        switch (event.getPlayer().getWorld().getName()) {
            case "hub_world": {
                break;
            }
            case "world":
            case "world_nether":
            case "world_the_end": {
                SurvivalEventHandler.onPlayerRespawn(event);
                break;
            }
            default: {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "[TestPlugin]: ERROR player died in unknown world");
            }
        }
    }

    @EventHandler
    public void playerToggleSneak(PlayerToggleSneakEvent event) {
        switch (event.getPlayer().getWorld().getName()) {
            case "hub_world": {
                break;
            }
            case "world":
            case "world_nether":
            case "world_the_end": {
                SurvivalEventHandler.onPlayerSneak(event);
                break;
            }
            default: {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "[TestPlugin]: ERROR player died in unknown world");
            }
        }
    }

    @EventHandler
    public void playerBedEnter(PlayerBedEnterEvent event) {
        switch (event.getPlayer().getWorld().getName()) {
            case "hub_world":
            case "world_nether":
            case "world_the_end": {
                break;
            }
            case "world": {
                SurvivalEventHandler.playerBedEnter(event);
                break;
            }
            default: {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "[TestPlugin]: ERROR player sleeping in unknown world");
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        switch (event.getEntity().getWorld().getName()) {
            case "hub_world": {
                break;
            }
            case "world":
            case "world_nether":
            case "world_the_end": {
                SurvivalEventHandler.onDamage(event);
                break;
            }
            default: {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "[TestPlugin]: ERROR player damaging in unknown world");
            }
        }
    }
}
