package me.sturm.blockedcommands.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockedCommand {

    private String plugin;
    private String commandName;
    private List<String> args;
    private int argsCount;

    public BlockedCommand(String plugin, String commandName, List<String> args, int argsCount) {
        assert commandName != null : "Command cannot be null!";
        this.plugin = plugin;
        this.commandName = commandName;
        this.args = args;
        this.argsCount = argsCount;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public String getCommandName() {
        return commandName;
    }

    public int getArgsCount() {
        return argsCount;
    }

    public List<String> getArgs() {
        return args;
    }


    public static BlockedCommand parseFromString(String string) {
        String[] bySpace = string.split(" ");
        String plugin = null;
        String commandName;
        List<String> args = new ArrayList<>();
        if (bySpace[0].contains(":")) {
            String[] pluginCmd = bySpace[0].split(":");
            plugin = pluginCmd[0].equals("") ? null : pluginCmd[0];
            commandName = pluginCmd[1];
        }
        else {
            commandName = bySpace[0];
        }
        for (int i = 1; i < bySpace.length; i++)
            args.add(bySpace[i]);
        int argsCount = bySpace.length - 1;
        return new BlockedCommand(plugin, commandName, args, argsCount);
    }

    @Override
    public int hashCode() {
        return commandName.toLowerCase().hashCode();
    }

    //Ахтунг! obj.plugin == null -> obj.plugin == any
    @Override
    public boolean equals(Object with) {
        if (!(with instanceof BlockedCommand)) return false;
        BlockedCommand otherCmd = (BlockedCommand) with;
        return (plugin == null || otherCmd.plugin == null || equalsIgnoreCase(plugin, otherCmd.plugin)) &&
                equalsIgnoreCase(commandName, otherCmd.commandName) &&
                args.equals(otherCmd.args);
    }

    public boolean cutArg() {
        if (argsCount == 0) return false;
        args.remove(argsCount - 1);
        argsCount--;
        return true;
    }

    public boolean isEqualsWithArgs(BlockedCommand otherCmd) {
        return (plugin == null || otherCmd.plugin == null || equalsIgnoreCase(plugin, otherCmd.plugin)) &&
                equalsIgnoreCase(commandName, otherCmd.commandName) &&
                argsCount <= otherCmd.argsCount &&
                args.equals(otherCmd.args.subList(0, argsCount));
    }

    public boolean equalsIgnoreCase(String obj1, String obj2) {
        return (obj1 == null && obj2 == null) || (obj1 != null && obj2 != null && obj1.equalsIgnoreCase(obj2));
    }

}
