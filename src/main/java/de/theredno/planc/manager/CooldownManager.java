package de.theredno.planc.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private static final Map<UUID, Map<String, Map<String, Long>>> cooldowns = new HashMap<>();

    public static boolean isOnCooldown(Player player, String gemId, String abilityType) {
        Map<String, Map<String, Long>> playerData = cooldowns.get(player.getUniqueId());
        if (playerData == null) return false;

        Map<String, Long> gemCooldowns = playerData.get(gemId);
        if (gemCooldowns == null) return false;

        Long expire = gemCooldowns.get(abilityType);
        return expire != null && expire > System.currentTimeMillis();
    }

    public static long getRemaining(Player player, String gemId, String abilityType) {
        Map<String, Map<String, Long>> playerData = cooldowns.get(player.getUniqueId());
        if (playerData == null) return 0;

        Map<String, Long> gemCooldowns = playerData.get(gemId);
        if (gemCooldowns == null) return 0;

        Long expire = gemCooldowns.get(abilityType);
        if (expire == null) return 0;

        return Math.max(0, expire - System.currentTimeMillis());
    }

    public static void setCooldown(Player player, String gemId, String abilityType, long ms) {
        cooldowns
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .computeIfAbsent(gemId, k -> new HashMap<>())
                .put(abilityType, System.currentTimeMillis() + ms);
    }

}
