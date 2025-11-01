package de.theredno.planc.commands;

import de.theredno.planc.Main;
import de.theredno.planc.TpaSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.Buffer;

public class TpaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length < 1) {
            player.sendMessage("!!!");
            return true;
        }

        switch (args[0]) {
            case "send":
                if (args.length < 2) {
                    player.sendMessage("&&&");
                }

                if (Bukkit.getOnlinePlayers().contains(args[1])) {
                    TpaSystem.sendTpaRequest(player, Bukkit.getPlayer(args[1]));
                }

            default:
                player.sendMessage(":::");
        }


        return true;
    }
}
