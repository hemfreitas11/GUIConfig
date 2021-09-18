package me.bkrmt.bkcore.guiconfig;

import me.bkrmt.bkcore.config.Configuration;

import java.util.UUID;

public class ConfigEntry {
    private final String key;
    private Object value;
    private final UUID playerUUID;

    public ConfigEntry(UUID playerUUID, String key, Object value) {
        this.key = key;
        this.playerUUID = playerUUID;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public Configuration getConfig() {
        GUISession session = GUIConfig.getGuiSessions().get(playerUUID);
        if (session != null) return session.getConfig();
        else return null;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
