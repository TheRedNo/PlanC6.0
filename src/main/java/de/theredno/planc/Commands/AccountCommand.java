package de.theredno.planc.Commands;

import de.theredno.planc.Main;
import de.theredno.planc.WebDashboard.DataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return true;

        // Asynchroner Zugriff auf die DB
        Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            int playerID = DataManager.getPlayerID(player.getUniqueId());
            String auth_code = null;

            try (Connection conn = Main.getInstance().getMysql().getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT auth_code FROM `players` WHERE playerID=?")) {

                ps.setInt(1, playerID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        auth_code = rs.getString("auth_code");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (auth_code != null) {
                Component link = Component.text("Your Dashboard: ", NamedTextColor.BLUE)
                        .append(
                                Component.text("[Click here]", NamedTextColor.AQUA)
                                        .hoverEvent(HoverEvent.showText(Component.text("Go to your Dashboard")))
                                        .clickEvent(ClickEvent.openUrl("https://theredno.de/planc/?player=" + auth_code))
                        );

                // Nachricht wieder auf den Hauptthread senden
                Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> player.sendMessage(link));
            }
        });

        return true;
    }
}