package de.theredno.planc.listeners;

import de.theredno.planc.Main;
import de.theredno.planc.items.weapons.scythes.Bloodscythe;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class DamageListener implements Listener {

    private String getBloodStack(int hits) {
        return switch (hits) {
            case 1 -> "⬥⬦⬦⬦⬦";
            case 2 -> "⬥⬥⬦⬦⬦";
            case 3 -> "⬥⬥⬥⬦⬦";
            case 4 -> "⬥⬥⬥⬥⬦";
            case 5 -> "⬥⬥⬥⬥⬥";
            default -> "⬦⬦⬦⬦⬦";
        };
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;
        if (!(e.getEntity() instanceof LivingEntity living)) return;

        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (!weapon.hasItemMeta()) return;
        ItemMeta meta = weapon.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        NamespacedKey bloodScytheKey = new NamespacedKey(Main.getInstance(), "blood_scythe");
        NamespacedKey hitsKey = new NamespacedKey(Main.getInstance(), "hits");

        if (data.has(bloodScytheKey, PersistentDataType.STRING)) {
            int hits = data.getOrDefault(hitsKey, PersistentDataType.INTEGER, 0);
            hits = Math.min(hits + 1, 5);
            data.set(hitsKey, PersistentDataType.INTEGER, hits);
            meta.setDisplayName("§4§lBlood Scythe " + getBloodStack(hits));
            weapon.setItemMeta(meta);
            player.getInventory().setItemInMainHand(weapon);
        }

    }
}

