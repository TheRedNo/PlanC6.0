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
import de.theredno.planc.WebDashboard.DataListener;
import de.theredno.planc.WebDashboard.DataTasks;
import de.theredno.planc.WebDashboard.DiamondSystem.DiamondListener;
import de.theredno.planc.WebDashboard.DiamondSystem.DiamondManager;
import de.theredno.planc.WebDashboard.DiamondSystem.DiamondTask;
import de.theredno.planc.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    @Getter
    private static GemsConfigManager gemsConfigManager;
    @Getter
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

        try {
            mysql.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        DataTasks.start();
        DiamondTask.start();

        getServer().getPluginManager().registerEvents(new GemListener(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);

        getServer().getPluginManager().registerEvents(new ArmorAbilityListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemAbilityListener(this), this);

        getServer().getPluginManager().registerEvents(new OnDispense(), this);

        getServer().getPluginManager().registerEvents(new DataListener(), this);

        getServer().getPluginManager().registerEvents(new GameListener(), this);

        getServer().getPluginManager().registerEvents(new DiamondListener(), this);


        getCommand("give_all_items").setExecutor(new giveAllItems());
        getCommand("setlevel").setExecutor(new setLevel());
        getCommand("gem_menu").setExecutor(new GemsInvCommand());
        getCommand("addgem").setExecutor(new addGem());
        getCommand("addgem").setTabCompleter(new addGem());
        getCommand("createcrate").setExecutor(new GiveCreateCommand());
        getCommand("tpa").setExecutor(new TpaCommand());

        getCommand("getgem").setExecutor(new getGem());

        getCommand("dashboard").setExecutor(new DashboardCommand());
        getCommand("account").setExecutor(new AccountCommand());



        getServer().getPluginManager().registerEvents(new CrateListener(), this);

        Gems.createGems();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        mysql.disconnect();
    }

}
