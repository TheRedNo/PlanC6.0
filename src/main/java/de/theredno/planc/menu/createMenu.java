package de.theredno.planc.menu;

import de.theredno.planc.Main;
import de.theredno.planc.api.GemAPI;
import de.theredno.planc.api.createGem;
import de.theredno.planc.manager.GemsConfigManager;
import de.theredno.planc.util.Gems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class createMenu implements Listener {

    private static GemsConfigManager gemsConfigManager;

    public createMenu(JavaPlugin plugin, GemsConfigManager gemsConfigManager) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.gemsConfigManager = gemsConfigManager;
    }

    private final Map<HumanEntity, Integer> activeTasks = new ConcurrentHashMap<>();

    public static Inventory createMainMenu() {
        String titel = ChatColor.BLUE + "Gems Menu";

        Inventory main = Bukkit.createInventory(null, 1 * 9, titel);

        ItemStack gemLogo = new ItemStack(Material.EMERALD);
        ItemMeta gemLogoMeta = gemLogo.getItemMeta();

        ItemStack craftingLogo = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta craftingLogoMeta = craftingLogo.getItemMeta();

        ItemStack recipessLogo = new ItemStack(Material.PAPER);
        ItemMeta recipesLogoMeta = recipessLogo.getItemMeta();

        gemLogoMeta.setDisplayName(ChatColor.GREEN + "Select Gem");
        craftingLogoMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Level Up Crafting");
        recipesLogoMeta.setDisplayName(ChatColor.GOLD + "Recipes");

        gemLogo.setItemMeta(gemLogoMeta);
        craftingLogo.setItemMeta(craftingLogoMeta);
        recipessLogo.setItemMeta(recipesLogoMeta);

        main.setItem(2, gemLogo);
        main.setItem( 4, craftingLogo);
        main.setItem(6, recipessLogo);

        return main;
    }

    public static Inventory createCraftingMenu() {
        String titel = ChatColor.BLUE + "Gems Menu (Crafting)";

        ItemStack FILLER = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

        Map<Integer, InvItemSlotData> filters = new HashMap<>()
        {
            {
                put(1, new InvItemSlotData(FILLER, 0));
                put(2, new InvItemSlotData(FILLER, 1));
                put(3, new InvItemSlotData(FILLER, 2));
                put(4, new InvItemSlotData(FILLER, 3));
                put(5, new InvItemSlotData(FILLER, 4));
                put(6, new InvItemSlotData(FILLER, 5));
                put(7, new InvItemSlotData(FILLER, 6));
                put(8, new InvItemSlotData(FILLER, 7));
                put(9, new InvItemSlotData(FILLER, 8));
                put(10, new InvItemSlotData(FILLER, 9));
                put(11, new InvItemSlotData(FILLER, 13));
                put(12, new InvItemSlotData(FILLER, 14));
                put(13, new InvItemSlotData(FILLER, 15));
                put(14, new InvItemSlotData(FILLER, 16));
                put(15, new InvItemSlotData(FILLER, 17));
                put(16, new InvItemSlotData(FILLER, 18));
                put(17, new InvItemSlotData(FILLER, 22));
                put(18, new InvItemSlotData(FILLER, 23));
                put(19, new InvItemSlotData(FILLER, 25));
                put(20, new InvItemSlotData(FILLER, 26));
                put(21, new InvItemSlotData(FILLER, 27));
                put(22, new InvItemSlotData(FILLER, 31));
                put(23, new InvItemSlotData(FILLER, 32));
                put(24, new InvItemSlotData(FILLER, 33));
                put(25, new InvItemSlotData(FILLER, 34));
                put(26, new InvItemSlotData(FILLER, 35));
                put(27, new InvItemSlotData(FILLER, 36));
                put(28, new InvItemSlotData(FILLER, 37));
                put(29, new InvItemSlotData(FILLER, 38));
                put(30, new InvItemSlotData(FILLER, 39));
                put(31, new InvItemSlotData(FILLER, 40));
                put(32, new InvItemSlotData(FILLER, 41));
                put(33, new InvItemSlotData(FILLER, 42));
                put(34, new InvItemSlotData(FILLER, 43));
            }
        };

        Inventory crafting = Bukkit.createInventory(null, 5 * 9, titel);

        for (InvItemSlotData filter : filters.values()) {
            ItemStack item = filter.getItem();
            int slot = filter.getSlot();

            crafting.setItem(slot, item);
        }

        ItemStack backIcon = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta backIconMeta = backIcon.getItemMeta();

        backIconMeta.setDisplayName(ChatColor.RED + "Back");

        backIcon.setItemMeta(backIconMeta);

        crafting.setItem(44, backIcon);

        return crafting;
    }

    public static Inventory createRecipesCraftingMenu() {
        String titel = ChatColor.BLUE + "Gems Menu (Recipes)";

        ItemStack Gem1Template = new ItemStack(Material.EMERALD);
        ItemMeta Gem1TemplateMeta = Gem1Template.getItemMeta();
        Gem1TemplateMeta.setDisplayName(ChatColor.GREEN + "Level 1");
        Gem1Template.setItemMeta(Gem1TemplateMeta);

        ItemStack Gem2Template = new ItemStack(Material.EMERALD);
        ItemMeta Gem2TemplateMeta = Gem2Template.getItemMeta();
        Gem2TemplateMeta.setDisplayName(ChatColor.GREEN + "Level 2");
        Gem2Template.setItemMeta(Gem2TemplateMeta);

        Map<Integer, InvItemSlotData> ingredients= Map.of(
                1, new InvItemSlotData(new ItemStack(Material.NETHERITE_INGOT), 10),
                2, new InvItemSlotData(new ItemStack(Material.EXPERIENCE_BOTTLE), 11),
                3, new InvItemSlotData(new ItemStack(Material.NETHERITE_INGOT), 12),
                4, new InvItemSlotData(new ItemStack(Material.DIAMOND_BLOCK), 19),
                5, new InvItemSlotData(Gem1Template, 20),
                6, new InvItemSlotData(new ItemStack(Material.DIAMOND_BLOCK), 21),
                7, new InvItemSlotData(new ItemStack(Material.NETHERITE_INGOT), 28),
                8, new InvItemSlotData(new ItemStack(Material.EXPERIENCE_BOTTLE), 29),
                9, new InvItemSlotData(new ItemStack(Material.NETHERITE_INGOT), 30),
                10, new InvItemSlotData(Gem2Template, 24)
        );

        Inventory recipes = Bukkit.createInventory(null, 5 * 9, titel);

        for (InvItemSlotData data : ingredients.values()) {
            ItemStack gem = data.getItem();
            int slot = data.getSlot();

            recipes.setItem(slot, gem);
        }

        ItemStack backIcon = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta backIconMeta = backIcon.getItemMeta();

        backIconMeta.setDisplayName(ChatColor.RED + "Back");

        backIcon.setItemMeta(backIconMeta);

        recipes.setItem(44, backIcon);

        return recipes;
    }

    public static Inventory createSelectMenu(Player player) {
        String titel = ChatColor.BLUE + "Gems Menu (Select)";

        List<Integer> slots = List.of(
                1, 2, 3, 4, 5, 6, 7, 10, 11, 12
        );

        List<ItemStack> configGems = gemsConfigManager.getGems(player);

        Map<Integer, InvItemSlotData> gems = new HashMap<>();

        IntStream.range(0, configGems.size())
                .forEach(i -> gems.put(i + 1, new InvItemSlotData(configGems.get(i), slots.get(i))));


        Inventory select = Bukkit.createInventory(null, 2 * 9, titel);

        for (InvItemSlotData data : gems.values()) {
            ItemStack gem = data.getItem();
            int slot = data.getSlot();

            select.setItem(slot, gem);
        }

        ItemStack backIcon = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta backIconMeta = backIcon.getItemMeta();

        backIconMeta.setDisplayName(ChatColor.RED + "Back");

        backIcon.setItemMeta(backIconMeta);

        select.setItem(17, backIcon);

        return select;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        InventoryView view = e.getView();
        ItemStack clickedItem = e.getCurrentItem();

        if (!(e.getWhoClicked() instanceof Player player)) return;

        if (clickedItem == null) return;

        if (view.getTitle().equals(ChatColor.BLUE + "Gems Menu")) {
            if (clickedItem.getType() == Material.EMERALD && clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Select Gem")) {
                e.getWhoClicked().openInventory(createSelectMenu(player));
                return;
            }

            if (clickedItem.getType() == Material.CRAFTING_TABLE && clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Level Up Crafting")) {
                e.getWhoClicked().openInventory(createCraftingMenu());
                e.getWhoClicked().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "" + "Pro Slot immer nur 1 Item hinlegen sonst werden überschüssige Items gelöscht!!!");
                return;
            }

            if (clickedItem.getType() == Material.PAPER && clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Recipes")) {
                e.getWhoClicked().openInventory(createRecipesCraftingMenu());
                return;
            }
        }

        if (view.getTitle().equals(ChatColor.BLUE + "Gems Menu (Crafting)")) {
            if (clickedItem.getType() == Material.RED_STAINED_GLASS_PANE && clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "Back")) {
                e.getWhoClicked().openInventory(createMainMenu());
                return;
            }
            if (clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                e.setCancelled(true);
                return;
            }

            if (e.getRawSlot() == 24) {
                // perform craft (uses top inventory of the viewer)
                Inventory top = e.getView().getTopInventory();
                HumanEntity clicker = e.getWhoClicked();
                performCraft(top, (Player) clicker);
            }

        }



        /*
        Map<Integer, InvItemSlotData> ingredients= Map.of(
                1, new InvItemSlotData(new ItemStack(Material.NETHERITE_INGOT), 10),
                2, new InvItemSlotData(new ItemStack(Material.EXPERIENCE_BOTTLE), 11),
                3, new InvItemSlotData(new ItemStack(Material.NETHERITE_INGOT), 12),
                4, new InvItemSlotData(new ItemStack(Material.DIAMOND_BLOCK), 19),
                5, new InvItemSlotData(Gem1Template, 20),
                6, new InvItemSlotData(new ItemStack(Material.DIAMOND_BLOCK), 21),
                7, new InvItemSlotData(new ItemStack(Material.NETHERITE_INGOT), 28),
                8, new InvItemSlotData(new ItemStack(Material.EXPERIENCE_BOTTLE), 29),
                9, new InvItemSlotData(new ItemStack(Material.NETHERITE_INGOT), 30),
                10, new InvItemSlotData(Gem2Template, 24)
        );
         */

        if (view.getTitle().equals(ChatColor.BLUE + "Gems Menu (Select)")) {
            if (clickedItem.getType() == Material.RED_STAINED_GLASS_PANE && clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "Back")) {
                e.getWhoClicked().openInventory(createMainMenu());
                return;
            }
            e.setCancelled(true);
        }

        if (view.getTitle().equals(ChatColor.BLUE + "Gems Menu (Recipes)")) {
            if (clickedItem.getType() == Material.RED_STAINED_GLASS_PANE && clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "Back")) {
                e.getWhoClicked().openInventory(createMainMenu());
                return;
            }
            e.setCancelled(true);
        }

    }



    // ---------- Helper: prüft die Slots und aktualisiert Slot 24 (Preview) ----------
    private void updateCraftingResult(Inventory inv) {
        if (inv == null) return;

        ItemStack item10 = inv.getItem(10);
        ItemStack item11 = inv.getItem(11);
        ItemStack item12 = inv.getItem(12);
        ItemStack item19 = inv.getItem(19);
        ItemStack gemSlot20 = inv.getItem(20);
        ItemStack item21 = inv.getItem(21);
        ItemStack item28 = inv.getItem(28);
        ItemStack item29 = inv.getItem(29);
        ItemStack item30 = inv.getItem(30);

        boolean recipeMatches = item10 != null && item10.getType() == Material.NETHERITE_INGOT &&
                item11 != null && item11.getType() == Material.EXPERIENCE_BOTTLE &&
                item12 != null && item12.getType() == Material.NETHERITE_INGOT &&
                item19 != null && item19.getType() == Material.DIAMOND_BLOCK &&
                gemSlot20 != null && createGem.isGem(gemSlot20) &&
                item21 != null && item21.getType() == Material.DIAMOND_BLOCK &&
                item28 != null && item28.getType() == Material.NETHERITE_INGOT &&
                item29 != null && item29.getType() == Material.EXPERIENCE_BOTTLE &&
                item30 != null && item30.getType() == Material.NETHERITE_INGOT;

        if (recipeMatches) {
            ItemStack gem = gemSlot20.clone();
            createGem custom = GemAPI.getFromItem(gem);

            int level = createGem.getLevelFromItem(gem);
            int newLevel = Math.min(level + 1, 10);

            createGem.setLevelOnItem(gem, newLevel);
            custom.updateItemLevelLore(gem, newLevel);

            inv.setItem(24, gem);
        } else {
            inv.setItem(24, null);
        }
    }

    private boolean isCraftingInventory(Inventory inv) {
        if (inv == null) return false;
        // Wir prüfen den Titel des Top-Inventars (sicherer)
        HumanEntity viewer = null;
        if (!inv.getViewers().isEmpty()) viewer = inv.getViewers().get(0);
        // better: check title via openInventory when handling events; here we assume caller passes correct inventory
        // For safety we also try to check the inventory's title via the first viewer
        if (viewer != null && viewer.getOpenInventory() != null) {
            String title = viewer.getOpenInventory().getTitle();
            return title != null && title.equals(ChatColor.BLUE + "Gems Menu (Crafting)");
        }
        return false;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        HumanEntity human = e.getPlayer();
        // start task only for crafting inventory
        if (e.getView().getTitle().equals(ChatColor.BLUE + "Gems Menu (Crafting)")) {

            // prevent double scheduling
            if (activeTasks.containsKey(human)) return;

            // schedule repeating task: startet nach 1 Tick, läuft jede Tick
            int taskId = new BukkitRunnable() {
                @Override
                public void run() {
                    // Wenn Spieler das Inventar nicht mehr offen hat -> cancel
                    if (!human.isValid() || human.getOpenInventory() == null) {
                        Integer id = activeTasks.remove(human);
                        if (id != null) this.cancel();
                        return;
                    }

                    // Only operate on the top inventory (custom GUI)
                    Inventory top = human.getOpenInventory().getTopInventory();

                    // extra safety: check title again
                    if (human.getOpenInventory().getTitle().equals(ChatColor.BLUE + "Gems Menu (Crafting)")) {
                        updateCraftingResult(top);
                    } else {
                        // inventory title changed -> cancel
                        Integer id = activeTasks.remove(human);
                        if (id != null) this.cancel();
                    }
                }
            }.runTaskTimer(Main.getInstance(), 1L, 1L).getTaskId();

            activeTasks.put(human, taskId);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        HumanEntity human = e.getPlayer();
        // cancel scheduled task when closed
        Integer id = activeTasks.remove(human);
        if (id != null) {
            Bukkit.getScheduler().cancelTask(id);
        }

        // Optional: beim Schließen Items zurückgeben / aufräumen
        // Inventory top = e.getView().getTopInventory();
        // cleanup(top, (Player) human);
    }

    // ---------- Helper: führt das Crafting aus (wird ausgeführt, wenn Spieler Result Slot klickt) ----------
    private void performCraft(Inventory inv, Player p) {
        if (inv == null || p == null) return;

        ItemStack result = inv.getItem(24);
        if (result == null || result.getType() == Material.AIR) {
            p.sendMessage(ChatColor.RED + "Das Rezept ist nicht vollständig.");
            return;
        }

        // Nochmal die Prüfung, um Missbrauch zu verhindern
        ItemStack item10 = inv.getItem(10);
        ItemStack item11 = inv.getItem(11);
        ItemStack item12 = inv.getItem(12);
        ItemStack item19 = inv.getItem(19);
        ItemStack gemSlot20 = inv.getItem(20);
        ItemStack item21 = inv.getItem(21);
        ItemStack item28 = inv.getItem(28);
        ItemStack item29 = inv.getItem(29);
        ItemStack item30 = inv.getItem(30);

        if (!(item10 != null && item10.getType() == Material.NETHERITE_INGOT &&
                item11 != null && item11.getType() == Material.EXPERIENCE_BOTTLE &&
                item12 != null && item12.getType() == Material.NETHERITE_INGOT &&
                item19 != null && item19.getType() == Material.DIAMOND_BLOCK &&
                gemSlot20 != null && createGem.isGem(gemSlot20) &&
                item21 != null && item21.getType() == Material.DIAMOND_BLOCK &&
                item28 != null && item28.getType() == Material.NETHERITE_INGOT &&
                item29 != null && item29.getType() == Material.EXPERIENCE_BOTTLE &&
                item30 != null && item30.getType() == Material.NETHERITE_INGOT)) {
            p.sendMessage(ChatColor.RED + "Das Rezept ist nicht vollständig.");
            inv.setItem(24, null);
            return;
        }

        // Verbrauch: wir setzen die Eingabefelder auf AIR (oder dekrementiere Stacks falls du mehrere verwenden möchtest)
        inv.setItem(10, null);
        inv.setItem(11, null);
        inv.setItem(12, null);
        inv.setItem(19, null);
        inv.setItem(20, null); // das Gem (wir geben ja stattdessen die verbesserte Version)
        inv.setItem(21, null);
        inv.setItem(28, null);
        inv.setItem(29, null);
        inv.setItem(30, null);

        // Ergebnisse dem Spieler geben (clonen, damit Inventar unabhängig bleibt)
        p.getInventory().addItem(result.clone());

        // Feedback
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        p.sendMessage(ChatColor.GREEN + "Dein Gem wurde verbessert!");

        // Resultatfeld leeren (wichtig)
        inv.setItem(24, null);
    }


    /*
        player.getInventory().addItem(Gems.strengthGem.createItem());
        player.getInventory().addItem(Gems.healingGem.createItem());
        player.getInventory().addItem(Gems.airgem.createItem());
        player.getInventory().addItem(Gems.firegem.createItem());
        player.getInventory().addItem(Gems.irongem.createItem());
        player.getInventory().addItem(Gems.lightninggem.createItem());
        player.getInventory().addItem(Gems.sandgem.createItem());
        player.getInventory().addItem(Gems.icegem.createItem());
        player.getInventory().addItem(Gems.lavagem.createItem());
        player.getInventory().addItem(Gems.watergem.createItem());
     */
}
