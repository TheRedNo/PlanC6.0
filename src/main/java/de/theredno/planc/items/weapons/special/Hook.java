package de.theredno.planc.items.weapons.special;

import de.theredno.planc.items.weapons.scythes.BloodscytheAbility;
import de.theredno.planc.util.AbilityTrigger;
import de.theredno.planc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Hook {
    public static ItemStack create() {
        return new ItemBuilder(Material.COPPER_CHAIN)
                .setDisplayName("§3§lHook")
                .setAbilityName("§6Ability: Pull  §e§lRIGHT CLICK")
                .addAbilityDescriptionLine("§7Pull the facing mob towards you.")
                .addAbilityDescriptionLine("")
                .setAbilityUsage("§8Cooldown: 10s")
                .setAbility(AbilityTrigger.RIGHT_CLICK)
                .setAbilityClass(HookAbility.class)
                .addTagString("hook", "true")
                .setItemModelTexture("planc", "hook")
                .build();
    }
}
