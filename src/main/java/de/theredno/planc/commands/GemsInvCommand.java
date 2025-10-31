package de.theredno.planc.commands;

import de.theredno.planc.menu.createMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GemsInvCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You are not a Player");
            return true;
        }

        Player player = (Player) sender;

        player.openInventory(createMenu.createMainMenu());


        return true;
    }
}
