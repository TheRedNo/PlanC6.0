package de.theredno.planc.Gems.Abilitys;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WaterCubeAbility {

    private final JavaPlugin plugin;

    public WaterCubeAbility(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Erstellt einen temporären Würfel aus Wasser um den Spieler
     * @param player Spieler, um den Würfel zu erzeugen
     * @param size Kantenlänge des Würfels (ungerade Zahl empfehlenswert, z.B. 3 oder 5)
     * @param duration Dauer in Sekunden
     */
    public void createWaterCube(Player player, int size, int duration) {
        Location center = player.getLocation().clone();
        int half = size / 2;

        for (int x = -half; x <= half; x++) {
            for (int y = -half; y <= half; y++) {
                for (int z = -half; z <= half; z++) {

                    Location blockLoc = center.clone().add(x, y, z);
                    Material original = blockLoc.getBlock().getType();

                    // Nur Luft oder andere durchlässige Blöcke ersetzen
                    if (original.isAir() || original == Material.SNOW || original == Material.TALL_GRASS) {
                        blockLoc.getBlock().setType(Material.WATER);

                        // Entfernen nach duration
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (blockLoc.getBlock().getType() == Material.WATER) {
                                    blockLoc.getBlock().setType(Material.AIR);
                                }
                            }
                        }.runTaskLater(plugin, duration * 20L);
                    }
                }
            }
        }
    }
}