package de.theredno.planc.util;

import org.bukkit.entity.Player;

public interface ItemAbility {
    void execute(Player player);
    void remove (Player player);
}
