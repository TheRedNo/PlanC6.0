package de.theredno.planc.Gems.Abilitys;

import de.theredno.planc.Main;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class LaserAbility {
    public static void shootLaser(Player player, double range, double damage, PotionEffectType effect, int duration, int amplifier) {
        World world = player.getWorld();
        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().normalize();

        double step = 0.5; // Abstand zwischen Partikeln
        final double[] distance = {0};

        new BukkitRunnable() {
            @Override
            public void run() {
                Location point = eyeLoc.clone().add(direction.clone().multiply(distance[0]));

                // Falls Block getroffen → abbrechen
                if (world.getBlockAt(point).getType().isSolid()) {

                    Color color = Color.YELLOW;
                    world.spawnParticle(Particle.FLASH, point, 1, color);
                    cancel();
                    return;
                }

                // Gelber Laser-Partikel
                world.spawnParticle(Particle.DUST, point, 1,
                        new Particle.DustOptions(Color.YELLOW, 1.2f));

                // Prüfe auf Treffer bei anderen Spielern
                List<Entity> nearby = world.getNearbyEntities(point, 0.5, 0.5, 0.5).stream()
                        .filter(e -> e instanceof Player && !e.equals(player))
                        .toList();

                for (Entity e : nearby) {
                    Player hit = (Player) e;

                    // Effekt anwenden
                    hit.addPotionEffect(new PotionEffect(effect, duration, amplifier, false)); // 5 Sek Weakness II
                    hit.damage(damage, player);

                    world.playSound(hit.getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 0.7f);
                    world.spawnParticle(Particle.CRIT, hit.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);

                    cancel(); // Laser endet beim Treffer
                    return;
                }

                distance[0] += step;
                if (distance[0] > range) cancel(); // Ende der Reichweite erreicht
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L); // Jede Tick 1 Partikel-Schritt
    }
}
