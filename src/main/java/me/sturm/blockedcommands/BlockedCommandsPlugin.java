package me.sturm.blockedcommands;

import me.sturm.blockedcommands.api.BlockedCommandsAPI;
import me.sturm.blockedcommands.command.CommandListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockedCommandsPlugin extends JavaPlugin {

    private CommandListener listener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        boolean isPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        listener = new CommandListener(this, isPAPI);
        listener.load();
        Bukkit.getPluginManager().registerEvents(listener, this);
        getCommand("blockedcommands").setExecutor(listener);
        getCommand("blockedcommands").setTabCompleter(listener);
        new BlockedCommandsAPI(listener, isPAPI);
    }
}
