package de.theredno.planc.Gems.Abilitys;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BlazeAbility {

    private final JavaPlugin plugin;
    private final List<Blaze> activeBlazes = new ArrayList<>();

    public BlazeAbility(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Spawn Blazes, die für den Spieler kämpfen
     * @param owner Spieler, der die Blazes beschwört
     * @param amount Anzahl der Blazes
     * @param duration Dauer in Sekunden
     */
    public void summonBlazes(Player owner, int amount, int duration) {
        Location loc = owner.getLocation();

        for (int i = 0; i < amount; i++) {
            Blaze blaze = (Blaze) owner.getWorld().spawnEntity(loc, org.bukkit.entity.EntityType.BLAZE);
            blaze.setCustomName("Blaze von " + owner.getName());
            blaze.setCustomNameVisible(true);

            // Besitzer in Metadata speichern
            blaze.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "owner"),
                    org.bukkit.persistence.PersistentDataType.STRING,
                    owner.getUniqueId().toString()
            );

            activeBlazes.add(blaze);

            // AI: Angreife alle Spieler außer dem Besitzer
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (blaze.isDead()) {
                        cancel();
                        activeBlazes.remove(blaze);
                        return;
                    }

                    // Suche Ziel in der Nähe (inklusive Spieler)
                    double searchRadius = 10;
                    Player targetPlayer = null;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.equals(owner)) continue; // Besitzer nicht angreifen
                        if (p.getLocation().distance(blaze.getLocation()) <= searchRadius) {
                            targetPlayer = p;
                            break;
                        }
                    }

                    if (targetPlayer != null) {
                        blaze.setTarget(targetPlayer); // Angriffsziel setzen
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L); // jede Sekunde prüfen
        }

        // Timer für automatische Entfernung
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Blaze blaze : new ArrayList<>(activeBlazes)) {
                    if (!blaze.isDead()) {
                        blaze.remove();
                    }
                    activeBlazes.remove(blaze);
                }
            }
        }.runTaskLater(plugin, duration * 20L); // Dauer in Ticks
    }
}
