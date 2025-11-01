package de.theredno.planc;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TpaSystem {

    private static List<Map<Player, Player>> pending = new ArrayList<>();

    public static void sendTpaRequest(Player sender, Player receiver) {
        pending.add(Map.of(sender, receiver));

        TextComponent accept = new TextComponent(ChatColor.GREEN + "Accept");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/say hi"));

        receiver.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.GRAY + " wants to teleport to you: " + accept);
    }

    public static void accept(Player target, Player sender) {
        if (pending.contains(Map.of(sender, target))) {
            sender.teleport(target);
        }
    }

    public static void cancel(Player target, Player sender) {
        if (pending.contains(Map.of(sender, target))) {
            pending.remove(Map.of(sender, target));

            target.sendMessage( sender.getName()+ ChatColor.YELLOW + "has cancelled tpa");
        }
    }

    public static void deny(Player target, Player sender) {
        if (pending.contains(Map.of(sender, target))) {
            pending.remove(Map.of(sender, target));

            target.sendMessage( sender.getName()+ ChatColor.RED + "rejected the TPA request");
        }
    }

}
