package de.theredno.planc.abilitys;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class StormAbility {

    private final JavaPlugin plugin;

    public StormAbility(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void activate(Player player) {
        player.sendMessage(ChatColor.AQUA + "⚡ You summon the storm!");

        // Bewegung deaktivieren
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGravity(false);
        player.setVelocity(new Vector(0, 0.5, 0));

        final Location startLoc = player.getLocation().clone();
        final double targetY = startLoc.getY() + 15;

        new BukkitRunnable() {
            double phase = 0;
            int ticks = 0;

            @Override
            public void run() {
                ticks++;

                // 1️⃣ Aufsteigen
                if (phase == 0) {
                    if (player.getLocation().getY() < targetY) {
                        player.setVelocity(new Vector(0, 0.3, 0));
                        spawnAuraParticles(player.getLocation());
                    } else {
                        phase = 1;
                        player.setVelocity(new Vector(0, 0, 0));
                    }
                }

                // 2️⃣ Tornado
                else if (phase == 1) {
                    spawnTornado(player.getLocation(), player);

                    // Nach 10 Sekunden → Schockwelle
                    if (ticks >= 200) {
                        phase = 2;
                    }
                }

                // 3️⃣ Shockwave
                else if (phase == 2) {
                    spawnShockwave(player);
                    player.sendMessage(ChatColor.RED + "💥 Shockwave unleashed!");
                    phase = 3;
                }

                // 4️⃣ Abstieg
                else if (phase == 3) {
                    player.setGravity(true);
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    if (player.isOnGround()) {
                        cancel();
                        player.sendMessage(ChatColor.GRAY + "The storm calms...");
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    // 🌪️ Tornado Partikel
    private void spawnTornado(Location loc, Player player) {
        for (double i = 0; i < Math.PI * 2; i += Math.PI / 8) {
            double radius = 5;
            double y = (Math.sin(i * 2) + 1) * 3;
            double x = Math.cos(i) * radius;
            double z = Math.sin(i) * radius;
            loc.getWorld().spawnParticle(Particle.CLOUD, loc.clone().add(x, y, z), 0);
        }

        // Entities anheben
        for (Entity e : loc.getWorld().getNearbyEntities(loc, 10, 10, 10)) {
            if (e instanceof LivingEntity && !e.equals(player)) {
                Vector diff = e.getLocation().toVector().subtract(loc.toVector()).normalize();
                diff.setY(0.8);
                e.setVelocity(diff.multiply(0.5));
            }
        }
    }

    // 💥 Shockwave
    private void spawnShockwave(Player player) {
        Location loc = player.getLocation();
        World world = player.getWorld();

        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.8f);
        world.spawnParticle(Particle.EXPLOSION, loc, 5);
        world.spawnParticle(Particle.FLASH, loc, 10);

        for (Entity e : world.getNearbyEntities(loc, 10, 10, 10)) {
            if (e instanceof LivingEntity && !e.equals(player)) {
                Vector push = e.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(2).setY(1);
                e.setVelocity(push);
            }
        }
    }

    // ✨ Aura während Aufstieg
    private void spawnAuraParticles(Location loc) {
        loc.getWorld().spawnParticle(Particle.ENCHANT, loc, 20, 0.5, 0.5, 0.5, 0.1);
    }
}
