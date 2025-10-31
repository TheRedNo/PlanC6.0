package de.theredno.planc.api;

import de.theredno.planc.manager.CooldownManager;
import de.theredno.planc.manager.HealingGemManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GemAPI implements Listener {
    private static final Map<String, createGem> ITEMS = new HashMap<>();
    public static NamespacedKey KEY;
    public static final PersistentDataType<String, String> TYPE = PersistentDataType.STRING;
    public static final PersistentDataType<Integer, Integer> INTEGER_TYPE = PersistentDataType.INTEGER;
    public static NamespacedKey LEVEL_KEY;


    public GemAPI(JavaPlugin plugin) {
        KEY = new NamespacedKey(plugin, "item_id");
        LEVEL_KEY = new NamespacedKey(plugin, "item_level");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                for (ItemStack item : player.getInventory().getContents()) {
                    createGem gem = getFromItem(item);
                    if (gem != null) {
                        gem.triggerPassiveEffect(player);
                    }
                }
            }

            for (Map.Entry<Player, Long> entry : HealingGemManager.healthLimitPlayer.entrySet()) {
                Player player = entry.getKey();
                Long time = entry.getValue();

                if (System.currentTimeMillis() - time >= (5*1000)) {
                    player.setMaxHealth(20);
                    HealingGemManager.healthLimitPlayer.remove(player);
                }
            }

            //Cooldown Actionbar
            for (Player player : Bukkit.getOnlinePlayers()) {
                List<ItemStack> gems = new ArrayList<>();
                for (ItemStack item : player.getInventory().getContents()) {
                    if (createGem.isGem(item)) {
                        gems.add(item);
                    }
                }

                if (gems.isEmpty()) return;

                String left = ChatColor.RED + "ERROR";
                String right = ChatColor.RED + "ERROR";
                String shiftRight = ChatColor.RED + "ERROR";

                String separation = ChatColor.GREEN + " | ";

                for (ItemStack gem : gems) {

                    long leftTime = CooldownManager.getRemaining(player, createGem.getGemIdFromItem(gem), "left") / 1000;
                    left = (leftTime == 0) ? ChatColor.DARK_GREEN + "Ready" : ChatColor.GRAY + String.valueOf(leftTime);

                    long rightTime = CooldownManager.getRemaining(player, createGem.getGemIdFromItem(gem), "right") / 1000;
                    right = (rightTime == 0) ? ChatColor.DARK_GREEN + "Ready" : ChatColor.GRAY + String.valueOf(rightTime);

                    long shiftRightTime = CooldownManager.getRemaining(player, createGem.getGemIdFromItem(gem), "shiftRight") / 1000;
                    shiftRight = (shiftRightTime == 0) ? ChatColor.DARK_GREEN + "Ready" : ChatColor.GRAY + String.valueOf(shiftRightTime);
                }

                player.sendActionBar(new TextComponent(left + separation + right + separation + shiftRight));
            }

        }, 20L, 20L);
    }

    public static void register(createGem item) {
        ITEMS.put(item.getId(), item);
    }

    public static createGem getFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        var meta = item.getItemMeta();
        var id = meta.getPersistentDataContainer().get(KEY, TYPE);
        if (id == null) return null;
        return ITEMS.get(id);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        var player = e.getPlayer();
        var item = e.getItem();
        var custom = getFromItem(item);
        if (custom == null) return;

        switch (e.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                custom.triggerLeftClick(player);
                break;

            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                if (player.isSneaking())
                    custom.triggerShiftRightClick(player);
                else
                    custom.triggerRightClick(player);
                break;
        }
    }
}
