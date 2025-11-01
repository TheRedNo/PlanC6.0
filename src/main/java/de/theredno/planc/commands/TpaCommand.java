package de.theredno.planc.commands;

import de.theredno.planc.Main;
import de.theredno.planc.TpaSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
            player.sendMessage("Tpa costs: 1 Enderpearl and 10 Levels");
            return true;
        }

        switch (args[0]) {
            case "send":
                if (args.length < 2) {
                    player.sendMessage("&&&");
                    break;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target != null) {
                    TpaSystem.sendTpaRequest(player, target);
                } else {
                    player.sendMessage("Spieler nicht gefunden!");
                }
                break;

            case "accept":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.GRAY + "/tpa accept <Player>");
                    break;
                }

                TpaSystem.accept(player, Bukkit.getPlayerExact(args[1]));
                break;

            case "deny":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.GRAY + "/tpa deny <Player>");
                    break;
                }

                TpaSystem.deny(player, Bukkit.getPlayerExact(args[1]));
                break;

            case "cancel":
                TpaSystem.cancel(player);
                break;


            default:
                player.sendMessage("Tpa costs: 1 Enderpearl and 10 Levels");
                break;
        }

        return true;
    }
}
