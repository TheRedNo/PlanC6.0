package de.theredno.planc.Gems.Abilitys;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class SlownessAbility {
    /**
     * Verlangsamt den Spieler, auf den der aktive Spieler schaut
     * @param player Der Spieler, der die Ability benutzt
     * @param range Reichweite der Ability in Blöcken
     * @param duration Dauer des Slowness-Effekts in Ticks
     * @param amplifier Stufe des Slowness-Effekts (0 = Slowness I)
     */
    public static void applyTargetedSlowness(Player player, double range, int duration, int amplifier) {
        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection();

        // Raytrace auf Entities
        RayTraceResult result = player.getWorld().rayTraceEntities(
                eyeLoc,
                direction,
                range,
                0.5, // Treffer-Offset Radius
                entity -> entity instanceof Player && !entity.equals(player) // Nur andere Spieler
        );

        if (result != null && result.getHitEntity() instanceof Player target) {
            // Slowness anwenden
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, amplifier));
            player.sendMessage("§aDu hast §e" + target.getName() + " §averlangsamt!");
            target.sendMessage("§cDu wurdest verlangsamt!");
        } else {
            player.sendMessage("§7Kein Spieler in Reichweite oder Blickrichtung gefunden!");
        }
    }
}
