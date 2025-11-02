package de.theredno.planc.Gems.Abilitys;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WaterBoostAbility {

    /**
     * Schub in Blickrichtung, wenn der Spieler im Wasser ist
     * @param player Spieler, der geboostet wird
     * @param strength Stärke des Boosts (z.B. 1.5 = stark)
     */
    public static void boostInWater(Player player, double strength) {
        Location loc = player.getLocation();

        // Prüfen, ob Spieler im Wasser ist
        if (loc.getBlock().getType() == Material.WATER || loc.getBlock().getType() == Material.BUBBLE_COLUMN) {
            Vector dir = loc.getDirection().normalize().multiply(strength);

            // Optional: leicht nach oben boosten
            dir.setY(Math.max(dir.getY(), 0.3));

            // Velocity setzen
            player.setVelocity(dir);

            // Optional: Sound / Partikel
            player.getWorld().spawnParticle(org.bukkit.Particle.BUBBLE_COLUMN_UP, player.getLocation(), 10, 0.3, 0.3, 0.3, 0.1);
            player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ITEM_TRIDENT_RIPTIDE_1, 1f, 1.2f);
        }
    }
}