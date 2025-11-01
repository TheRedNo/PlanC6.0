package de.theredno.planc.items.weapons.special;

import de.theredno.planc.Main;
import de.theredno.planc.util.ItemAbility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.List;

public class HookAbility implements ItemAbility {

    private final double range = 20.0;
    private final double hookSpeed = 1.5;
    private final double pullSpeed = 0.5;
    private final double hitRadius = 1.0;

    @Override
    public void execute(Player player) {
        Location start = player.getLocation().clone().add(0, 1.0, 0);
        Vector direction = player.getEyeLocation().getDirection().normalize();

        new BukkitRunnable() {
            Location hookLocation = start.clone();
            Entity hookedEntity = null;
            boolean hookFlying = true;
            List<ArmorStand> chain = new ArrayList<>();

            @Override
            public void run() {
                if (hookFlying) {
                    hookLocation.add(direction.clone().multiply(hookSpeed));
                    hookLocation.getWorld().playSound(hookLocation, Sound.ENTITY_SNOWBALL_THROW, 0.5f, 1f);

                    for (Entity entity : hookLocation.getWorld().getNearbyEntities(hookLocation, hitRadius, hitRadius, hitRadius)) {
                        if (entity instanceof LivingEntity && entity != player) {
                            hookedEntity = entity;
                            hookFlying = false;

                            // Spawn chain armor stands
                            spawnChain(player.getLocation().clone().add(0, -0.5, 0),
                                    hookedEntity.getLocation().clone().add(0, 1, 0), chain);
                            break;
                        }
                    }

                    if (hookLocation.distance(start) > range) this.cancel();

                } else if (hookedEntity != null && hookedEntity.isValid()) {
                    Vector pull = player.getLocation().toVector().add(new Vector(0, 1, 0))
                            .subtract(hookedEntity.getLocation().toVector());
                    pull.normalize().multiply(pullSpeed);
                    hookedEntity.setVelocity(pull);


                    updateChain(player.getLocation().clone().add(0, -0.5, 0),
                            hookedEntity.getLocation().clone().add(0, 1, 0), chain);

                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CHAIN_HIT, 0.5f, 1f);

                    if (hookedEntity.getLocation().distance(player.getLocation()) < 2) {
                        removeChain(chain);
                        this.cancel();
                    }

                } else {
                    removeChain(chain);
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }

    private void spawnChain(Location start, Location end, List<ArmorStand> chain) {
        Vector vec = end.toVector().subtract(start.toVector());
        int steps = (int) (vec.length() * 2);
        Vector step = vec.clone().multiply(1.0 / steps);
        Location loc = start.clone();

        for (int i = 0; i < steps; i++) {
            loc.add(step);

            int finalI = i;
            ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class, as -> {
                as.setInvisible(true);
                as.setMarker(true);
                as.setSmall(true);
                as.setGravity(false);

                ItemStack helmet = new org.bukkit.inventory.ItemStack(Material.COPPER_CHAIN);
                org.bukkit.inventory.meta.ItemMeta meta = helmet.getItemMeta();

                if (meta != null) {
                    if (finalI == steps - 1) {
                        meta.setItemModel(new NamespacedKey("planc", "chain_end"));
                    } else {
                        meta.setItemModel(new NamespacedKey("planc", "chain"));
                    }
                    helmet.setItemMeta(meta);
                }

                as.getEquipment().setHelmet(helmet);
            });

            chain.add(stand);
        }
    }


    private void updateChain(Location start, Location end, List<ArmorStand> chain) {
        if (chain.isEmpty()) return;

        Vector vec = end.toVector().subtract(start.toVector());
        int steps = chain.size();
        Vector step = vec.clone().multiply(1.0 / steps);
        Location loc = start.clone();

        for (ArmorStand stand : chain) {
            loc.add(step);
            stand.teleport(loc);
        }
    }

    private void removeChain(List<ArmorStand> chain) {
        for (ArmorStand stand : chain) {
            stand.remove();
        }
        chain.clear();
    }

    @Override
    public void remove(Player player) {
    }
}
