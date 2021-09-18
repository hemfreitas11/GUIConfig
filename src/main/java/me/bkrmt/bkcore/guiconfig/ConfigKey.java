package me.bkrmt.bkcore.guiconfig;

import me.bkrmt.bkcore.AbstractItem;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.bkcore.properties.DisplayItemBuilder;
import me.bkrmt.bkcore.properties.DisplayLoreBuilder;
import me.bkrmt.bkcore.properties.DisplayNameBuilder;

public abstract class ConfigKey extends AbstractItem {
    private final ConfigEntry configEntry;

    protected ConfigKey(ConfigEntry configEntry, DisplayNameBuilder displayName, DisplayLoreBuilder lore, DisplayItemBuilder displayItem) {
        super(-1, -1, displayName, lore, displayItem);
        this.configEntry = configEntry;
    }

    public static String cleanName(String name) {
        return "§7§l" + Utils.capitalize(name.replace("-", " ").replace("_", " "));
    }

    public ConfigEntry getConfigEntry() {
        return configEntry;
    }

    public void saveValue() {
        Configuration config = configEntry.getConfig();
        config.set(configEntry.getKey(), configEntry.getValue());
        config.saveToFile();
    }
}
