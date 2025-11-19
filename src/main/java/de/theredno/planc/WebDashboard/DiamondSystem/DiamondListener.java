package de.theredno.planc.WebDashboard.DiamondSystem;

import de.theredno.planc.Main;
import de.theredno.planc.WebDashboard.DataManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.UUID;

public class DiamondListener implements Listener {
    @EventHandler
    public static void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        int playerID = DataManager.getPlayerID(player.getUniqueId());

        if (e.getView().getTitle().equals(ChatColor.BLUE + "Diamond Account")) {
            int slot = e.getSlot();

            if (slot == 7) {
                ItemStack item = e.getInventory().getItem(7);
                int amount = item.getAmount();

                if (item.getType().equals(Material.DIAMOND)) {
                    try {
                        DiamondManager.updateDiamonds(playerID, amount, Main.getInstance().getMysql().getConnection());
                        Main.getInstance().getMysql().getConnection().close();
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                }

                if (item.getType().equals(Material.DIAMOND_BLOCK)) {
                    try {
                        DiamondManager.updateDiamonds(playerID, amount * 9, Main.getInstance().getMysql().getConnection());
                        Main.getInstance().getMysql().getConnection().close();
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                }

                e.setCancelled(false);
                return;
            }

            if (slot == 1) {
                int avaiableDiamonds = DiamondManager.getDiamonds(playerID);

                try {
                    if (avaiableDiamonds > 0) {
                        DiamondManager.updateDiamonds(playerID, avaiableDiamonds - 1, Main.getInstance().getMysql().getConnection());
                        Main.getInstance().getMysql().getConnection().close();
                        player.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }

            if (slot == 2) {
                int avaiableDiamonds = DiamondManager.getDiamonds(playerID);

                try {
                    if (avaiableDiamonds >= 64) {
                        DiamondManager.updateDiamonds(playerID, avaiableDiamonds - 64, Main.getInstance().getMysql().getConnection());
                        Main.getInstance().getMysql().getConnection().close();
                        player.getInventory().addItem(new ItemStack(Material.DIAMOND, 64));
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public static void onVilligerClick(PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();

        if (!(entity instanceof Villager villager)) return;

        Player player = e.getPlayer();

        if (villager.getCustomName() != null && villager.getCustomName().equals(ChatColor.AQUA + "Banker")) {
            DiamondMenu.openDiaInv(DiamondMenu.initInvs(), player);

            e.setCancelled(true);
        }
    }
}
