package de.theredno.planc.GameMechanics.DispenserPlaceBlock;

import de.theredno.planc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OnDispense implements Listener {
    @EventHandler
    public void onDispenserDispense(BlockDispenseEvent event) {
        Block block = event.getBlock();
        if (!(block.getState() instanceof Dispenser dispenser)) return;

        ItemStack dispensedItem = event.getItem();
        if (dispensedItem == null || !dispensedItem.getType().isBlock()) return;

        BlockFace facing = ((Directional) block.getBlockData()).getFacing();
        Block targetBlock = block.getRelative(facing);

        // Wenn vor dem Dispenser schon ein Block steht -> nichts tun, aber Item soll verbraucht werden
        boolean canPlace = targetBlock.isEmpty() || targetBlock.isReplaceable();
        if (!canPlace) {
            event.setCancelled(true);
            consumeLater(dispenser, dispensedItem);
            return;
        }

        // Block platzieren
        targetBlock.setType(dispensedItem.getType());

        // Item entfernen
        event.setCancelled(true);
        consumeLater(dispenser, dispensedItem);
    }

    /**
     * Entfernt EIN Item vom gleichen Typ im Dispenser – sicher nach einem Tick,
     * damit Paper den TileEntity-Status nicht überschreibt.
     */
    private void consumeLater(Dispenser dispenser, ItemStack dispensedItem) {
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            Inventory inv = dispenser.getInventory();
            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if (stack == null) continue;
                if (stack.getType() == dispensedItem.getType()) {
                    int newAmount = stack.getAmount() - 1;
                    if (newAmount <= 0) inv.setItem(i, null);
                    else stack.setAmount(newAmount);
                    break;
                }
            }
        }, 1L); // 1 Tick später
    }
}
