package de.theredno.planc.api;

import de.theredno.planc.manager.CooldownManager;
import de.theredno.planc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class createGem {
    private final String id;
    private final Material material;
    private String displayName;
    private int level = 1;

    private List<String> lore = new ArrayList<>();

    private Consumer<Player> leftClickAbility;
    private Consumer<Player> rightClickAbility;
    private Consumer<Player> shiftRightClickAbility;
    private Consumer<Player> passiveEffect;

    private final Map<Player, Long> leftClickCooldown = new HashMap<>();
    private final Map<Player, Long> rightClickCooldown = new HashMap<>();
    private final Map<Player, Long> shiftRightClickCooldown = new HashMap<>();

    ItemStack item;
    ItemMeta meta;

    public createGem(String id, Material material) {
        this.id = id;
        this.material = material;

        item = new ItemStack(material);
        meta = item.getItemMeta();
    }

    public createGem setDisplayName (String name) {
        this.displayName = name;
        return this;
    }

    public createGem setLevel(int level) {
        this.level = Math.max(1, Math.min(25, level));
        return this;
    }

    public int getLevel() {
        return level;
    }

    public createGem setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public createGem setLeftClickAbility(Consumer<Player> ability) {
        this.leftClickAbility = ability;
        return this;
    }

    public createGem setRightClickAbility(Consumer<Player> ability) {
        this.rightClickAbility = ability;
        return this;
    }

    public createGem setShiftRightClickAbility(Consumer<Player> ability) {
        this.shiftRightClickAbility = ability;
        return this;
    }

    public createGem setPassiveEffect(Consumer<Player> effect) {
        this.passiveEffect = effect;
        return this;
    }

    public createGem setItemModelTexture(String pack, String item) {
        meta.setItemModel(new NamespacedKey(pack, item));
        return this;
    }

    public ItemStack createItem() {

        if (meta != null) {
            meta.setDisplayName(displayName);

            List<String> finalLore = new ArrayList<>();
            finalLore.add("§7Level: §a" + level); // 👈 immer als erste Zeile
            if (lore != null) finalLore.addAll(lore); // Rest der Lore anhängen
            meta.setLore(finalLore);

            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(GemAPI.KEY, GemAPI.TYPE, id);
            data.set(GemAPI.LEVEL_KEY, GemAPI.INTEGER_TYPE, level);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static void setLevelOnItem(ItemStack item, int newLevel) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(GemAPI.LEVEL_KEY, GemAPI.INTEGER_TYPE, Math.max(1, Math.min(25, newLevel)));
        item.setItemMeta(meta);
    }

    public static int getLevelFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 1;
        Integer stored = item.getItemMeta().getPersistentDataContainer()
                .get(GemAPI.LEVEL_KEY, GemAPI.INTEGER_TYPE);
        return stored != null ? stored : 1;
    }
    private int getLevelFromHeldItem(Player p) {
        ItemStack item = p.getInventory().getItemInMainHand();
        return getLevelFromItem(item);
    }

    public static void updateItemLevelLore(ItemStack item, int newLevel) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        // Level im PDC speichern
        data.set(GemAPI.LEVEL_KEY, GemAPI.INTEGER_TYPE, newLevel);

        // Lore updaten
        List<String> oldLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        List<String> newLore = new ArrayList<>();

        // erste Zeile = aktuelles Level
        newLore.add("§7Level: §a" + newLevel);

        // Rest der Lore übernehmen (alles außer alte Level-Zeile)
        if (oldLore != null) {
            for (int i = 1; i < oldLore.size(); i++) {
                newLore.add(oldLore.get(i));
            }
        }

        meta.setLore(newLore);
        item.setItemMeta(meta);
    }



    private long getCooldownTicks(int level) {
        // Basis: 60 Sekunden (60 * 20 = 1200 Ticks)
        // Level 1 → 1200 Ticks (60s)
        // Level 25 → 40 Ticks (2s)
        double factor = 1 - ((level - 1) / 24.0); // 1.0 → 0.0 (weil max Level = 25)
        return Math.max(40, Math.round(1200 * factor)); // min 40 Ticks = 2 Sekunden
    }

    private boolean isOnCooldown(Player p, Map<Player, Long> map, int level) {
        long now = System.currentTimeMillis();
        long cooldown = getCooldownTicks(level) * 50L / 1000L; // Ticks in Sekunden umgerechnet
        if (map.containsKey(p) && now < map.get(p)) {
            long left = (map.get(p) - now) / 1000;
            p.sendMessage("§7Cooldown: §c" + left + "s");
            return true;
        }
        map.put(p, now + cooldown * 1000);
        return false;
    }

    public void triggerLeftClick(Player player) {
        String gemId = getId();
        int level = getLevelFromHeldItem(player);
        long ms = getCooldownTicks(level) * 50L / 1000L * 1000L; // Ticks zu ms

        if (CooldownManager.isOnCooldown(player, gemId, "left")) {
            long left = CooldownManager.getRemaining(player, gemId, "left") / 1000;
            player.sendMessage("§7Cooldown: §c" + left + "s");
            return;
        }

        CooldownManager.setCooldown(player, gemId, "left", ms);
        if (leftClickAbility != null) leftClickAbility.accept(player);
    }

    public void triggerRightClick(Player player) {

        String gemId = getId();
        int level = getLevelFromHeldItem(player);
        long ms = getCooldownTicks(level) * 50L / 1000L * 1000L; // Ticks zu ms

        if (CooldownManager.isOnCooldown(player, gemId, "right")) {
            long right = CooldownManager.getRemaining(player, gemId, "right") / 1000;
            player.sendMessage("§7Cooldown: §c" + right + "s");
            return;
        }

        CooldownManager.setCooldown(player, gemId, "right", ms);
        if (rightClickAbility != null) rightClickAbility.accept(player);
    }

    public void triggerShiftRightClick(Player player) {
        String gemId = getId();
        int level = getLevelFromHeldItem(player);
        long ms = getCooldownTicks(level) * 50L / 1000L * 1000L; // Ticks zu ms

        if (CooldownManager.isOnCooldown(player, gemId, "shiftRight")) {
            long shiftRight = CooldownManager.getRemaining(player, gemId, "shiftRight") / 1000;
            player.sendMessage("§7Cooldown: §c" + shiftRight + "s");
            return;
        }

        CooldownManager.setCooldown(player, gemId, "shiftRight", ms);
        if (shiftRightClickAbility != null) shiftRightClickAbility.accept(player);
    }

    public void triggerPassiveEffect(Player player) {
        if (passiveEffect != null) passiveEffect.accept(player);
    }

    public static boolean isGem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        // Prüfe, ob das Item deinen Gem-Key enthält
        return data.has(GemAPI.KEY, GemAPI.TYPE);
    }



    public static String getGemIdFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null; // Kein Item oder keine Metadaten
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        // Lese die ID aus dem PersistentDataContainer (PDC) aus
        String gemId = data.get(GemAPI.KEY, GemAPI.TYPE);

        return gemId;
    }



    public String getId() {
        return id;
    }


}
