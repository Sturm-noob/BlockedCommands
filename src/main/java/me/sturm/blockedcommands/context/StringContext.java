package me.sturm.blockedcommands.context;

import me.clip.placeholderapi.PlaceholderAPI;
import me.sturm.blockedcommands.Utils;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class StringContext implements Context {

    private boolean isPAPI;
    private String context;
    private String name;

    public StringContext(String name, String context, boolean isPAPI) {
        this.name = name;
        this.context = context;
        this.isPAPI = isPAPI;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getRawContext() {
        return context;
    }

    @Override
    public boolean onContextRequest(Player player) {
        Location location = player.getLocation();
        String finalContext = context.replace("%player%", player.getName())
                .replace("%x%", location.getX()+"")
                .replace("%y%", location.getY()+"")
                .replace("%z%", location.getZ()+"")
                .replace("%block-x%", location.getBlockX()+"")
                .replace("%block-y%", location.getBlockY()+"")
                .replace("%block-z%", location.getBlockZ()+"")
                .replace("%world%", location.getWorld().getName())
                .replace("%level%", player.getLevel()+"")
                .replace("%health%", player.getHealth()+"")
                .replace("%food%", player.getFoodLevel()+"")
                .replace("%deaths%", player.getStatistic(Statistic.DEATHS)+"")
                .replace("%kills%", player.getStatistic(Statistic.PLAYER_KILLS)+"");
        try {
            finalContext = finalContext.replace("%played-server%", player.getStatistic(Statistic.PLAY_ONE_TICK)+"");
        }
        catch (NoSuchFieldError exception) {
            finalContext = finalContext.replace("%played-server%", player.getStatistic(Statistic.valueOf("PLAY_ONE_MINUTE"))+"");
        }
        if (isPAPI) finalContext = PlaceholderAPI.setPlaceholders(player, finalContext);
        return Utils.parseLogicString(finalContext);
    }
}
