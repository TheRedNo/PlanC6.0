package de.theredno.planc.items.weapons.scythes;

import de.theredno.planc.util.AbilityTrigger;
import de.theredno.planc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Bloodscythe {
    public static ItemStack create() {
        return new ItemBuilder(Material.IRON_HOE)
                .setDisplayName("§4§lBlood Scythe ⬦⬦⬦⬦⬦") // blood stacks ᚋ ᚌ ᚍ ᚎ ᚏ
                .addCustomEnchantmentLore("§7Hit enemies to build up §4Blood Stacks ⬥")
                .setAbilityName("§6Ability: Blood slash  §e§lRIGHT CLICK")
                .addAbilityDescriptionLine("§7Spawn a Blood slash in front of you dealing")
                .addAbilityDescriptionLine("§7damage to all Mobs it touches.")
                .addAbilityDescriptionLine("")
                .setAbilityUsage("§8Cost: §45 Blood Stacks")
                .setAbility(AbilityTrigger.RIGHT_CLICK)
                .setAbilityClass(BloodscytheAbility.class)
                .addTagString("blood_scythe", "true")
                .addTagInt("hits")
                .setItemModelTexture("planc", "blood_scythe")
                .build();
    }
}
