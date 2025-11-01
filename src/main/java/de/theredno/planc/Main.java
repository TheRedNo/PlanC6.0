package de.theredno.planc;

import de.theredno.planc.api.GemAPI;
import de.theredno.planc.commands.*;
import de.theredno.planc.listeners.GemListener;
import de.theredno.planc.manager.GemsConfigManager;
import de.theredno.planc.menu.createMenu;
import de.theredno.planc.util.Gems;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private static GemsConfigManager gemsConfigManager;

    @Override
    public void onEnable() {
        instance = this;
        GemAPI api = new GemAPI(this);
        gemsConfigManager = new GemsConfigManager(this);
        createMenu menu = new createMenu(this);

        getServer().getPluginManager().registerEvents(new GemListener(), this);

        getCommand("give_all_items").setExecutor(new giveAllItems());
        getCommand("setlevel").setExecutor(new setLevel());
        getCommand("gem_menu").setExecutor(new GemsInvCommand());
        getCommand("addgem").setExecutor(new addGem(this));
        getCommand("addgem").setTabCompleter(new addGem(this));

        getCommand("getgem").setExecutor(new getGem(this));

        Gems.createGems();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getInstance() {
        return instance;
    }

    public static GemsConfigManager getGemsManager() {
        return gemsConfigManager;
    }
}
