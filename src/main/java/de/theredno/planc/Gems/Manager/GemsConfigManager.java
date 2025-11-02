package de.theredno.planc.Gems.Manager;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class GemsConfigManager {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public GemsConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        plugin.saveDefaultConfig();
    }

    @SuppressWarnings("unchecked")
    public List<ItemStack> getGems(Player player) {
        UUID uuid = player.getUniqueId();
        List<?> raw = config.getList("gems." + uuid.toString() + ".owned", new ArrayList<>());
        List<ItemStack> items = new ArrayList<>();
        for (Object o : raw) {
            if (o instanceof ItemStack item) {
                items.add(item);
            }
        }
        return items;
    }

    public void setGems(Player player, List<ItemStack> items) {
        UUID uuid = player.getUniqueId();
        config.set("gems." + uuid.toString() + ".owned", items);
        plugin.saveConfig();
    }

    public void setSelected(Player player, String gemId) {
        UUID uuid = player.getUniqueId();
        config.set("gems." + uuid.toString() + ".selected", gemId);
        plugin.saveConfig();
    }

    public String getSelected(Player player) {
        UUID uuid = player.getUniqueId();
        return config.getString("gems." + uuid.toString() + ".selected");
    }

    public void addGem(Player player, ItemStack item) {
        List<ItemStack> items = getGems(player);
        items.add(item);
        setGems(player, items);
    }

    public void removeGem(Player player, ItemStack item) {
        List<ItemStack> items = getGems(player);
        items.remove(item);
        setGems(player, items);
    }



    @SuppressWarnings("unchecked")
    public boolean updateGemLevel(Player player, String itemId, int newLevel) {
        UUID uuid = player.getUniqueId();
        String path = "gems." + uuid + ".owned";

        Bukkit.getLogger().info("[GemsConfig] Trying to update gemLevel for player=" + player.getName() + " path=" + path);

        List<ItemStack> items = (List<ItemStack>) (List<?>) config.getList(path);
        if (items == null || items.isEmpty()) {
            Bukkit.getLogger().info("[GemsConfig] No gems found at path " + path);
            return false;
        }

        boolean found = false;
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            if (item == null || !item.hasItemMeta()) continue;

            ItemMeta meta = item.getItemMeta();
            if (!meta.hasCustomModelData() && !meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "item_id"), PersistentDataType.STRING)) {
                continue;
            }

            // ID lesen
            String storedId = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "item_id"), PersistentDataType.STRING);
            if (storedId != null && storedId.equalsIgnoreCase(itemId)) {
                // Level setzen
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "item_level"), PersistentDataType.INTEGER, newLevel);
                item.setItemMeta(meta);

                items.set(i, item);
                found = true;
                Bukkit.getLogger().info("[GemsConfig] Updated " + itemId + " to level " + newLevel);
                break;
            }
        }

        if (found) {
            config.set(path, items);
            plugin.saveConfig();
            Bukkit.getLogger().info("[GemsConfig] updateGemLevel: saved successfully for " + player.getName());
            return true;
        } else {
            Bukkit.getLogger().info("[GemsConfig] updateGemLevel: gemId " + itemId + " not found");
            return false;
        }
    }


    @SuppressWarnings("unchecked")
    public List<ItemStack> getGemsFromConfig(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "gems." + uuid + ".owned";

        List<ItemStack> items = (List<ItemStack>) (List<?>) plugin.getConfig().getList(path, new ArrayList<>());
        // Optional: filter null
        items.removeIf(Objects::isNull);
        return items;
    }




}
