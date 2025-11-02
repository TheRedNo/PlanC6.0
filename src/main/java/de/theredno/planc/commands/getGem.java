package de.theredno.planc.commands;

import de.theredno.planc.Main;
import de.theredno.planc.manager.GemsConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class getGem implements CommandExecutor {
    private final GemsConfigManager gemsConfigManager;

    public getGem() {
        this.gemsConfigManager = Main.getGemsConfigManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Dieser Befehl kann nur von Spielern ausgeführt werden!");
            return true;
        }


        List<ItemStack> gems = gemsConfigManager.getGems(player);

        if (gems.isEmpty()) {
            player.sendMessage("Du hast keine Gems.");
            return true;
        }

        for (ItemStack gem : gems) {
            if (gem == null) continue;

            String name = gem.hasItemMeta() && gem.getItemMeta().hasDisplayName()
                    ? gem.getItemMeta().getDisplayName()
                    : gem.getType().name();

            player.sendMessage("GEM: " + name);
        }

        return true;
    }
}