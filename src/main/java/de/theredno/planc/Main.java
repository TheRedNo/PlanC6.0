package de.theredno.planc;

import de.theredno.planc.GameMechanics.Crafting.GemCrate;
import de.theredno.planc.GameMechanics.DispenserPlaceBlock.OnDispense;
import de.theredno.planc.Gems.API.GemAPI;
import de.theredno.planc.Gems.Commands.*;
import de.theredno.planc.Gems.Listeners.CrateListener;
import de.theredno.planc.Gems.Listeners.GemListener;
import de.theredno.planc.MySQL.LoginData;
import de.theredno.planc.MySQL.MySQL;
import de.theredno.planc.TPA.Commands.TpaCommand;
import de.theredno.planc.Commands.*;
import de.theredno.planc.Listeners.*;
import de.theredno.planc.Gems.Manager.GemsConfigManager;
import de.theredno.planc.Gems.Menu.createMenu;
import de.theredno.planc.Gems.Gems;
import de.theredno.planc.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    @Getter
    private static GemsConfigManager gemsConfigManager;
    private MySQL mysql;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        instance = this;
        GemAPI api = new GemAPI(this);
        gemsConfigManager = new GemsConfigManager(this);
        createMenu menu = new createMenu(this, gemsConfigManager);

        ItemBuilder.setPlugin(this);

        GemCrate.initCraftingRecipe();

        mysql = LoginData.getLogin();

        getServer().getPluginManager().registerEvents(new GemListener(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);

        getServer().getPluginManager().registerEvents(new ArmorAbilityListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemAbilityListener(this), this);

        getServer().getPluginManager().registerEvents(new OnDispense(), this);


        getCommand("give_all_items").setExecutor(new giveAllItems());
        getCommand("setlevel").setExecutor(new setLevel());
        getCommand("gem_menu").setExecutor(new GemsInvCommand());
        getCommand("addgem").setExecutor(new addGem());
        getCommand("addgem").setTabCompleter(new addGem());
        getCommand("createcrate").setExecutor(new GiveCreateCommand());
        getCommand("tpa").setExecutor(new TpaCommand());

        getCommand("getgem").setExecutor(new getGem());



        getServer().getPluginManager().registerEvents(new CrateListener(), this);

        Gems.createGems();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
