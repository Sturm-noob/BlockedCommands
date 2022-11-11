package me.sturm.blockedcommands.command;

import me.sturm.blockedcommands.Utils;
import me.sturm.blockedcommands.context.Context;
import me.sturm.blockedcommands.context.StringContext;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CommandListener implements Listener, CommandExecutor, TabCompleter {

    private final Map<BlockedCommand, List<Context>> contexts = new HashMap<>();
    private final Map<BlockedCommand, List<Context>> contextsFromOtherPlugins = new HashMap<>();
    private final Map<String, Context> contextMap = new HashMap<>();
    private final Map<String, Context> contextsMapFromOtherPlugins = new HashMap<>();
    private final Map<BlockedCommand, String> messages = new HashMap<>();
    private final boolean isPAPI;
    private final Plugin plugin;

    public CommandListener(Plugin plugin, boolean isPAPI) {
        this.isPAPI = isPAPI;
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandExecute(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        BlockedCommand cmd = BlockedCommand.parseFromString(event.getMessage().substring(1));
        PluginCommand plc = Bukkit.getPluginCommand(cmd.getCommandName());
        if (plc != null) cmd.setPlugin(plc.getPlugin().getName());
        List<Context> commandContexts = this.contexts.get(cmd);
        while (commandContexts == null && cmd.cutArg()) {
            commandContexts = this.contexts.get(cmd);
        }
        if (commandContexts == null || commandContexts.isEmpty()) return;
        boolean isCancelled = commandContexts.stream().anyMatch(context -> context.onContextRequest(player));
        if (isCancelled) {
            event.setCancelled(true);
            if (messages.containsKey(cmd)) player.sendMessage(messages.get(cmd));
        }
    }

    public void load() {
        contexts.clear();
        contextMap.clear();
        messages.clear();
        plugin.reloadConfig();
        loadContexts(plugin.getConfig().getConfigurationSection("contexts"));
        loadCommands(plugin.getConfig().getConfigurationSection("commands"));
    }

    void loadContexts(ConfigurationSection sec) {
        for (String id : sec.getKeys(false)) {
            this.contextMap.put(id, new StringContext(id, sec.getString(id), isPAPI));
        }
        this.contextMap.putAll(this.contextsMapFromOtherPlugins);
    }

    void loadCommands(ConfigurationSection sec) {
        for (String cmd : sec.getKeys(false)) {
            try {
                List<Context> contextsCmd = new ArrayList<>();
                String message = null;
                if (sec.isString(cmd)) contextsCmd.add(getContextOrError(sec.getString(cmd)));
                else if (sec.isConfigurationSection(cmd)) {
                    sec.getStringList(cmd+".contexts").forEach(contextID -> contextsCmd.add(getContextOrError(contextID)));
                    message = ChatColor.translateAlternateColorCodes('&', sec.getString(cmd+".message"));
                }
                else sec.getStringList(cmd).forEach(contextID -> contextsCmd.add(getContextOrError(contextID)));
                BlockedCommand command = BlockedCommand.parseFromString(cmd);
                if (message != null) messages.put(command, message);
                this.contexts.put(command, contextsCmd);
            }
            catch (NullPointerException exception) {
                Bukkit.getLogger().info("Error command load: " + cmd);
                exception.printStackTrace();
            }
        }
        this.contextsFromOtherPlugins.forEach((cmd, contexts) -> {
            this.contexts.computeIfAbsent(cmd, k -> new ArrayList<>()).addAll(contexts);
        });
    }

    public Context getContextOrError(String contextID) throws NullPointerException {
        Context context = getContext(contextID);
        if (context != null) return context;
        else throw new NullPointerException("Error context name: " + contextID);
    }

    public void setBlockedMessage(BlockedCommand command, String message) {
        this.messages.put(command, message);
    }

    public void addContext(BlockedCommand command, Context context, boolean fromOtherPlugin) {
        Objects.requireNonNull(context.getName());
        contextMap.put(context.getName(), context);
        contexts.computeIfAbsent(command, k -> new ArrayList<>()).add(context);
        if (fromOtherPlugin) {
            this.contextsMapFromOtherPlugins.put(context.getName(), context);
            this.contextsFromOtherPlugins.computeIfAbsent(command, k -> new ArrayList<>()).add(context);
        }
    }

    public Context getContext(String id) {
        return contextMap.get(id);
    }

    public boolean removeCommand(BlockedCommand command) {
        this.messages.remove(command);
        this.contextsFromOtherPlugins.remove(command);
        return this.contexts.remove(command) != null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.hasPermission("blockedcommands.reload"))) {
            sender.sendMessage(ChatColor.RED + "No permission");
            return true;
        }
        this.load();
        sender.sendMessage(ChatColor.GREEN + "Reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission("blockedcommands.reload")) result.add("reload");
        return result;
    }
}
