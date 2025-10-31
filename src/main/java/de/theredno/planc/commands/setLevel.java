package de.theredno.planc.commands;

import de.theredno.planc.api.GemAPI;
import de.theredno.planc.api.createGem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class setLevel implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) sender.sendMessage("Du bist kein Spieler!");

        if (!sender.hasPermission("OP")) sender.sendMessage("Du bist nicht OP");

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        createGem custom = GemAPI.getFromItem(item);

        if (custom != null) {
            int newLevel = createGem.getLevelFromItem(item) + 1;
            if (newLevel > 25) {
                player.sendMessage("Max level = 25");
                return true;
            }
            createGem.setLevelOnItem(item, newLevel);
            custom.updateItemLevelLore(item, newLevel);
        }

        player.sendMessage("item new Level + " + createGem.getLevelFromItem(item));


        return true;
    }
}
