package me.bkrmt.bkcore.guiconfig;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GUIConfig {
    private static BkPlugin instance;
    private final static ConcurrentHashMap<UUID, GUISession> guiSessions = new ConcurrentHashMap<>();

    public static void openMenu(BkPlugin plugin, Player player, Configuration config) {
        if (instance == null) {
            instance = plugin;
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onLeave(PlayerQuitEvent event) {
                    Player player = event.getPlayer();
                    guiSessions.remove(player.getUniqueId());
                }
            }, instance);
        }
        guiSessions.put(player.getUniqueId(), new GUISession(player, config));
    }

    public static ConcurrentHashMap<UUID, GUISession> getGuiSessions() {
        return guiSessions;
    }

    public static BkPlugin getInstance() {
        return instance;
    }
}
