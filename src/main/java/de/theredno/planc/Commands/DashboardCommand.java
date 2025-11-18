package de.theredno.planc.Commands;

import de.theredno.planc.Main;
import de.theredno.planc.WebDashboard.DataManager;
import io.papermc.paper.event.player.AsyncChatCommandDecorateEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.maven.model.MailingList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DashboardCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return true;

        if (!player.hasPermission("OP")) {
            player.sendMessage("Du brauchst OP!");
            return true;
        }

        // Asynchroner DB-Zugriff
        Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            String adminCode = DataManager.authCodeGenerator(32, 32, true);

            try (Connection conn = Main.getInstance().getMysql().getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE `admin` SET `auth_code`= ? LIMIT 1")) {

                ps.setString(1, adminCode);
                ps.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            Component link = Component.text("Admin Dashboard: ", NamedTextColor.BLUE)
                    .append(
                            Component.text("[Click here]", NamedTextColor.AQUA)
                                    .hoverEvent(HoverEvent.showText(Component.text("Go to Admin Dashboard")))
                                    .clickEvent(ClickEvent.openUrl("https://theredno.de/planc/?player=" + adminCode))
                    );

            // Nachricht wieder auf Hauptthread senden
            Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> player.sendMessage(link));
        });

        return true;
    }
}