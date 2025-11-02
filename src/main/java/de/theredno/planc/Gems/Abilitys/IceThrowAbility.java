package de.theredno.planc.Gems.Abilitys;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class IceThrowAbility implements Listener {
    private final JavaPlugin plugin;

    public IceThrowAbility(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void throwIce(Player player, double speed, double damage) {
        World world = player.getWorld();

        // Unsichtbares Projektil starten
        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setVelocity(player.getLocation().getDirection().multiply(speed));
        snowball.setShooter(player);
        snowball.setCustomName("ice_ability_projectile");
        snowball.setCustomNameVisible(false);

        // Partikel-Trail (Eis-Partikel während Flug)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (snowball.isDead() || snowball.isOnGround()) {
                    cancel();
                    return;
                }
                world.spawnParticle(Particle.SNOWFLAKE, snowball.getLocation(), 5, 0.1, 0.1, 0.1, 0.01);
                world.spawnParticle(Particle.CLOUD, snowball.getLocation(), 2, 0, 0, 0, 0.02);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Snowball)) return;
        if (!"ice_ability_projectile".equals(projectile.getCustomName())) return;

        if (projectile.getShooter() instanceof Player player) {
            // Partikel-Explosion beim Aufprall
            projectile.getWorld().spawnParticle(Particle.ITEM_SNOWBALL, projectile.getLocation(), 30, 0.3, 0.3, 0.3, 0.1);
            projectile.getWorld().playSound(projectile.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 1.2f);

            // Schaden an getroffener Entity
            if (event.getHitEntity() instanceof LivingEntity hit) {
                hit.damage(6.0, player); // 6 Schaden (3 Herzen)
                hit.setFreezeTicks(100); // ~5 Sekunden eingefroren
                projectile.getWorld().playSound(hit.getLocation(), Sound.BLOCK_GLASS_STEP, 1f, 0.8f);
            }
        }

        projectile.remove(); // Entfernen nach Treffer
    }
}
