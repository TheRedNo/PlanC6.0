package de.theredno.planc.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        List<?> raw = config.getList("gems." + uuid.toString(), new ArrayList<>());
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
        config.set("gems." + uuid.toString(), items);
        plugin.saveConfig();
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

}
