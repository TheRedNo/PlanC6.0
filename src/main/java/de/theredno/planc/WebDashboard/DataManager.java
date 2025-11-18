package de.theredno.planc.WebDashboard;

import de.theredno.planc.Main;
import io.papermc.paper.advancement.AdvancementDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class DataManager {

    private static final SecureRandom RNG = new SecureRandom();
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";

    public static String authCodeGenerator(int lettersCount, int digitsCount, boolean uppercase) {
        List<Character> pool = new ArrayList<>(lettersCount + digitsCount);

        for (int i = 0; i < lettersCount; i++) {
            char c = LETTERS.charAt(RNG.nextInt(LETTERS.length()));
            if (uppercase) c = Character.toUpperCase(c);
            pool.add(c);
        }

        for (int i = 0; i < digitsCount; i++) {
            pool.add(DIGITS.charAt(RNG.nextInt(DIGITS.length())));
        }

        Collections.shuffle(pool, RNG);

        StringBuilder sb = new StringBuilder(pool.size());
        for (char ch : pool) sb.append(ch);
        return sb.toString();
    }

    public static int getPlayerID(UUID uuid) {
        try {
            PreparedStatement ps = Main.getInstance().getMysql().getConnection().prepareStatement("SELECT playerID FROM players WHERE uuid=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("playerID");
            }
        } catch (SQLException e) {
            e.printStackTrace(); } return -1;
    }

    public static int getPlayerIDWithConn(UUID uuid, Connection conn) throws SQLException {
        try (var ps = conn.prepareStatement("SELECT playerID FROM players WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("playerID");
            }
        }
        return -1;
    }



    public static int getPlayerIDSync(UUID uuid) {
        try (var conn = Main.getInstance().getMysql().getConnection();
             var ps = conn.prepareStatement("SELECT playerID FROM `players` WHERE uuid=?")) {

            ps.setString(1, uuid.toString());
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("playerID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }



    public static List<String> getPlayerAdvancements(Player player) {
        List<String> completed = new ArrayList<>();

        for (@NotNull Iterator<Advancement> it = Bukkit.getServer().advancementIterator(); it.hasNext(); ) {
            Advancement adv = it.next();
            AdvancementDisplay display = adv.getDisplay();
            AdvancementProgress progress = player.getAdvancementProgress(adv);
            if (progress.isDone()) {
                if (display != null) {
                    Component titelComponent = display.title();
                    String friendlyName = PlainTextComponentSerializer.plainText().serialize(titelComponent);

                    completed.add(friendlyName);
                }
            }
        }
        return completed;
    }

    public static int getXpToNextLevel(Player player) {
        int level = player.getLevel();
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }

    public interface PlayerIDCallback {
        void onResult(int playerID);
    }

    public interface PVPCallback {
        void onResult(boolean pvp);
    }


}
