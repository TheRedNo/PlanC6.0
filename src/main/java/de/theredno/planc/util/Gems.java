package de.theredno.planc.util;

import de.theredno.planc.Main;
import de.theredno.planc.abilitys.*;
import de.theredno.planc.api.GemAPI;
import de.theredno.planc.api.createGem;
import de.theredno.planc.manager.HealingGemManager;
import org.bukkit.*;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.attribute.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gems {

    public static final createGem strengthGem = new createGem("strength_gem", Material.EMERALD)
            .setDisplayName(ChatColor.GREEN + "Strength Gem")
            .setLevel(1)
            .setLore(List.of("", ChatColor.GREEN + "Abilities:",
                    ChatColor.BLUE + "Leftclick: " + ChatColor.RESET + "Throws nearby players into the air and they take damage",
                    ChatColor.BLUE + "Rightclick: " + ChatColor.RESET + "You get Saturation, Strength and Resistance",
                    ChatColor.BLUE + "Shift-Rightclick: " + ChatColor.RESET + "A shield that protects you from attacks"))
            .setLeftClickAbility(p -> {
                List<Player> inArea = new ArrayList<>();
                Location center = p.getLocation();
                double radius = 10;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.getWorld().equals(center.getWorld())) continue;
                    if (player.getLocation().distance(center) <= radius) {
                        inArea.add(player);
                    }
                }

                if (inArea.contains(p)) {
                    inArea.remove(p);
                }

                for (Player player : inArea) {
                    player.setVelocity(player.getVelocity().add(new Vector(0, 1.5, 0)));
                    player.damage(5);
                }
            })
            .setRightClickAbility(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 120 * 20, 0, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 120 * 20, 0, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 120 * 20, 0, false));
            })
            .setShiftRightClickAbility(p -> {
                new de.theredno.planc.abilitys.SphereShield(Main.getInstance(), p.getLocation(), 6.0, 10).start();
                p.getWorld().playSound(p.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 1f);
                p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 10 * 20, 2, false));
            })
            .setPassiveEffect(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 5 * 20, 1, false));

            });

    public static final createGem healingGem = new createGem("healing_gem", Material.EMERALD)
            .setDisplayName(ChatColor.GREEN + "Healing Gem")
            .setLevel(1)
            .setLore(List.of("", ChatColor.GREEN + "Abilities:",
                    ChatColor.BLUE + "Leftclick: " + ChatColor.RESET + "Heart Lock from the nearest player",
                    ChatColor.BLUE + "Rightclick: " + ChatColor.RESET + "You get Regeneration II",
                    ChatColor.BLUE + "Shift-Rightclick: " + ChatColor.RESET + "Your hearts will be fully healed"))
            .setLeftClickAbility(p -> {
                Location location = p.getLocation();

                double closestDistanceSquared = Double.MAX_VALUE;
                Player nearestPlayer = null;

                // Iteriere über alle Spieler in der Welt der Location
                for (Player other : location.getWorld().getPlayers()) {
                    // Spieler überspringen, der ignoriert werden soll
                    if (other.equals(p)) {
                        continue;
                    }

                    // Verwende distanceSquared() für bessere Performance (keine Wurzelberechnung)
                    double distanceSquared = location.distanceSquared(other.getLocation());

                    if (distanceSquared < closestDistanceSquared) {
                        closestDistanceSquared = distanceSquared;
                        nearestPlayer = other;
                    }
                }

                if (nearestPlayer == null) return;

                double enemieHealth = nearestPlayer.getHealth();

                nearestPlayer.setMaxHealth(enemieHealth);
                HealingGemManager.healthLimitPlayer.put(nearestPlayer, System.currentTimeMillis());
            }).setRightClickAbility(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 1, false));
            }).setShiftRightClickAbility(p -> {
                p.setHealth(20);
            }).setPassiveEffect(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0, false));
            });

    public static final createGem airgem = new createGem("air_gem", Material.EMERALD)
            .setDisplayName(ChatColor.GREEN + "Air Gem")
            .setLevel(1)
            .setLore(List.of("", ChatColor.GREEN + "Abilities:",
                    ChatColor.BLUE + "Leftclick: " + ChatColor.RESET + "All players within a radius of 10 blocks get slowfalling",
                    ChatColor.BLUE + "Rightclick: " + ChatColor.RESET + "All players within a radius of 10 are thrown into the air",
                    ChatColor.BLUE + "Shift-Rightclick: " + ChatColor.RESET + "Makes a cloud you dash in the direction you are looking and become invisible for 2min"))
            .setLeftClickAbility(p -> {
                List<Player> inArea = new ArrayList<>();
                Location center = p.getLocation();
                double radius = 10;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.getWorld().equals(center.getWorld())) continue;
                    if (player.getLocation().distance(center) <= radius) {
                        inArea.add(player);
                    }
                }

                if (inArea.contains(p)) {
                    inArea.remove(p);
                }

                for (Player player : inArea) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 6 * 10, 0, false));
                }
            })
            .setRightClickAbility(p -> {
                List<Player> inArea = new ArrayList<>();
                Location center = p.getLocation();
                double radius = 10;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.getWorld().equals(center.getWorld())) continue;
                    if (player.getLocation().distance(center) <= radius) {
                        inArea.add(player);
                    }
                }

                if (inArea.contains(p)) {
                    inArea.remove(p);
                }

                for (Player player : inArea) {
                    player.setVelocity(player.getVelocity().add(new Vector(0, 2.5, 0)));
                }
            })
            .setShiftRightClickAbility(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60 * 20, 0, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 20, 1, false));

                Vector direction = p.getLocation().getDirection();
                direction.multiply(2);
                p.setVelocity(direction);

                Particle particle = Particle.CLOUD;
                int particleCount = 10000;
                double radius = 2.0;

                p.getWorld().spawnParticle(particle, p.getLocation(), particleCount, radius, radius, radius, 0.0001);
            })
            .setPassiveEffect(p -> {

            });

    public static createGem firegem = new createGem("fire_gem", Material.EMERALD)
            .setDisplayName(ChatColor.GREEN + "Fire Gem")
            .setLevel(1)
            .setLore(List.of("", ChatColor.GREEN + "Abilities:",
                    ChatColor.BLUE + "Leftclick: " + ChatColor.RESET + "Shoots a fireball in the direction of view",
                    ChatColor.BLUE + "Rightclick: " + ChatColor.RESET + "Gives you Fire Resistance, Speed II and Haste II",
                    ChatColor.BLUE + "Shift-Rightclick: " + ChatColor.RESET + "Makes an explosion and deals 5 hearts of damage to players within a 10 block radius"))
            .setLeftClickAbility(p -> {
                Location location = p.getEyeLocation();
                Vector direction = location.getDirection();

                World world = location.getWorld();

                Fireball fireball = world.spawn(location, Fireball.class);

                fireball.setDirection(direction);
                fireball.setVelocity(direction.multiply(1));
                fireball.setYield(5.0f);
                fireball.setIsIncendiary(true);
                fireball.setShooter(p);
            })
            .setRightClickAbility(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10 * 20, 0, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 10 * 20, 1, false));
            })
            .setShiftRightClickAbility(p -> {
                p.getWorld().createExplosion(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), 4.0f, true, true);

                List<Player> inArea = new ArrayList<>();
                Location center = p.getLocation();
                double radius = 10;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.getWorld().equals(center.getWorld())) continue;
                    if (player.getLocation().distance(center) <= radius) {
                        inArea.add(player);
                    }
                }

                if (inArea.contains(p)) {
                    inArea.remove(p);
                }

                for (Player player : inArea) {
                    player.damage(10);
                    p.heal(10);
                }

            })
            .setPassiveEffect(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60 * 20, 0, false));
            });

    public static createGem irongem = new createGem("iron_gem", Material.EMERALD)
            .setDisplayName(ChatColor.GREEN + "Iron Gem")
            .setLevel(1)
            .setLore(List.of("", ChatColor.GREEN + "Abilities:",
                    ChatColor.BLUE + "Leftclick: " + ChatColor.RESET + "Fires a barrage of spectral arrows in a circle shape",
                    ChatColor.BLUE + "Rightclick: " + ChatColor.RESET + "Temporarily grants the user increased absorption and knockback resistance",
                    ChatColor.BLUE + "Shift-Rightclick: " + ChatColor.RESET + "Temporarily increases the player's armour and armour toughness"))
            .setLeftClickAbility(p -> {
                Location center = p.getLocation().add(0, 1.5, 0); // etwas über dem Boden
                World world = center.getWorld();
                int arrowCount = 36; // wie viele Pfeile im Kreis
                double speed = 2.0;  // Fluggeschwindigkeit
                double radius = 1.0; // Startabstand vom Spieler

                for (int i = 0; i < arrowCount; i++) {
                    double angle = (2 * Math.PI / arrowCount) * i;
                    double x = Math.cos(angle);
                    double z = Math.sin(angle);

                    // Position rund um den Spieler
                    Location spawnLoc = center.clone().add(x * radius, 0, z * radius);

                    // Richtung = vom Spieler weg
                    Vector dir = new Vector(x, 0, z).normalize();

                    // Spectral Arrow erschaffen
                    SpectralArrow arrow = world.spawn(spawnLoc, SpectralArrow.class);
                    arrow.setShooter(p);
                    arrow.setVelocity(dir.multiply(speed));
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    arrow.setGravity(true); // false, wenn du willst, dass sie gerade fliegen
                    arrow.setDamage(4.0);

                    // Optional: Effekt beim Schießen
                    world.spawnParticle(Particle.CRIT, spawnLoc, 3, 0, 0, 0, 0);
                }

                world.playSound(center, Sound.ENTITY_ARROW_SHOOT, 1f, 1.2f);
            })
            .setRightClickAbility(p -> {
                AttributeInstance kbAttr = p.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
                if (kbAttr == null) return;

                NamespacedKey kbKey = new NamespacedKey("ultimategems", "temp_knockback_buff");
                AttributeModifier kbMod = new AttributeModifier(kbKey, 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
                kbAttr.addModifier(kbMod);

                // 💛 Absorption (extra Herzen)
                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 10 * 20, 2, false));

                // ⏳ Nach Ablauf entfernen
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        kbAttr.removeModifier(kbMod);
                    }
                }.runTaskLater(Main.getInstance(), 10 * 20);
            })
            .setShiftRightClickAbility(p -> {
                AttributeInstance armorAttr = p.getAttribute(Attribute.ARMOR);
                AttributeInstance toughAttr = p.getAttribute(Attribute.ARMOR_TOUGHNESS);
                if (armorAttr == null || toughAttr == null) return;

                NamespacedKey armorKey = new NamespacedKey("ultimategems", "temp_armor_buff");
                NamespacedKey toughKey = new NamespacedKey("ultimategems", "temp_toughness_buff");

                AttributeModifier armorMod = new AttributeModifier(armorKey, 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
                AttributeModifier toughMod = new AttributeModifier(toughKey, 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);

                armorAttr.addModifier(armorMod);
                toughAttr.addModifier(toughMod);

                // nach Ablauf entfernen
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        armorAttr.removeModifier(armorMod);
                        toughAttr.removeModifier(toughMod);
                    }
                }.runTaskLater(Main.getInstance(), 10 * 20);
            })
            .setPassiveEffect(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 5 * 20, 0, false));
            });

    public static createGem lightninggem = new createGem("lightning_gem", Material.EMERALD)
            .setDisplayName(ChatColor.GREEN + "Lightning Gem")
            .setLevel(1)
            .setLore(List.of("", ChatColor.GREEN + "Abilities:",
                    ChatColor.BLUE + "Leftclick: " + ChatColor.RESET + "Launches the user forward in the direction they are looking at",
                    ChatColor.BLUE + "Rightclick: " + ChatColor.RESET + "Strikes lightning at position the user is aiming at and anything nearby, damaging them",
                    ChatColor.BLUE + "Shift-Rightclick: " + ChatColor.RESET + "Gives you Haste III and Speed IV"))
            .setLeftClickAbility(p -> {
                Vector direction = p.getLocation().getDirection();
                direction.multiply(3.5);
                p.setVelocity(direction);
            })
            .setRightClickAbility(p -> {
                World world = p.getWorld();

                // 1️⃣ Raytrace: Finde Zielposition im Blickfeld
                RayTraceResult result = world.rayTraceBlocks(
                        p.getEyeLocation(),
                        p.getEyeLocation().getDirection(),
                        30
                );

                // Falls kein Block getroffen → weiter in Blickrichtung
                Location targetLoc = (result != null)
                        ? result.getHitPosition().toLocation(world)
                        : p.getEyeLocation().add(p.getLocation().getDirection().multiply(30));

                // 2️⃣ Blitz einschlagen
                world.strikeLightningEffect(targetLoc); // Effekt ohne echten Schaden durch "natürlichen" Blitz

                // 3️⃣ Entities in der Nähe finden
                List<Entity> nearby = world.getNearbyEntities(targetLoc, 15, 15, 15)
                        .stream()
                        .filter(e -> e instanceof LivingEntity)
                        .toList();

                for (Entity e : nearby) {
                    LivingEntity le = (LivingEntity) e;
                    if (le.equals(p)) continue; // sich selbst aussparen

                    le.damage(10, p); // Schaden mit Spieler als Verursacher
                    le.setFireTicks(10 * 20);       // Brennen für 2 Sekunden
                    world.spawnParticle(Particle.ELECTRIC_SPARK, le.getLocation().add(0, 1, 0), 20, 0.3, 0.5, 0.3, 0.1);
                }

                // 4️⃣ Sound / Feedback
                world.playSound(targetLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2f, 1f);
                p.playSound(p.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1f, 1.2f);
            })
            .setShiftRightClickAbility(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 10 * 20, 2, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 3, false));
            })
            .setPassiveEffect(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 0, false));
            });

    public static createGem sandgem = new createGem("sand_gem", Material.EMERALD)
            .setDisplayName(ChatColor.GREEN + "Sand Gem")
            .setLevel(1)
            .setLore(List.of("", ChatColor.GREEN + "Abilities:",
                    ChatColor.BLUE + "Leftclick: " + ChatColor.RESET + "Gives the target player a temporary slowness effect",
                    ChatColor.BLUE + "Rightclick: " + ChatColor.RESET + "Gives the target player a temporary weakness effect",
                    ChatColor.BLUE + "Shift-Rightclick: " + ChatColor.RESET + "Gives the target player a temporary blindess effect"))
            .setLeftClickAbility(p -> {
                LaserAbility.shootLaser(p, 30, 4, PotionEffectType.SLOWNESS, 20 * 20, 1);
            })
            .setRightClickAbility(p -> {
                LaserAbility.shootLaser(p, 30, 4, PotionEffectType.WEAKNESS, 15 * 20, 0);
            })
            .setShiftRightClickAbility(p -> {
                LaserAbility.shootLaser(p, 30, 4, PotionEffectType.BLINDNESS, 20 * 20, 0);
            })
            .setPassiveEffect(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 5 * 20, 0, false));
            });

    public static createGem icegem = new createGem("ice_gem", Material.EMERALD)
            .setDisplayName(ChatColor.GREEN + "Ice Gem")
            .setLevel(1)
            .setLore(List.of("", ChatColor.GREEN + "Abilities:",
                    ChatColor.BLUE + "Leftclick: " + ChatColor.RESET + "Gives the target player temporary slowness effect",
                    ChatColor.BLUE + "Rightclick: " + ChatColor.RESET + "Throws an ice block, dealing damage to anything that gets hit",
                    ChatColor.BLUE + "Shift-Rightclick: " + ChatColor.RESET + "Spawns snow golems on the user to fight for them"))
            .setLeftClickAbility(p -> {
                SlownessAbility.applyTargetedSlowness(p, 15, 7 * 20, 0);
            })
            .setRightClickAbility(p -> {
                new IceThrowAbility(Main.getInstance()).throwIce(p, 1.5, 6.0);
            })
            .setShiftRightClickAbility(p -> {
                SnowGolemAbility golemAbility = new SnowGolemAbility(Main.getInstance());
                golemAbility.summonSnowGolems(p, 3, 60); // 3 Golems für 60 Sekunden
            })
            .setPassiveEffect(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0, false));
            });

    public static createGem lavagem = new createGem("lava_gem", Material.EMERALD)
            .setDisplayName(ChatColor.GREEN + "Lava Gem")
            .setLevel(1)
            .setLore(List.of("", ChatColor.GREEN + "Abilities:",
                    ChatColor.BLUE + "Leftclick: " + ChatColor.RESET + "Provides the user with temporary effect of Fire resistance",
                    ChatColor.BLUE + "Rightclick: " + ChatColor.RESET + "Creates a ring of lava around the user",
                    ChatColor.BLUE + "Shift-Rightclick: " + ChatColor.RESET + "Spawns blazes on the user to fight for them"))
            .setLeftClickAbility(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60 * 20, 0, false));
            })
            .setRightClickAbility(p -> {
                LavaRingAbility lavaRing = new LavaRingAbility(Main.getInstance());
                lavaRing.createLavaRing(p, 5, 120); // Radius 5 Blöcke, 120 Sekunden Lava
            })
            .setShiftRightClickAbility(p -> {
                BlazeAbility blazeAbility = new BlazeAbility(Main.getInstance());
                blazeAbility.summonBlazes(p, 3, 60); // 3 Blazes für 60 Sekunden
            })
            .setPassiveEffect(p -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0, false));
            });

    public static createGem watergem = new createGem("water_gem", Material.EMERALD)
            .setDisplayName(ChatColor.GREEN + "Water Gem")
            .setLevel(1)
            .setLore(List.of("", ChatColor.GREEN + "Abilities:",
                    ChatColor.BLUE + "Leftclick: " + ChatColor.RESET + "Makes the nearest player suffocate",
                    ChatColor.BLUE + "Rightclick: " + ChatColor.RESET + "Boost you in the Water like a trident",
                    ChatColor.BLUE + "Shift-Rightclick: " + ChatColor.RESET + "Create a temporary water cube around you"))
            .setLeftClickAbility(p -> {

                Location location = p.getLocation();

                double closestDistanceSquared = Double.MAX_VALUE;
                Player nearestPlayer = null;

                // Iteriere über alle Spieler in der Welt der Location
                for (Player other : location.getWorld().getPlayers()) {
                    // Spieler überspringen, der ignoriert werden soll
                    if (other.equals(p)) {
                        continue;
                    }

                    // Verwende distanceSquared() für bessere Performance (keine Wurzelberechnung)
                    double distanceSquared = location.distanceSquared(other.getLocation());

                    if (distanceSquared < closestDistanceSquared) {
                        closestDistanceSquared = distanceSquared;
                        nearestPlayer = other;
                    }
                }


                final Player playerNearest = nearestPlayer;

                BukkitRunnable task = new BukkitRunnable() {
                    int ticks = 0; // 1 Sekunde = 20 Ticks

                    @Override
                    public void run() {
                        if (ticks >= 200) { // 200 Ticks = 10 Sekunden
                            playerNearest.setRemainingAir(p.getMaximumAir());
                            playerNearest.sendMessage("😮‍💨 Du kannst wieder atmen!");
                            cancel();
                            return;
                        }

                        if (playerNearest == null) return;

                        // Setze Luft auf 0 = Ertrinken
                        playerNearest.setRemainingAir(0);
                        playerNearest.damage(1.0);
                        ticks += 20;
                    }
                };

                task.runTaskTimer(Main.getInstance(), 0L, 20L);
            })
            .setRightClickAbility(p -> {
                WaterBoostAbility.boostInWater(p, 3.5);
            })
            .setShiftRightClickAbility(p -> {
                WaterCubeAbility waterCube = new WaterCubeAbility(Main.getInstance());
                waterCube.createWaterCube(p, 3, 10); // 3x3x3 Würfel, 10 Sekunden
            })
            .setPassiveEffect(p -> {
                if (p.getLocation().getBlock().getType().equals(Material.WATER)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0, false));
                }
            });


    public static void createGems() {
        GemAPI.register(strengthGem);
        GemAPI.register(healingGem);
        GemAPI.register(airgem);
        GemAPI.register(firegem);
        GemAPI.register(irongem);
        GemAPI.register(lightninggem);
        GemAPI.register(sandgem);
        GemAPI.register(icegem);
        GemAPI.register(lavagem);
        GemAPI.register(watergem);
    }

    private static final Map<String, ItemStack> gemMap = new HashMap<>();


    static {
        gemMap.put("strength_gem", strengthGem.createItem());
        gemMap.put("healing_gem", healingGem.createItem());
        gemMap.put("air_gem", airgem.createItem());
        gemMap.put("fire_gem", firegem.createItem());
        gemMap.put("iron_gem", irongem.createItem());
        gemMap.put("lightning_gem", lightninggem.createItem());
        gemMap.put("sand_gem", sandgem.createItem());
        gemMap.put("ice_gem", icegem.createItem());
        gemMap.put("lava_gem", lavagem.createItem());
        gemMap.put("water_gem", watergem.createItem());
    }

    public static boolean exists(String name) {
        return gemMap.containsKey(name.toLowerCase());
    }

    public static ItemStack getGem(String name) {
        return gemMap.get(name.toLowerCase());
    }

    public static List<String> getAllGemNames() {
        return gemMap.keySet().stream().toList();
    }

    public static List<ItemStack> getAllGems() {
        return new ArrayList<>(gemMap.values());
    }


    /*

    ItemStack item = player.getInventory().getItemInMainHand();
    CustomItem custom = CustomItemAPI.getFromItem(item);
    if (custom != null) {
        int newLevel = custom.getLevelFromItem(item) + 1;
        custom.setLevelOnItem(item, newLevel);
        player.sendMessage("§aDein Item ist jetzt Level " + newLevel + "!");
    }


    */
}
