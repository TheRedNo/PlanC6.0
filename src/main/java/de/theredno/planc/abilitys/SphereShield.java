package de.theredno.planc.abilitys;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class SphereShield implements Listener {
    private final JavaPlugin plugin;
    private final Location center;
    private final double radius;
    private final long durationTicks; // Dauer in Ticks
    private BukkitTask task;
    private final Map<UUID, Boolean> wasInside = new HashMap<>(); // vorheriger Inside-Status

    /**
     * @param plugin dein Plugin
     * @param center Zentrum der Kugel (Location)
     * @param radius Radius in Blocks (z.B. 4)
     * @param durationSeconds Dauer in Sekunden (z.B. 10)
     */
    public SphereShield(JavaPlugin plugin, Location center, double radius, int durationSeconds) {
        this.plugin = plugin;
        this.center = center.clone();
        this.radius = radius;
        this.durationTicks = durationSeconds * 20L;
    }

    /** Startet das Schild */
    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        final long start = plugin.getServer().getCurrentTick();
        // Haupt-Task: Partikel + Kontrolle
        this.task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            // 1) Partikel: zufällige Punkte auf Kugeloberfläche
            spawnSphereParticles();

            // 2) Entities im Bereich prüfen (radius + 1 für Sicherheit)
            for (Entity e : center.getWorld().getNearbyEntities(center, radius + 1, radius + 1, radius + 1)) {
                // ignore the shield's world mismatch (shouldn't happen) and invisible entities? We handle all.
                double distSq = e.getLocation().distanceSquared(center);
                boolean nowInside = distSq <= (radius * radius);
                UUID id = e.getUniqueId();
                boolean prevInside = wasInside.getOrDefault(id, nowInside); // default: current state

                // Wenn Entity kommt VON AUSSEN -> versucht einzudringen: prevInside == false && nowInside == true
                if (!prevInside && nowInside) {
                    // Block entry
                    handleEntryAttempt(e);
                    // mark as outside (so next tick still considered outside unless actually inside)
                    wasInside.put(id, false);
                } else {
                    // Update status (wenn entity ist momentan innen, merken wir das)
                    wasInside.put(id, nowInside);
                }

                // Falls entity ist innen und will raus, nichts tun (erlaubt)
            }

            // 3) Dauer beenden?
            long elapsed = plugin.getServer().getCurrentTick() - start;
            if (elapsed >= durationTicks) {
                cancel();
            }
        }, 0L, 1L);
    }

    /** Stoppt das Schild */
    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        // aufräumen
        wasInside.clear();
        // Listener entfernen: einfacher Weg ist kein explizites Unregister (stattdessen Listener prüft task==null)
    }

    /** Partikel auf der Oberfläche (zufällige Punkte, pro Tick ~30) */
    private void spawnSphereParticles() {
        // EndRod wirkt weiß und schön
        int points = 30;
        for (int i = 0; i < points; i++) {
            double u = Math.random();
            double v = Math.random();
            double theta = 2 * Math.PI * u;
            double phi = Math.acos(2 * v - 1);
            double x = Math.sin(phi) * Math.cos(theta);
            double y = Math.sin(phi) * Math.sin(theta);
            double z = Math.cos(phi);
            Vector dir = new Vector(x, z, y).normalize(); // permutiert ein bisschen für Variation
            Location spawn = center.clone().add(dir.multiply(radius));
            center.getWorld().spawnParticle(Particle.END_ROD, spawn, 1, 0, 0, 0, 0.0);
        }
    }

    /** Wird aufgerufen, wenn ein Entity versucht einzudringen */
    private void handleEntryAttempt(Entity e) {
        // Falls Projectile --> entfernen
        if (e instanceof Projectile) {
            e.remove();
            // optional: kleine Explosion-Particle
            center.getWorld().spawnParticle(Particle.SMOKE, e.getLocation(), 6, 0.2, 0.2, 0.2, 0.02);
            return;
        }

        // Spieler: zurückstoßen und Nachricht / sound
        if (e instanceof Player) {
            Player p = (Player) e;
            Vector away = getAwayVector(p.getLocation());
            p.setVelocity(away.multiply(0.6)); // push back
            p.sendMessage("§cDu kannst nicht in die geschützte Kugel eindringen!");
            return;
        }

        // Mobs & andere Entities: zurückstoßen via velocity (so können sie raus, aber nicht rein)
        // LivingEntity hat bessere Wirkung (knockback)
        Vector away = getAwayVector(e.getLocation());
        if (e instanceof LivingEntity) {
            e.setVelocity(away.multiply(0.6));
        } else {
            // für sonstige Entitäten teleport small step back (fallback)
            Location back = e.getLocation().add(away.multiply(0.6));
            e.teleport(back);
        }
    }

    /** Vektor vom Zentrum zur Position (normalisiert) */
    private Vector getAwayVector(Location loc) {
        Vector v = loc.toVector().subtract(center.toVector());
        if (v.lengthSquared() == 0) {
            // wenn genau im Zentrum, einfach nach oben schieben
            return new Vector(0, 1, 0).normalize();
        }
        return v.normalize();
    }

    /** Schützt gegen Teleport-Bypass: wenn jemand außerhalb ist und teleportiert INS Zentrum, cancel */
    @EventHandler
    public void onEntityTeleport(EntityTeleportEvent ev) {
        if (task == null) return; // Schild nicht aktiv
        Location to = ev.getTo();
        Location from = ev.getFrom();
        if (to == null || from == null) return;
        boolean fromInside = from.distanceSquared(center) <= radius * radius;
        boolean toInside = to.distanceSquared(center) <= radius * radius;
        if (!fromInside && toInside) {
            // verhindert Teleports ins Innere
            ev.setCancelled(true);
            Entity e = ev.getEntity();
            // push back a bit / notify player
            if (e instanceof Player) {
                ((Player) e).sendMessage("§cTeleportieren in die geschützte Kugel ist nicht erlaubt.");
            }
        }
    }
}
