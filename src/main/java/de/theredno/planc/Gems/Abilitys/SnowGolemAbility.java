package de.theredno.planc.Gems.Abilitys;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SnowGolemAbility {
    private final JavaPlugin plugin;
    private final List<Snowman> activeGolems = new ArrayList<>();

    public SnowGolemAbility(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Spawn Snow Golems, die für den Spieler kämpfen
     * @param owner Spieler, der die Golems beschwört
     * @param amount Anzahl der Golems
     * @param duration Dauer in Sekunden
     */
    public void summonSnowGolems(Player owner, int amount, int duration) {
        Location loc = owner.getLocation();

        for (int i = 0; i < amount; i++) {
            Snowman golem = (Snowman) owner.getWorld().spawnEntity(loc, EntityType.SNOW_GOLEM);
            golem.setCustomName("Golem von " + owner.getName());
            golem.setCustomNameVisible(true);

            // Optional: Besitzer in Metadata speichern
            golem.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "owner"),
                    org.bukkit.persistence.PersistentDataType.STRING,
                    owner.getUniqueId().toString()
            );

            activeGolems.add(golem);

            // AI: Angreife alle Spieler außer dem Besitzer
            golem.setTarget(null); // Standard-Ziel löschen
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (golem.isDead()) {
                        cancel();
                        activeGolems.remove(golem);
                        return;
                    }

                    // Suche Ziel in der Nähe (inklusive Spieler)
                    double searchRadius = 10;
                    Player targetPlayer = null;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.equals(owner)) continue; // Besitzer nicht angreifen
                        if (p.getLocation().distance(golem.getLocation()) <= searchRadius) {
                            targetPlayer = p;
                            break;
                        }
                    }

                    if (targetPlayer != null) {
                        golem.setTarget(targetPlayer); // Angriffsziel setzen
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L); // jede Sekunde prüfen
        }

        // Timer für automatische Entfernung
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Snowman golem : new ArrayList<>(activeGolems)) {
                    if (!golem.isDead()) {
                        golem.remove();
                    }
                    activeGolems.remove(golem);
                }
            }
        }.runTaskLater(plugin, duration * 20L); // Dauer in Ticks
    }
}
