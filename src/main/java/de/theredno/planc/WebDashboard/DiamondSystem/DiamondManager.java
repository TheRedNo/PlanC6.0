package de.theredno.planc.WebDashboard.DiamondSystem;

import de.theredno.planc.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DiamondManager {
    public static long getZinsLong(int playerID) {
        try {
            PreparedStatement ps = Main.getInstance().getMysql().getConnection().prepareStatement("SELECT last FROM zinsTimeTabel WHERE playerID=?");
            ps.setInt(1, playerID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("last");
            }
        } catch (SQLException e) {
            e.printStackTrace(); } return -1;
    }

    public static int getDiamonds(int playerID) {
        try {
            PreparedStatement ps = Main.getInstance().getMysql().getConnection().prepareStatement("SELECT diamonds FROM player_statistic WHERE playerID=?");
            ps.setInt(1, playerID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("diamonds");
            }
        } catch (SQLException e) {
            e.printStackTrace(); } return -1;
    }

    public static void updateDiamonds(int playerID, int dias, Connection conn) {
        try (var ps = conn.prepareStatement("UPDATE player_statistic SET diamonds = diamonds + ? WHERE playerID = ?")) {
            ps.setLong(1, dias);
            ps.setInt(2, playerID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateDiamondsWithConn(int playerID, int amount, Connection conn) throws SQLException {
        try (var ps = conn.prepareStatement(
                "UPDATE player_statistic SET diamonds = diamonds + ? WHERE playerID = ?")) {
            ps.setInt(1, amount);
            ps.setInt(2, playerID);
            ps.executeUpdate();
        }
    }



}
