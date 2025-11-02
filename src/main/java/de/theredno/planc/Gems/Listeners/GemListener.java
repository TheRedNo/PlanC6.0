package de.theredno.planc.Gems.Listeners;

import de.theredno.planc.Main;
import de.theredno.planc.Gems.API.GemAPI;
import de.theredno.planc.Gems.API.createGem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GemListener implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack item = e.getItemDrop().getItemStack();

        if (item.getType().equals(Material.EMERALD) && createGem.isGem(item)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        Inventory topInv = e.getView().getTopInventory();
        Inventory clickedInv = e.getClickedInventory();

        if (clickedInv == null) return;
        if (item == null || !item.hasItemMeta()) return;
        if (!createGem.isGem(item)) return;

        if (e.getView().getTitle().equals(ChatColor.BLUE + "Gems Menu (Crafting)")) return;

        // === 1️⃣ Shift-Klicks komplett blockieren (egal wo) ===
        if (e.isShiftClick()) {
            e.setCancelled(true);
            return;
        }

        // === 2️⃣ Wenn überhaupt kein "Top Inventory" existiert (also nur eigenes Inventar offen) ===
        if (topInv == null || topInv.getType() == InventoryType.CRAFTING) {
            // Spieler hat nur sein eigenes Inventar offen
            // => NICHT abbrechen, sonst Duplikation!
            return;
        }

        // === 3️⃣ Klicks in anderen Inventaren (z. B. Chest, GUI) blockieren ===
        if (topInv.getType() != InventoryType.PLAYER && clickedInv.getType() != InventoryType.PLAYER) {
            e.setCancelled(true);
            return;
        }

        // === 4️⃣ Klicks aus Spielerinventar in fremdes Inventar blockieren ===
        if (clickedInv.getType() == InventoryType.PLAYER && topInv.getType() != InventoryType.PLAYER) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onItemFrameInteract(PlayerInteractEntityEvent e) {
        // Prüfe, ob das Ziel ein Item Frame ist
        if (!(e.getRightClicked() instanceof ItemFrame frame)) return;

        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Prüfe, ob das Item überhaupt ein Gem ist
        if (createGem.isGem(item)) {
            e.setCancelled(true);
        }
    }

    /*@EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        for (ItemStack item : e.getNewItems().values()) {
            if (item != null && item.hasItemMeta() && createGem.isGem(item)) {
                // Prüfen, ob in fremdes Inventar gezogen wird
                if (e.getView().getTopInventory().getType() != InventoryType.PLAYER) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }*/

    @EventHandler
    public void onHopperMove(InventoryMoveItemEvent e) {
        ItemStack item = e.getItem();
        if (createGem.isGem(item)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        if (createGem.isGem(e.getPlayerItem())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cDu kannst ein Gem nicht an Armor Stands benutzen!");
        }
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        for (ItemStack item : e.getInventory().getContents()) {
            if (item != null && createGem.isGem(item)) {
                e.setResult(null); // Kein Ergebnis erzeugen
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        // Wir erstellen eine Liste, um die Gems zu merken
        List<ItemStack> gems = new ArrayList<>();

        // Alle Drops durchgehen und Gems entfernen
        e.getDrops().removeIf(item -> {
            if (createGem.isGem(item)) {
                gems.add(item);
                return true; // nicht droppen
            }
            return false; // normale Items dürfen droppen
        });

        if (!gems.isEmpty()) {
            for (ItemStack gem : gems) {
                createGem custom = GemAPI.getFromItem(gem);

                if (custom != null) {
                    int newLevel = createGem.getLevelFromItem(gem) - 2;
                    if (newLevel < 1) {
                        newLevel = 1;
                    }
                    createGem.setLevelOnItem(gem, newLevel);
                    custom.updateItemLevelLore(gem, newLevel);
                }
            }

            // Variante 1: Gem bleibt im Inventar nach Respawn
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                for (ItemStack gem : gems) {
                    player.getInventory().addItem(gem);
                }
            }, 1L);
        }

    }


    @EventHandler
    public void onPlayerKillPlayer(PlayerDeathEvent event) {
        Player victim = event.getEntity();              // Der gestorbene Spieler
        Player killer = victim.getKiller();             // Der Mörder (falls ein Spieler)

        if (killer == victim) return;

        if (killer != null) {
            List<ItemStack> gems = new ArrayList<>();

            for (ItemStack item : killer.getInventory().getContents()) {
                if (createGem.isGem(item)) {
                    gems.add(item);
                }
            }

            for (ItemStack gem : gems) {
                createGem custom = GemAPI.getFromItem(gem);

                if (custom != null) {
                    int newLevel = createGem.getLevelFromItem(gem) + 2;
                    if (newLevel > 10) {
                        newLevel = 10;
                    }
                    createGem.setLevelOnItem(gem, newLevel);
                    custom.updateItemLevelLore(gem, newLevel);
                }
            }
        }
    }
}
