package de.theredno.planc.Items.weapons.special;

import de.theredno.planc.Main;
import de.theredno.planc.util.ItemAbility;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class HookAbility implements ItemAbility {

    private final double range = 50.0;
    private final double hookSpeed = 3;
    private final double pullSpeed = 0.5;
    private final double hitRadius = 1.0;
    int blockAttachDelay = 2;

    @Override
    public void execute(Player player) {
        ItemStack hook = player.getItemInHand();
        if (player.getCooldown(hook) > 0) return;

        Location start = player.getEyeLocation();
        Vector direction = start.getDirection().normalize();

        new BukkitRunnable() {
            Location hookLocation = start.clone();
            Entity hookedEntity = null;
            boolean hookFlying = true;
            boolean hookedToBlock = false;
            Location blockHitLocation = null;
            List<ArmorStand> chain = new ArrayList<>();

            @Override
            public void run() {

                if (!player.isOnline() || player.isDead()) {
                    removeChain(chain);
                    this.cancel();
                    return;
                }


                if (hookFlying) {
                    hookLocation.add(direction.clone().multiply(hookSpeed));
                    hookLocation.getWorld().playSound(hookLocation, Sound.ENTITY_SNOWBALL_THROW, 0.3f, 1.1f);

                    //raytrace = pixel perfect
                    RayTraceResult ray = hookLocation.getWorld().rayTraceBlocks(
                            hookLocation.clone().subtract(direction.clone().multiply(1.0)),
                            direction, hookSpeed, FluidCollisionMode.NEVER, true
                    );

                    if (ray != null && ray.getHitBlock() != null) {


                        blockHitLocation = ray.getHitPosition().toLocation(player.getWorld());
                        blockHitLocation.subtract(direction.clone().multiply(0.2));

                        hookedToBlock = true;
                        hookFlying = false;

                        spawnChain(player.getLocation().clone().add(0, -0.5, 0), blockHitLocation, chain);
                    }

                    for (Entity entity : hookLocation.getWorld().getNearbyEntities(hookLocation, hitRadius, hitRadius, hitRadius)) {
                        if (entity instanceof LivingEntity && entity != player && !(entity instanceof ArmorStand)) {
                            hookedEntity = entity;
                            hookFlying = false;

                            spawnChain(player.getLocation().clone().add(0, -0.5, 0),
                                    hookedEntity.getLocation().clone().add(0, 1, 0), chain);
                            break;
                        }
                    }

                    if (hookLocation.distance(start) > range) {
                        removeChain(chain);
                        this.cancel();
                        return;
                    }

                } else if (hookedEntity != null && hookedEntity.isValid()) {
                    // pull
                    Vector pull = player.getLocation().toVector().add(new Vector(0, 1, 0))
                            .subtract(hookedEntity.getLocation().toVector());
                    pull.normalize().multiply(pullSpeed);
                    hookedEntity.setVelocity(pull);

                    updateChain(player.getLocation().clone().add(0, -0.5, 0),
                            hookedEntity.getLocation().clone().add(0, 1, 0), chain);

                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CHAIN_HIT, 0.4f, 1.2f);

                    if (hookedEntity.getLocation().distance(player.getLocation()) < 2) {
                        removeChain(chain);
                        this.cancel();
                    }

                } else if (hookedToBlock && blockHitLocation != null) {

                    Vector pull = blockHitLocation.toVector().subtract(player.getLocation().toVector());
                    double distance = pull.length();


                    if (distance < 1.5) {
                        removeChain(chain);
                        this.cancel();
                        return;
                    }

                    pull.normalize();

                    double speedMultiplier = 0.5;
                    Vector currentVel = player.getVelocity();



                    Vector newVel = currentVel.add(pull.multiply(speedMultiplier)).multiply(0.9);
                    player.setVelocity(newVel);


                    updateChain(player.getLocation().clone().add(0, -0.5, 0), blockHitLocation, chain);

                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CHAIN_HIT, 0.6f, 1.1f);

                } else if (!hookFlying && !hookedToBlock && hookedEntity == null) {
                if (blockAttachDelay-- <= 0) {
                    removeChain(chain);
                    this.cancel();
                }
            }


        }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);

        player.setCooldown(hook, 200);
    }


    // helpers

    private void spawnChain(Location start, Location end, List<ArmorStand> chain) {
        Vector vec = end.toVector().subtract(start.toVector());
        int steps = Math.max(1, (int) (vec.length() * 2));
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

                ItemStack helmet = new ItemStack(Material.COPPER_CHAIN);
                org.bukkit.inventory.meta.ItemMeta meta = helmet.getItemMeta();

                if (meta != null) {
                    if (finalI == steps - 1) {
                        meta.setItemModel(new NamespacedKey("planc", "hook"));
                    } else {
                        meta.setItemModel(new NamespacedKey("planc", "rope"));
                    }
                    helmet.setItemMeta(meta);
                }

                as.getEquipment().setHelmet(helmet);
            });

            Vector dir = end.toVector().subtract(stand.getLocation().toVector());
            Location fixedLoc = stand.getLocation().clone();
            fixedLoc.setDirection(dir);
            stand.teleport(fixedLoc);
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
            Vector dir = end.toVector().subtract(loc.toVector());
            Location fixedLoc = loc.clone();
            fixedLoc.setDirection(dir);
            stand.teleport(fixedLoc);
        }
    }

    private void removeChain(List<ArmorStand> chain) {
        for (ArmorStand stand : chain) {
            stand.remove();
        }
        chain.clear();
    }

    @Override
    public void remove(Player player) {}
}
