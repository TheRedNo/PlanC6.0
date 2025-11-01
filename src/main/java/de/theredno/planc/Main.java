package de.theredno.planc;

import de.theredno.planc.api.GemAPI;
import de.theredno.planc.commands.*;
import de.theredno.planc.listeners.ArmorAbilityListener;
import de.theredno.planc.listeners.GemListener;
import de.theredno.planc.listeners.ItemAbilityListener;
import de.theredno.planc.manager.GemsConfigManager;
import de.theredno.planc.menu.createMenu;
import de.theredno.planc.util.Gems;
import de.theredno.planc.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    @Getter
    private static GemsConfigManager gemsConfigManager;

    @Override
    public void onEnable() {
        instance = this;
        GemAPI api = new GemAPI(this);
        gemsConfigManager = new GemsConfigManager(this);
        createMenu menu = new createMenu(this);


        ItemBuilder.setPlugin(this);

        getServer().getPluginManager().registerEvents(new GemListener(), this);

        getServer().getPluginManager().registerEvents(new ArmorAbilityListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemAbilityListener(this), this);

        getCommand("give_all_items").setExecutor(new giveAllItems());
        getCommand("setlevel").setExecutor(new setLevel());
        getCommand("gem_menu").setExecutor(new GemsInvCommand());
        getCommand("addgem").setExecutor(new addGem());
        getCommand("addgem").setTabCompleter(new addGem());

        getCommand("getgem").setExecutor(new getGem());

        Gems.createGems();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
