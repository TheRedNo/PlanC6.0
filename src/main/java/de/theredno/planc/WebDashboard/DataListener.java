package de.theredno.planc.WebDashboard;

import de.theredno.planc.Main;
import io.papermc.paper.advancement.AdvancementDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DataListener implements Listener {

    private static final Main plugin = Main.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        String playername = e.getPlayer().getName();
        String code = DataManager.authCodeGenerator(6, 6, false);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = plugin.getMysql().getConnection()) {

                // --- Player exist check ---
                int playerID;
                try (PreparedStatement checkPlayer = conn.prepareStatement("SELECT playerID FROM `players` WHERE uuid=?")) {
                    checkPlayer.setString(1, uuid.toString());
                    try (ResultSet rs = checkPlayer.executeQuery()) {
                        if (rs.next()) {
                            playerID = rs.getInt("playerID");
                        } else {
                            // Insert new player
                            try (PreparedStatement insertPlayer = conn.prepareStatement(
                                    "INSERT INTO `players` (`playername`, `uuid`, `pvp`, `auth_code`) VALUES (?, ?, ?, ?)",
                                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                                insertPlayer.setString(1, playername);
                                insertPlayer.setString(2, uuid.toString());
                                insertPlayer.setBoolean(3, false);
                                insertPlayer.setString(4, code);
                                insertPlayer.executeUpdate();

                                try (ResultSet generatedKeys = insertPlayer.getGeneratedKeys()) {
                                    if (generatedKeys.next()) {
                                        playerID = generatedKeys.getInt(1);
                                    } else {
                                        return; // Fehler beim Einfügen, SpielerID nicht gefunden
                                    }
                                }
                            }
                        }
                    }
                }

                // --- Player Statistic exist check ---
                boolean statsExist = false;
                try (PreparedStatement checkStats = conn.prepareStatement(
                        "SELECT playerID FROM `player_statistic` WHERE playerID=?")) {
                    checkStats.setInt(1, playerID);
                    try (ResultSet rsStats = checkStats.executeQuery()) {
                        if (rsStats.next()) statsExist = true;
                    }
                }

                if (!statsExist) {
                    try (PreparedStatement insertStats = conn.prepareStatement(
                            "INSERT INTO `player_statistic`(`playerID`, `score`, `level`, `xp`, `max_xp`, `playtime`, `diamonds`, `deaths`, `kills`, `placed_blocks`, `breaked_blocks`) " +
                                    "VALUES (?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)")) {
                        insertStats.setInt(1, playerID);
                        insertStats.executeUpdate();
                    }
                }

                // --- ZinsTimeTabel exist check ---
                boolean zinsExist = false;
                try (PreparedStatement checkZins = conn.prepareStatement(
                        "SELECT playerID FROM `zinsTimeTabel` WHERE playerID=?")) {
                    checkZins.setInt(1, playerID);
                    try (ResultSet rsZins = checkZins.executeQuery()) {
                        if (rsZins.next()) zinsExist = true;
                    }
                }

                if (!zinsExist) {
                    try (PreparedStatement insertZins = conn.prepareStatement(
                            "INSERT INTO `zinsTimeTabel`(`playerID`, `last`) VALUES (?, ?)")) {
                        insertZins.setInt(1, playerID);
                        insertZins.setLong(2, System.currentTimeMillis());
                        insertZins.executeUpdate();
                    }
                }

                // --- Advancements ---
                for (String adv : DataManager.getPlayerAdvancements(e.getPlayer())) {
                    if (adv.startsWith("minecraft:recipes/")) continue;
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT IGNORE INTO `advancements`(`playerID`, `advancement`) VALUES (?, ?)")) {
                        ps.setInt(1, playerID);
                        ps.setString(2, adv);
                        ps.executeUpdate();
                    }
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }


    @EventHandler
    public void onAdvancements(PlayerAdvancementDoneEvent e) {
        if (e.getAdvancement().getKey().getKey().startsWith("recipes/")) return;
        AdvancementDisplay display = e.getAdvancement().getDisplay();
        if (display == null) return;

        String friendlyName = PlainTextComponentSerializer.plainText().serialize(display.title());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = plugin.getMysql().getConnection()) {
                // PlayerID hier async abfragen
                int playerID;
                try (PreparedStatement psID = conn.prepareStatement("SELECT playerID FROM `players` WHERE uuid=?")) {
                    psID.setString(1, e.getPlayer().getUniqueId().toString());
                    try (ResultSet rs = psID.executeQuery()) {
                        if (rs.next()) playerID = rs.getInt("playerID");
                        else return; // Spieler nicht gefunden
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT IGNORE INTO `advancements`(`playerID`, `advancement`) VALUES (?, ?)")) {
                    ps.setInt(1, playerID);
                    ps.setString(2, friendlyName);
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        int score = e.getPlayer().getDeathScreenScore();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = plugin.getMysql().getConnection()) {
                int playerID;
                try (PreparedStatement psID = conn.prepareStatement("SELECT playerID FROM `players` WHERE uuid=?")) {
                    psID.setString(1, e.getPlayer().getUniqueId().toString());
                    try (ResultSet rs = psID.executeQuery()) {
                        if (rs.next()) playerID = rs.getInt("playerID");
                        else return;
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE `player_statistic` SET `deaths` = deaths + 1, score = ? WHERE `playerID` = ?")) {
                    ps.setInt(1, score);
                    ps.setInt(2, playerID);
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = plugin.getMysql().getConnection()) {
                int killerID;
                try (PreparedStatement psID = conn.prepareStatement("SELECT playerID FROM `players` WHERE uuid=?")) {
                    psID.setString(1, killer.getUniqueId().toString());
                    try (ResultSet rs = psID.executeQuery()) {
                        if (rs.next()) killerID = rs.getInt("playerID");
                        else return;
                    }
                }

                int victimID;
                try (PreparedStatement psID = conn.prepareStatement("SELECT playerID FROM `players` WHERE uuid=?")) {
                    psID.setString(1, e.getEntity().getUniqueId().toString());
                    try (ResultSet rs = psID.executeQuery()) {
                        if (rs.next()) victimID = rs.getInt("playerID");
                        else return;
                    }
                }

                if (killerID == victimID) return; // Spieler hat sich selbst getötet

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE player_statistic SET kills = kills + 1 WHERE playerID = ?")) {
                    ps.setInt(1, killerID);
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        updateStatAsync(e.getPlayer().getUniqueId(), "placed_blocks");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        updateStatAsync(e.getPlayer().getUniqueId(), "breaked_blocks");
    }


    private void updateStatAsync(UUID uuid, String column) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = plugin.getMysql().getConnection()) {
                int playerID;
                try (PreparedStatement psID = conn.prepareStatement("SELECT playerID FROM `players` WHERE uuid=?")) {
                    psID.setString(1, uuid.toString());
                    try (ResultSet rs = psID.executeQuery()) {
                        if (rs.next()) playerID = rs.getInt("playerID");
                        else return;
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE player_statistic SET " + column + " = " + column + " + 1 WHERE playerID = ?")) {
                    ps.setInt(1, playerID);
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}
