package com.jroeseph.roeboat.events;

import com.jroeseph.roeboat.RoeBoat;
import com.jroeseph.roeboat.entities.Grave;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

import static org.bukkit.Bukkit.getScheduler;
import static org.bukkit.Bukkit.getServer;

public class SurvivalEventHandler {
    public static Dictionary<UUID, Location> survivalRespawnPoints = new Hashtable<>();
    public static boolean activeVote = false;

    public static void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (block != null &&
                    player.getWorld().getName().equals("world") &&
                    block.getBlockData() instanceof Bed) {
                survivalRespawnPoints.put(player.getUniqueId(), block.getLocation());
            } else if (block != null &&
                    (player.getWorld().getName().equals("world_nether") || player.getWorld().getName().equals("world_the_end")) &&
                    block.getBlockData() instanceof RespawnAnchor) {
                survivalRespawnPoints.put(player.getUniqueId(), block.getLocation());
            }
        }
    }

    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location respawnLocation = survivalRespawnPoints.get(player.getUniqueId());
        if (respawnLocation == null)
            return;

        Block block = respawnLocation.getBlock();

        if (block.getBlockData() instanceof RespawnAnchor respawnAnchor) {
            if (respawnAnchor.getCharges() == 0) {
                event.setRespawnLocation(RoeBoat.survivalWorld.getSpawnLocation());
                player.sendMessage("You have no home bed or charged respawn anchor, or it was obstructed");
                player.sendMessage(ChatColor.GRAY + "Don't worry, your items won't despawn! A grave has appeared where you died, and if you sneak on top of it, you will collect your items!");
                return;
            } else {
                respawnAnchor.setCharges(respawnAnchor.getCharges() - 1);
                block.setBlockData(respawnAnchor);
            }
        } else if (!(block.getBlockData() instanceof Bed)) {
            event.setRespawnLocation(RoeBoat.survivalWorld.getSpawnLocation());
            player.sendMessage("You have no home bed or charged respawn anchor, or it was obstructed");
            player.sendMessage(ChatColor.GRAY + "Don't worry, your items won't despawn! A grave has appeared where you died, and if you sneak on top of it, you will collect your items!");
            return;
        }
        player.sendMessage(ChatColor.GRAY + "Don't worry, your items won't despawn! A grave has appeared where you died, and if you sneak on top of it, you will collect your items!");
        event.setRespawnLocation(respawnLocation);
    }

    public static void onDeath(PlayerDeathEvent event) {
        Grave.makeGrave(event.getEntity().getLocation(), event.getEntity(), event.getDeathMessage());
        event.getDrops().clear();
    }

    public static void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            Player player = event.getPlayer();
            for (Entity entity : player.getNearbyEntities(2,2,2)) {
                if (entity instanceof Marker marker && marker.getScoreboardTags().contains("grave")) {
                    Grave.checkRemoveGrave(player, marker);
                }
            }
        }
    }

    public static void playerBedEnter(PlayerBedEnterEvent event) {
        if (!activeVote && event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK && getServer().getOnlinePlayers().size() > 1) {
            activeVote = true;
            RoeBoat.yesVotes.clear();
            RoeBoat.noVotes.clear();
            getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "A player has begun to sleep! Set to day? (Vote ending in 10 seconds)");
            BaseComponent[] component = new ComponentBuilder("[Day]")
                    .color(net.md_5.bungee.api.ChatColor.YELLOW)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/voteday"))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Set it to day")))
                    .append(" [Night]")
                    .color(net.md_5.bungee.api.ChatColor.BLUE)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/votenight"))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Keep it night"))).create();
            getServer().spigot().broadcast(component);
            getScheduler().runTaskLater(RoeBoat.instance, () -> {
                if (RoeBoat.yesVotes.size() > RoeBoat.noVotes.size()) {
                    getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "The vote has passed " + RoeBoat.yesVotes.size() + " to " + RoeBoat.noVotes.size());
                    RoeBoat.survivalWorld.setTime(0);
                    RoeBoat.survivalWorld.setClearWeatherDuration(1000000);
                } else {
                    getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "The vote has failed " + RoeBoat.noVotes.size() + " to " + RoeBoat.yesVotes.size());
                }
                activeVote = false;
            }, 20L * 10L);
        }
    }

    public static void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Sheep sheep) {
             if (event.getDamageSource().getCausingEntity() instanceof Player player) {
                File file = new File("sheeplog.log");
                boolean newFile = false;
                try {
                    if (file.createNewFile()) {
                        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[RoeBoat]: Sheeplog made!");
                        newFile = true;
                    }
                } catch (Exception e) {
                    getServer().getConsoleSender().sendMessage(ChatColor.RED + "[RoeBoat]: Error making sheeplog" + e);
                }
                try (FileWriter fileWriter = new FileWriter(file, true)){
                    String message = new SimpleDateFormat("[MM/dd/yyyy @ HH:mm:ss] ").format(new java.util.Date());
                    if (sheep.getHealth() < event.getDamage())
                        message += player.getDisplayName() + " killed a sheep!";
                    else
                        message += player.getDisplayName() + " damaged sheep for " + event.getDamage() + " damage!";
                    if (newFile)
                        fileWriter.write(message);
                    else
                        fileWriter.write("\n" + message);
                } catch (Exception e) {
                    getServer().getConsoleSender().sendMessage(ChatColor.RED + "[RoeBoat]: Error making sheeplog " + e);
                }
            }
        }
    }
}
