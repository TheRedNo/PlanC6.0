package de.theredno.planc.Gems.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class GiveCreateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("You are not a PLAYER");
            return true;
        }

        ItemStack crate = new ItemStack(Material.CHEST);
        ItemMeta meta = crate.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6Gem Slot"); // Name für Rechtsklickprüfung
            crate.setItemMeta(meta);
        }

        player.getInventory().addItem(crate);
        player.sendMessage("Du hast eine Lootcrate erhalten!");

        return true;
    }
}
