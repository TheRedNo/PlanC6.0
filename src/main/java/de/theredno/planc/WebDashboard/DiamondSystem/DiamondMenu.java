package de.theredno.planc.WebDashboard.DiamondSystem;

import de.theredno.planc.Main;
import de.theredno.planc.WebDashboard.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DiamondMenu {

    public static Inventory initInvs() {
        Inventory bankInv = Bukkit.createInventory(null, 9 * 1, ChatColor.BLUE + "Diamond Account");

        ItemStack none = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

        ItemStack dia = new ItemStack(Material.DIAMOND);
        ItemStack diaBlock = new ItemStack(Material.DIAMOND_BLOCK);

        ItemMeta diaMeta = dia.getItemMeta();
        ItemMeta diaBlockMeta = diaBlock.getItemMeta();

        diaMeta.setDisplayName(ChatColor.BLUE + "1 Diamond Auszahlen");
        diaBlockMeta.setDisplayName(ChatColor.BLUE + "64 Diamond Auszahlen");

        dia.setItemMeta(diaMeta);
        diaBlock.setItemMeta(diaBlockMeta);

        bankInv.setItem(0, none);
        bankInv.setItem(3, none);
        bankInv.setItem(4, none);
        bankInv.setItem(6, none);

        bankInv.setItem(1, dia);
        bankInv.setItem(2, diaBlock);

        bankInv.setItem(8, new ItemStack(Material.PAPER));

        return bankInv;
    }

    public static void openDiaInv(Inventory inventory, Player player) {
        int playerID = DataManager.getPlayerID(player.getUniqueId());
        int diamonds = DiamondManager.getDiamonds(playerID);

        inventory.getItem(8).getItemMeta().setDisplayName(ChatColor.AQUA + String.valueOf(diamonds) + " Diamonds");

        player.openInventory(inventory);
    }
}
