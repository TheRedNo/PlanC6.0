package de.theredno.planc.Items.weapons.special;

import de.theredno.planc.util.AbilityTrigger;
import de.theredno.planc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Hook {
    public static ItemStack create() {
        return new ItemBuilder(Material.WAXED_EXPOSED_COPPER_CHAIN)
                .setDisplayName("§3§lHook")
                .addCustomEnchantmentLore("§750 block range")
                .setAbilityName("§6Ability: Pull  §e§lRIGHT CLICK")
                .addAbilityDescriptionLine("§7Pull the facing mob towards you/")
                .addAbilityDescriptionLine("§7Pull yourself to the facing block.")
                .setAbilityUsage("§8Cooldown: 10s")
                .setAbility(AbilityTrigger.RIGHT_CLICK)
                .setAbilityClass(HookAbility.class)
                .addTagString("hook", "true")
                .setItemModelTexture("planc", "hook")
                .build();
    }
}
