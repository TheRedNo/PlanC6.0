package de.theredno.planc.commands;

import de.theredno.planc.items.weapons.scythes.Bloodscythe;
import de.theredno.planc.items.weapons.special.Hook;
import de.theredno.planc.Gems.Gems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class giveAllItems implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) sender.sendMessage("Du musst ein Spieler sein!");

        if (!sender.hasPermission("OP")) sender.sendMessage("Du bist nicht OP");

        Player player = (Player) sender;

        player.getInventory().addItem(Bloodscythe.create());
        player.getInventory().addItem(Hook.create());
        player.getInventory().addItem(Gems.strengthGem.createItem());
        player.getInventory().addItem(Gems.healingGem.createItem());
        player.getInventory().addItem(Gems.airgem.createItem());
        player.getInventory().addItem(Gems.firegem.createItem());
        player.getInventory().addItem(Gems.irongem.createItem());
        player.getInventory().addItem(Gems.lightninggem.createItem());
        player.getInventory().addItem(Gems.sandgem.createItem());
        player.getInventory().addItem(Gems.icegem.createItem());
        player.getInventory().addItem(Gems.lavagem.createItem());
        player.getInventory().addItem(Gems.watergem.createItem());


        return true;
    }
}
