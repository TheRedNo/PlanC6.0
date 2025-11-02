package de.theredno.planc.Items.weapons.scythes;

import de.theredno.planc.Main;
import de.theredno.planc.util.ItemAbility;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BloodscytheAbility implements ItemAbility {

    @Override
    public void execute(Player player) {
        NamespacedKey hitsKey = new NamespacedKey(Main.getInstance(), "hits");

        ItemStack weapon = player.getItemInHand();
        ItemMeta meta = weapon.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        int hits = data.getOrDefault(hitsKey, PersistentDataType.INTEGER, 0);
        if (hits != 5) return;

        World world = player.getWorld();
        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection().normalize();

        double radius = 2.5; // size of half-circle
        double travelDistance = 10.0; // distance traveled
        double stepSize = 1.0; // move per tick
        int particlePoints = 20; // particles per arc
        double damageAmount = 6; // temporary vary with entchantment etc

        List<LivingEntity> hitEntities = new ArrayList<>();

        Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();
        Vector up = right.clone().crossProduct(direction).normalize();
        new BukkitRunnable() {
            double traveled = 0;

            @Override
            public void run() {
                if (traveled >= travelDistance || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                Location center = eye.clone().add(direction.clone().multiply(traveled + 1.0));

                for (int i = 0; i <= particlePoints; i++) {
                    double angle = Math.PI * i / particlePoints;

                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;

                    Vector offset = right.clone().multiply(x).add(direction.clone().multiply(z));
                    Location point = center.clone().add(offset);

                    world.spawnParticle(Particle.DUST, point, 2, new Particle.DustOptions(Color.fromRGB(139, 0, 0), 1.5f));

                    for (Entity e : world.getNearbyEntities(point, 0.6, 0.6, 0.6)) {
                        if (e instanceof LivingEntity mob && mob != player) {
                            if (!hitEntities.contains(mob)) {
                                mob.damage(damageAmount, player);
                                hitEntities.add(mob);

                                world.playSound(mob.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
                            }
                        }
                    }
                }

                traveled += stepSize;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);


        meta.setDisplayName("§4§lBlood Scythe ⬦⬦⬦⬦⬦");

        data.set(hitsKey, PersistentDataType.INTEGER, 0);
        weapon.setItemMeta(meta);
    }



    @Override
    public void remove(Player player) {

    }
}
