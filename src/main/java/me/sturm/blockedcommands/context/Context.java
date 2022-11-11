package me.sturm.blockedcommands.context;

import org.bukkit.entity.Player;

public interface Context {

    public boolean onContextRequest(Player player);
    public String getName();

}
