package de.theredno.planc.commands;

import de.theredno.planc.Main;
import de.theredno.planc.manager.GemsConfigManager;
import de.theredno.planc.util.Gems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class addGem implements CommandExecutor, TabCompleter {
    private final GemsConfigManager gemsConfigManager;

    public addGem(Main plugin) {
        this.gemsConfigManager = plugin.getGemsManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Dieser Befehl kann nur von Spielern ausgeführt werden!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("Bitte gib einen Gem-Namen an!");
            return false;
        }

        String gemName = args[0].toLowerCase(Locale.ROOT);

        // Prüfen, ob es einen gültigen Gem gibt
        if (!Gems.exists(gemName)) {
            player.sendMessage("Ungültiger Gem: " + gemName);
            return true;
        }

        // ItemStack erstellen und hinzufügen
        gemsConfigManager.addGem(player, Gems.getGem(gemName));
        player.sendMessage("Du hast einen " + gemName + " erhalten!");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>(Gems.getAllGemNames()); // Liste aller Gems

        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return suggestions.stream()
                    .filter(name -> name.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }

        return List.of(); // Keine weiteren Argumente
    }
}
