package com.jroeseph.roeboat;

import com.jroeseph.roeboat.entities.Grave;
import com.jroeseph.roeboat.events.PlayerTransferHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static org.bukkit.Bukkit.getServer;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {

        switch (cmd.getName()) {
            case "hub":
                return processHub(sender);
            case "survival":
                return processSurvival(sender);
            case "spawngrave":
                return processSpawngrave(sender, args);
            case "sheeplog":
                return processSheepLog(sender);
            case "timevote":
                return processTimevote(sender, args);
            case "cc":
                return processCc(sender, args);
            default:
                getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: ERROR unknown command processed");
        }

        return false;
    }

    public static boolean processHub(CommandSender sender) {
        if (sender instanceof Player player) {
            switch (player.getWorld().getName()) {
                case "hub_world": {
                    player.sendMessage(ChatColor.GRAY + "You're already here!");
                    return true;
                }
                case "world":
                case "world_nether":
                case "world_the_end": {
                    PlayerTransferHandler.saveSurvivalPlayerData(player);
                    PlayerTransferHandler.joinHub(player);
                    return true;
                }
                default: {
                    getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: ERROR player executed command \"/hub\" in unknown world");
                }
            }
        }
        return false;
    }

    public static boolean processSurvival(CommandSender sender) {
        if (sender instanceof Player player) {
            switch (player.getWorld().getName()) {
                case "hub_world": {
                    PlayerTransferHandler.joinSurvival(player);
                    return true;
                }
                case "world":
                case "world_nether":
                case "world_the_end": {
                    player.sendMessage(ChatColor.GRAY + "You're already here!");
                    return true;
                }
                default: {
                    getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: ERROR player executed command \"/survival\" in unknown world");
                }
            }
        }
        return false;
    }

    public static boolean processSpawngrave(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            switch (player.getWorld().getName()) {
                case "hub_world": {
                    player.sendMessage(ChatColor.RED + "You cannot spawn test graves here!");
                    return true;
                }
                case "world":
                case "world_nether":
                case "world_the_end": {
                    String deathMessage = "Test Grave";
                    if (args.length > 1) {
                        StringBuilder deathMessageBuilder = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            deathMessageBuilder.append(args[i]);
                            deathMessageBuilder.append(" ");
                        }
                        deathMessage = deathMessageBuilder.toString();
                    }
                    Grave.makeGrave(player.getLocation(), ((Player) sender), deathMessage);
                    return true;
                }
                default: {
                    getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[TestPlugin]: ERROR player executed command \"/spawngrave\" in unknown world");
                }
            }
        }
        return false;
    }

    public static boolean processTimevote(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /timevote [day/night]");
            return true;
        }
        if (sender instanceof Player player) {
            if (args[0].equalsIgnoreCase("day")) {
                if (RoeBoat.yesVotes.contains(player)) {
                    player.sendMessage(ChatColor.RED + "You've already voted for day!");
                } else {
                    RoeBoat.noVotes.remove(player);
                    RoeBoat.yesVotes.add(player);
                    player.sendMessage(ChatColor.YELLOW + "You have now voted for day!");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("night")) {
                if (RoeBoat.noVotes.contains(player)) {
                    player.sendMessage(ChatColor.RED + "You've already voted for night!");
                } else {
                    RoeBoat.yesVotes.remove(player);
                    RoeBoat.noVotes.add(player);
                    player.sendMessage(ChatColor.BLUE + "You have now voted for night!");
                }
                return true;
            }
        }
        return false;
    }


    public static boolean processSheepLog(CommandSender sender) {
        Player player = (Player) sender;
        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) writtenBook.getItemMeta();
        if (meta == null)
            return false;
        meta.setAuthor("Woven Tales");
        meta.setAuthor("Deus Ovis");
        File file = new File("sheeplog.log");
        if (!file.exists())
            meta.setPages("");
        else {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                while (fileReader.ready()) {
                    String line = fileReader.readLine();
                    meta.addPage(line);
                }
            } catch (Exception e) {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "[RoeBoat]: ERROR reading sheeplog");
                return false;
            }
        }
        writtenBook.setItemMeta(meta);
        World world = player.getWorld();
        Item item = world.spawn(player.getLocation(), Item.class);
        item.setItemStack(writtenBook);
        item.setPickupDelay(0);
        return true;
    }

    public static boolean processCc(CommandSender sender, String[] args) {
        if (args.length == 0)
            return false;
        if (sender instanceof Player player) {
            player.sendMessage(ChatColor.GRAY + "This is a console only command!");
            return true;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(args[0]);
        for (int i = 1; i < args.length; i++) {
            stringBuilder.append(" ");
            stringBuilder.append(args[i]);
        }
        getServer().broadcastMessage("<JRoeseph (In Console)> " + stringBuilder);
        return true;
    }
}
