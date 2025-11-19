package de.theredno.planc.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

public class ATMCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return true;

        Location location = player.getLocation();
        World world = player.getWorld();

        Villager v = world.spawn(location, Villager.class);

        v.setCustomName(ChatColor.AQUA + "Banker");
        v.setCustomNameVisible(true);

        v.setProfession(Villager.Profession.LIBRARIAN);
        v.setVillagerType(Villager.Type.PLAINS);

        v.setAI(false);
        v.setInvulnerable(true);
        v.setPersistent(true);

        return true;
    }
}
