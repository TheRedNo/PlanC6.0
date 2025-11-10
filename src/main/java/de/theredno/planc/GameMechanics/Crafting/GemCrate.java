package de.theredno.planc.GameMechanics.Crafting;

import de.theredno.planc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class GemCrate {
    public static void initCraftingRecipe() {
        ItemStack crate = de.theredno.planc.Gems.GemCrate.Crate();
        NamespacedKey CrateKey = new NamespacedKey(Main.getInstance(), "gem_crate");

        Bukkit.addRecipe(CrateRecipe(CrateKey, crate));
    }

    private static ShapedRecipe CrateRecipe(NamespacedKey key, ItemStack result) {
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("WHW", "GTG", "NSN");

        recipe.setIngredient('W', Material.WITHER_SKELETON_SKULL);
        recipe.setIngredient('H', Material.HEAVY_CORE);
        recipe.setIngredient('G', Material.GOLDEN_APPLE);
        recipe.setIngredient('T', Material.TRIDENT);
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('S', Material.NETHER_STAR);

        return recipe;
    }
}
