package de.theredno.planc.Gems.Menu.util;

import org.bukkit.inventory.ItemStack;

public class InvItemSlotData {
    private final ItemStack item;
    private final int slot;

    public InvItemSlotData(ItemStack item, int slot) {
        this.item = item;
        this.slot = slot;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getSlot() {
        return slot;
    }
}
