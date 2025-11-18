package de.theredno.planc.WebDashboard.DiamondSystem;

import de.theredno.planc.Main;
import de.theredno.planc.WebDashboard.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.UUID;

import static de.theredno.planc.WebDashboard.DiamondSystem.DiamondManager.updateDiamonds;
import static de.theredno.planc.WebDashboard.DiamondSystem.DiamondManager.updateDiamondsWithConn;

public class DiamondTask {
    private static final Main plugin = Main.getInstance();

    public static void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (var conn = plugin.getMysql().getConnection()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        int playerID = DataManager.getPlayerIDWithConn(player.getUniqueId(), conn);
                        if (playerID == -1) continue;

                        long lastLong = DiamondManager.getZinsLong(playerID, conn);
                        long twoMinute = 2 * 60 * 1000;

                        if (System.currentTimeMillis() - lastLong >= twoMinute) {
                            // Update last timestamp
                            try (var ps = conn.prepareStatement("UPDATE zinsTimeTabel SET last = ? WHERE playerID = ?")) {
                                ps.setLong(1, System.currentTimeMillis());
                                ps.setInt(2, playerID);
                                ps.executeUpdate();
                            }

                            int diamonds = DiamondManager.getDiamonds(playerID);
                            int zinsDiamond = (int) Math.round(diamonds * 0.003);

                            // Update diamonds über eine Helper-Methode
                            updateDiamondsWithConn(playerID, zinsDiamond, conn);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }
        }.runTaskTimerAsynchronously(plugin, 1200L, 1200L); // Async Timer direkt, kein Haupt-Thread blockiert
    }
}
