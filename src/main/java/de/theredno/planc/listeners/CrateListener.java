package de.theredno.planc.listeners;

import de.theredno.planc.Main;
import de.theredno.planc.manager.GemsConfigManager;
import de.theredno.planc.util.Gems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CrateListener implements Listener {
    private final GemsConfigManager gemsConfigManager;
    private final Random random = new Random();

    private static final String InvName = ChatColor.GOLD + "Gem Crate";

    private BukkitTask task;

    // Weighted Gems (ItemStack + Wahrscheinlichkeit in %)
    private final List<WeightedGem> weightedGems = new ArrayList<>();


    public CrateListener() {
        gemsConfigManager = Main.getGemsConfigManager();

        // Beispiel: Gems registrieren mit Wahrscheinlichkeiten
        weightedGems.add(new WeightedGem(Gems.getGem("strength_gem"), 10));
        weightedGems.add(new WeightedGem(Gems.getGem("healing_gem"), 10));
        weightedGems.add(new WeightedGem(Gems.getGem("air_gem"), 10));
        weightedGems.add(new WeightedGem(Gems.getGem("fire_gem"), 10));
        weightedGems.add(new WeightedGem(Gems.getGem("iron_gem"), 10));
        weightedGems.add(new WeightedGem(Gems.getGem("lightning_gem"), 10));
        weightedGems.add(new WeightedGem(Gems.getGem("sand_gem"), 10));
        weightedGems.add(new WeightedGem(Gems.getGem("ice_gem"), 10));
        weightedGems.add(new WeightedGem(Gems.getGem("lava_gem"), 10));
        weightedGems.add(new WeightedGem(Gems.getGem("water_gem"), 10));
    }

    @EventHandler
    public void onPlayerUseGemSlot(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        if (item.getItemMeta().getDisplayName().equals("§6Gem Slot")) {
            event.setCancelled(true);
            openGemSlotInventory(player);
        }
    }

    private void openGemSlotInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, InvName);

        // Anfangs zufällig befüllen
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, getRandomWeightedGem());
        }

        player.openInventory(inv);
        startGemRatter(player, inv);
    }


    private void startGemRatter(Player player, Inventory inv) {
        task = new BukkitRunnable() {
            final int totalTicks = 20 * 5; // 5 Sekunden
            int elapsedTicks = 0;

            @Override
            public void run() {

                // Alle Slots nach links verschieben
                for (int i = 0; i < 8; i++) {
                    inv.setItem(i, inv.getItem(i + 1));
                }

                // Neuer Slot rechts: random weighted gem
                inv.setItem(8, getRandomWeightedGem());

                elapsedTicks++;
                if (elapsedTicks >= totalTicks) {



                    reward(player, inv.getItem(4));
                    player.closeInventory();
                    task.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 2L);
    }

    private void reward(Player player, ItemStack rewardGem) {
        List<ItemStack> configGems = gemsConfigManager.getGems(player);

        for (ItemStack gem : configGems) {
            if (rewardGem.getItemMeta().getDisplayName().equals(gem.getItemMeta().getDisplayName())) {
                player.sendMessage(ChatColor.YELLOW + "You already have this Gem: " + rewardGem.getItemMeta().getDisplayName());
                return;
            }
        }

        gemsConfigManager.addGem(player, rewardGem);
        player.sendMessage(ChatColor.GREEN+ "You get this Gem: " + rewardGem.getItemMeta().getDisplayName());

    }


    private ItemStack getRandomWeightedGem() {
        double totalWeight = weightedGems.stream().mapToDouble(WeightedGem::getWeight).sum();
        double r = random.nextDouble() * totalWeight;
        double sum = 0;
        for (WeightedGem wg : weightedGems) {
            sum += wg.getWeight();
            if (r <= sum) return wg.getItem().clone(); // clone, damit es unabhängig ist
        }
        return weightedGems.get(0).getItem().clone(); // Fallback
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(InvName)) {

            if (event.getReason() != InventoryCloseEvent.Reason.PLAYER) return;

            if (!(event.getPlayer() instanceof Player player)) return;

            Inventory inv  = event.getInventory();

            reward(player, inv.getItem(4));
            task.cancel();
        }
    }


    // WeightedGem-Klasse
    private static class WeightedGem {
        private final ItemStack item;
        private final double weight;

        public WeightedGem(ItemStack item, double weight) {
            this.item = item;
            this.weight = weight;
        }

        public ItemStack getItem() { return item; }
        public double getWeight() { return weight; }
    }
}
