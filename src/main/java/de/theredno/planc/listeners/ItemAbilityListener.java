package de.theredno.planc.listeners;

import de.theredno.planc.util.AbilityTrigger;
import de.theredno.planc.util.ItemAbility;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemAbilityListener implements Listener {

    private final JavaPlugin plugin;
    private final NamespacedKey classKey;
    private final NamespacedKey triggerKey;
    private final NamespacedKey bonusclassKey;
    private final NamespacedKey bonustriggerKey;

    public ItemAbilityListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.classKey = new NamespacedKey(plugin, "item_ability_class");
        this.triggerKey = new NamespacedKey(plugin, "item_ability_trigger");
        this.bonusclassKey = new NamespacedKey(plugin, "item_bonus_ability_class");
        this.bonustriggerKey = new NamespacedKey(plugin, "item_bonus_ability_trigger");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        var meta = item.getItemMeta();
        var container = meta.getPersistentDataContainer();

        // === MAIN ABILITY CHECK ===
        if (container.has(classKey, PersistentDataType.STRING)
                && container.has(triggerKey, PersistentDataType.STRING)) {

            AbilityTrigger mainTrigger = AbilityTrigger.valueOf(
                    container.get(triggerKey, PersistentDataType.STRING)
            );

            if (triggerMatches(mainTrigger, event)) {
                executeAbility(container.get(classKey, PersistentDataType.STRING), event.getPlayer());
                event.setCancelled(true);
            }
        }

        // === BONUS ABILITY CHECK ===
        if (container.has(bonusclassKey, PersistentDataType.STRING)
                && container.has(bonustriggerKey, PersistentDataType.STRING)) {

            AbilityTrigger bonusTrigger = AbilityTrigger.valueOf(
                    container.get(bonustriggerKey, PersistentDataType.STRING)
            );

            if (triggerMatches(bonusTrigger, event)) {
                executeAbility(container.get(bonusclassKey, PersistentDataType.STRING), event.getPlayer());
                event.setCancelled(true);
            }
        }
    }


    private void executeAbility(String className, Player player) {
        try {
            Class<?> clazz = Class.forName(className);
            if (!ItemAbility.class.isAssignableFrom(clazz)) return;

            ItemAbility ability = (ItemAbility) clazz.getDeclaredConstructor().newInstance();
            ability.execute(player);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean triggerMatches(AbilityTrigger trigger, PlayerInteractEvent event) {
        return switch (trigger) {
            case RIGHT_CLICK -> event.getAction().toString().contains("RIGHT");
            case LEFT_CLICK -> event.getAction().toString().contains("LEFT");
            case SHIFT_RIGHT_CLICK -> event.getAction().toString().contains("RIGHT") && event.getPlayer().isSneaking();
            default -> false;
        };
    }
}
