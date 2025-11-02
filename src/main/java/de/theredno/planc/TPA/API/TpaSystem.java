package de.theredno.planc.TPA.API;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.inventory.ItemStack;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TpaSystem {

    private static List<Map<Player, Player>> pending = new ArrayList<>();

    public static void sendTpaRequest(Player sender, Player receiver) {
        pending.add(Map.of(sender, receiver));

        TextComponent accept = new TextComponent(ChatColor.GREEN + "[Accept]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa accept " + sender.getName()));

        // Deny Button
        TextComponent deny = new TextComponent(ChatColor.RED + "[Deny]");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa deny " + sender.getName()));

        // Nachricht zusammensetzen
        TextComponent message = new TextComponent("");
        message.addExtra(new TextComponent(ChatColor.GOLD + sender.getName()));
        message.addExtra(new TextComponent(ChatColor.GRAY + " wants to teleport to you: "));
        message.addExtra(accept);
        message.addExtra(new TextComponent(" ")); // Abstand zwischen Buttons
        message.addExtra(deny);

        // Nachricht senden (wichtig: spigot()!)
        receiver.spigot().sendMessage(message);

        sender.sendMessage(ChatColor.YELLOW + "If accepted, 1 ender pearl and 10 levels will be removed.");
    }

    public static void accept(Player target, Player sender) {
        Map<Player, Player> found = null;
        for (Map<Player, Player> map : pending) {
            if (map.containsKey(sender) && map.get(sender).equals(target)) {
                found = map;
                break;
            }
        }
        if (found != null) {
            if (sender.getInventory().contains(Material.ENDER_PEARL, 1) && sender.getLevel() >= 10) {
                sender.getInventory().removeItem(new ItemStack(Material.ENDER_PEARL, 1));
                sender.setLevel(sender.getLevel() - 10);
            } else {
                target.sendMessage(ChatColor.YELLOW + "TPA cancelled" + sender.getName() + " cannot pay the costs");
                sender.sendMessage(ChatColor.YELLOW + "TPA cancelled you cannot pay the costs");
                pending.remove(found);
                return;
            }

            pending.remove(found);
            sender.teleport(target);
            sender.sendMessage(ChatColor.GREEN + "TPA accepted!");
            target.sendMessage(ChatColor.GREEN + "You accepted the TPA request from " + sender.getName());

        } else {
            target.sendMessage(ChatColor.RED + "No pending TPA request found!");
        }
    }

    public static void cancel(Player sender) {
        Map<Player, Player> found = null;

        // Suche nach einer Map, bei der sender der Absender ist
        for (Map<Player, Player> map : pending) {
            if (map.containsKey(sender)) {
                found = map;
                break;
            }
        }

        if (found != null) {
            Player receiver = found.get(sender);
            pending.remove(found);

            sender.sendMessage(ChatColor.YELLOW + "You have cancelled your TPA request to " + receiver.getName());
            receiver.sendMessage(ChatColor.RED + sender.getName() + " has cancelled their TPA request.");
        } else {
            sender.sendMessage(ChatColor.RED + "You have no pending TPA requests to cancel.");
        }
    }


    public static void deny(Player target, Player sender) {
        Map<Player, Player> found = null;
        for (Map<Player, Player> map : pending) {
            if (map.containsKey(sender) && map.get(sender).equals(target)) {
                found = map;
                break;
            }
        }
        if (found != null) {
            pending.remove(found);
            target.sendMessage(ChatColor.RED + "TPA denied!");
            sender.sendMessage(ChatColor.RED + target.getName() + " denied your TPA request!");
        } else {
            target.sendMessage(ChatColor.RED + "No pending TPA request found!");
        }
    }

}
