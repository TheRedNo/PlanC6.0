package de.theredno.planc.util;

import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ItemBuilder {
    private ItemStack item;
    private ItemMeta meta;
    @Setter
    private static JavaPlugin plugin;

    private final List<String> enchantmentLore = new ArrayList<>();
    private String abilityName = null;
    private final List<String> abilityDescription = new ArrayList<>();
    private String abilityUsage = null;
    private String rarity = null;
    private final Map<String, Double> stats = new HashMap<>();

    private de.theredno.planc.util.AbilityTrigger trigger = null;
    private Class<? extends de.theredno.planc.util.ItemAbility> abilityClass = null;

    private Class<? extends de.theredno.planc.util.ItemAbility> bonusClass = null;
    private de.theredno.planc.util.AbilityTrigger bonusTrigger = null;


    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack existingItem) {
        this.item = existingItem.clone();
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder setDisplayName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder addCustomEnchantmentLore(String line) {
        enchantmentLore.add(line);
        return this;
    }

    public ItemBuilder setAbilityName(String name) {
        this.abilityName = name;
        return this;
    }

    public ItemBuilder addAbilityDescriptionLine(String line) {
        this.abilityDescription.add(line);
        return this;
    }

    public ItemBuilder setAbilityUsage(String usage) {
        this.abilityUsage = usage;
        return this;
    }

    public ItemBuilder setRarity(String rarity) {
        this.rarity = rarity;
        return this;
    }

    public ItemBuilder addCheckEnchantment() {
        meta.addEnchant(Enchantment.AQUA_AFFINITY, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder addCheckEnchantment2() {
        meta.addEnchant(Enchantment.DENSITY, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder addCheckEnchantment3() {
        meta.addEnchant(Enchantment.BREACH, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }


    public ItemBuilder setItemModelTexture(String pack, String item) {
        meta.setItemModel(new NamespacedKey(pack, item));
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }

    public ItemBuilder setDamage(double num) {
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier("generic.attackDamage", 10, AttributeModifier.Operation.ADD_NUMBER));
        return this;
    }

    public ItemBuilder setSpeed(double num) {
        meta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier("generic.attackSpeed", 5, AttributeModifier.Operation.ADD_NUMBER));
        return this;
    }


    public ItemBuilder setAbility(de.theredno.planc.util.AbilityTrigger trigger) {
        this.trigger = trigger;
        return this;
    }

    public ItemBuilder setAbilityClass(Class<? extends de.theredno.planc.util.ItemAbility> abilityClass) {
        this.abilityClass = abilityClass;
        return this;
    }

    public ItemBuilder setBonusClass(Class<? extends de.theredno.planc.util.ItemAbility> setBonusClass) {
        this.bonusClass = setBonusClass;
        return this;
    }

    public ItemBuilder setBonusTrigger(de.theredno.planc.util.AbilityTrigger trigger) {
        this.bonusTrigger = trigger;
        return this;
    }


    public ItemBuilder addTagString(String key, String value) {
        if (plugin != null) {
            NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
            meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
        }
        return this;
    }

    public ItemBuilder addTagInt(String key) {
        if (plugin != null) {
            NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
            meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 0);
        }
        return this;
    }

    public ItemStack build() {
        List<String> lore = new ArrayList<>(enchantmentLore);

        if (abilityName != null || !abilityDescription.isEmpty()) {
            lore.add("");
        }

        if (abilityName != null) {
            lore.add(abilityName);
        }

        lore.addAll(abilityDescription);

        if (abilityUsage != null) {
            lore.add(abilityUsage);
        }

        if (rarity != null) {
            lore.add("");
            lore.add(rarity);
        }

        meta.setLore(lore);


        if (plugin != null && abilityClass != null && trigger != null) {
            NamespacedKey key = new NamespacedKey(plugin, "item_ability_class");
            NamespacedKey triggerKey = new NamespacedKey(plugin, "item_ability_trigger");

            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, abilityClass.getName());
            meta.getPersistentDataContainer().set(triggerKey, PersistentDataType.STRING, trigger.name());
        }

        if (plugin != null && bonusClass != null && bonusTrigger != null) {
            NamespacedKey setBonusKey = new NamespacedKey(plugin, "item_bonus_ability_class");
            NamespacedKey setBonusTriggerKey = new NamespacedKey(plugin, "item_bonus_ability_trigger");

            meta.getPersistentDataContainer().set(setBonusKey, PersistentDataType.STRING, bonusClass.getName());
            meta.getPersistentDataContainer().set(setBonusTriggerKey, PersistentDataType.STRING, bonusTrigger.name());
        }


        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
