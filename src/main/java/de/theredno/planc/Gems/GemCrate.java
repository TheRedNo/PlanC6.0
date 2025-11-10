package de.theredno.planc.Gems;

import de.theredno.planc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class GemCrate {
    public static ItemStack Crate() {
        ItemStack crate = new ItemStack(Material.CHEST);
        ItemMeta meta = crate.getItemMeta();

        meta.setDisplayName("§6Gem Crate");
        crate.setItemMeta(meta);

        return crate;
    }
}
