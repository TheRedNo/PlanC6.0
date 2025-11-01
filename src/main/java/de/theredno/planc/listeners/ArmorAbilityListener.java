package de.theredno.planc.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import de.theredno.planc.util.AbilityTrigger;
import de.theredno.planc.util.ItemAbility;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorAbilityListener implements Listener {

    private final JavaPlugin plugin;
    private final NamespacedKey classKey;
    private final NamespacedKey triggerKey;
    private final NamespacedKey bonusclassKey;
    private final NamespacedKey bonustriggerKey;

    private final Map<UUID, ItemAbility> activeEquipAbilities = new HashMap<>();
    private final Map<UUID, ItemAbility> activeFullSetAbilities = new HashMap<>();

    public ArmorAbilityListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.classKey = new NamespacedKey(plugin, "item_ability_class");
        this.triggerKey = new NamespacedKey(plugin, "item_ability_trigger");
        this.bonusclassKey = new NamespacedKey(plugin, "item_bonus_ability_class");
        this.bonustriggerKey = new NamespacedKey(plugin, "item_bonus_ability_trigger");
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        ItemStack oldItem = event.getOldItem();
        ItemStack newItem = event.getNewItem();

        // unequip
        if (oldItem.hasItemMeta()) {
            PersistentDataContainer container = oldItem.getItemMeta().getPersistentDataContainer();

            if (container.has(classKey, PersistentDataType.STRING) &&
                    container.has(triggerKey, PersistentDataType.STRING)) {

                String trigger = container.get(triggerKey, PersistentDataType.STRING);
                if (trigger.equalsIgnoreCase(AbilityTrigger.EQUIP.name())) {
                    ItemAbility ability = activeEquipAbilities.remove(playerId);
                    removeAbility(player, ability);
                }
            }
        }

        // equip
        if (newItem != null && newItem.hasItemMeta()) {
            PersistentDataContainer container = newItem.getItemMeta().getPersistentDataContainer();
            if (container.has(classKey, PersistentDataType.STRING) &&
                    container.has(triggerKey, PersistentDataType.STRING)) {
                String trigger = container.get(triggerKey, PersistentDataType.STRING);
                if (trigger.equalsIgnoreCase(AbilityTrigger.EQUIP.name())) {
                    ItemAbility ability = createAbility(container);
                    if (ability != null) {
                        ability.execute(player);
                        activeEquipAbilities.put(playerId, ability);
                    }
                }
            }
        }

        checkFullSet(player);
    }

    private ItemAbility createAbility(PersistentDataContainer container) {
        try {
            String className = container.get(classKey, PersistentDataType.STRING);
            Class<?> clazz = Class.forName(className);

            if (!ItemAbility.class.isAssignableFrom(clazz)) return null;

            return (ItemAbility) clazz.getDeclaredConstructor().newInstance();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void removeAbility(Player player, ItemAbility ability) {
        if (ability == null) return;

        try {
            ability.remove(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkFullSet(Player player) {
        UUID playerId = player.getUniqueId();
        ItemStack[] armor = player.getInventory().getArmorContents();
        String bonusClass = null;

        for (ItemStack piece: armor) {
            if (piece == null || !piece.hasItemMeta()) {
                removeAbility(player, activeFullSetAbilities.remove(playerId));
                return;
            }

            var container = piece.getItemMeta().getPersistentDataContainer();

            if (!container.has(bonusclassKey, PersistentDataType.STRING)) return;

            String thisClass = container.get(bonusclassKey, PersistentDataType.STRING);
            if (bonusClass == null) {
                bonusClass = thisClass;
            } else if (!bonusClass.equals(thisClass)) {
                removeAbility(player, activeFullSetAbilities.remove(playerId));
                return;
            }
        }

        if (bonusClass == null) {
            removeAbility(player, activeFullSetAbilities.remove(playerId));
            return;
        }

        try {
            Class<?> clazz = Class.forName(bonusClass);
            if (!ItemAbility.class.isAssignableFrom(clazz)) {
                removeAbility(player, activeFullSetAbilities.remove(playerId));
                return;
            }

            ItemAbility ability = (ItemAbility) clazz.getDeclaredConstructor().newInstance();

            ItemAbility current = activeFullSetAbilities.get(playerId);
            if (current == null || !current.getClass().equals(ability.getClass())) {
                removeAbility(player, activeFullSetAbilities.remove(playerId));
                ability.execute(player);
                activeFullSetAbilities.put(playerId, ability);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
