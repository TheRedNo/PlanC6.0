package de.theredno.planc.Gems.Abilitys;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class LavaRingAbility {

    private final JavaPlugin plugin;

    public LavaRingAbility(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Erstellt einen Lava-Ring um den Spieler
     * @param player Spieler, um den der Ring entstehen soll
     * @param radius Radius des Rings in Blöcken
     * @param duration Dauer der Lava in Sekunden
     */
    public void createLavaRing(Player player, double radius, int duration) {
        Location center = player.getLocation().clone();
        int y = center.getBlockY();

        // Punkte im Kreis berechnen
        int points = (int) (2 * Math.PI * radius); // Anzahl der Punkte im Kreis

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            int x = center.getBlockX() + (int) Math.round(radius * Math.cos(angle));
            int z = center.getBlockZ() + (int) Math.round(radius * Math.sin(angle));

            Location lavaLoc = new Location(center.getWorld(), x, y, z);

            // Nur setzen, wenn dort Luft ist
            if (lavaLoc.getBlock().getType() == Material.AIR) {
                lavaLoc.getBlock().setType(Material.LAVA);

                // Optional: Lava nach Duration wieder entfernen
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (lavaLoc.getBlock().getType() == Material.LAVA) {
                            lavaLoc.getBlock().setType(Material.AIR);
                        }
                    }
                }.runTaskLater(plugin, duration * 20L);
            }
        }
    }
}