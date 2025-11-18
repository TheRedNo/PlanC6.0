package de.theredno.planc.WebDashboard;

import de.theredno.planc.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class DataTasks {

    private static final Main plugin = Main.getInstance();

    public static void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (var conn = plugin.getMysql().getConnection()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        int playerID = DataManager.getPlayerIDWithConn(player.getUniqueId(), conn);
                        if (playerID == -1) continue;

                        try (var ps = conn.prepareStatement(
                                "UPDATE player_statistic SET playtime = playtime + 1, score = ?, level = ?, xp = ?, max_xp = ? WHERE playerID = ?")) {
                            ps.setInt(1, player.getDeathScreenScore());
                            ps.setInt(2, player.getLevel());
                            ps.setFloat(3, player.getExp());
                            ps.setInt(4, DataManager.getXpToNextLevel(player));
                            ps.setInt(5, playerID);
                            ps.executeUpdate();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 1200L, 1200L);

    }
}
