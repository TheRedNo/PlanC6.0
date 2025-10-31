package de.theredno.planc;

import de.theredno.planc.api.GemAPI;
import de.theredno.planc.commands.GemsInvCommand;
import de.theredno.planc.commands.giveAllItems;
import de.theredno.planc.commands.setLevel;
import de.theredno.planc.listeners.GemListener;
import de.theredno.planc.menu.createMenu;
import de.theredno.planc.util.Gems;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        GemAPI api = new GemAPI(this);

        createMenu menu = new createMenu(this);

        getServer().getPluginManager().registerEvents(new GemListener(), this);

        getCommand("give_all_items").setExecutor(new giveAllItems());
        getCommand("setlevel").setExecutor(new setLevel());
        getCommand("gem_menu").setExecutor(new GemsInvCommand());

        Gems.createGems();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getInstance() {
        return instance;
    }
}
