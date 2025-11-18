package de.theredno.planc.WebDashboard.DiamondSystem;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class DiamondListener implements Listener {
    @EventHandler
    public static void onInventoryClick(InventoryClickEvent e) {
        UUID uuid = e.getWhoClicked().getUniqueId();

        if (e.getView().getTitle().equals(ChatColor.BLUE + "Diamond Account")) {

        }
    }
}
