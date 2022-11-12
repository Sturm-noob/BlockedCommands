package me.sturm.blockedcommands.api;

import me.sturm.blockedcommands.command.BlockedCommand;
import me.sturm.blockedcommands.command.CommandListener;
import me.sturm.blockedcommands.Utils;
import me.sturm.blockedcommands.context.Context;
import me.sturm.blockedcommands.context.StringContext;

import java.util.Map;

public class BlockedCommandsAPI {

    private static BlockedCommandsAPI api;
    private CommandListener listener;
    private boolean isPAPI;

    public BlockedCommandsAPI(CommandListener listener, boolean isPAPI) {
        this.listener = listener;
        this.isPAPI = isPAPI;
        api = this;
    }

    public static BlockedCommandsAPI getBlockedCommandsAPI() {
        return api;
    }

    public void registerContext(BlockedCommand command, Context context) {
        listener.addContext(command, context, true);
    }

    public boolean removeCommand(BlockedCommand command) {
        return listener.removeCommand(command);
    }

    public void setBlockedMessage(BlockedCommand command, String message) {
        listener.setBlockedMessage(command, message);
    }

    public void setBlockedMessage(BlockedCommand command, Context context, String message) {
        listener.setBlockedMessage(command, context, message);
    }

    public Map<Context, String> getCommandMessages(BlockedCommand command) {
        return listener.getCommandMessages(command);
    }

    public BlockedCommand parseCommandFromString(String command) {
        return BlockedCommand.parseFromString(command);
    }

    public Context getContextByID(String id) {
        return listener.getContext(id);
    }

    public StringContext contextFromString(String name, String string) {
        return new StringContext(name, string, isPAPI);
    }

    public boolean parseExpression(String expression) {
        return Utils.parseLogicString(expression);
    }

}
