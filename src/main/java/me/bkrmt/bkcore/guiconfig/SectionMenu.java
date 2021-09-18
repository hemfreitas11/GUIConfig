package me.bkrmt.bkcore.guiconfig;

import me.bkrmt.bkcore.PagedItem;
import me.bkrmt.bkcore.PagedList;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.bkcore.guiconfig.types.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.List;
import java.util.UUID;

public class SectionMenu {
    private final PagedList menu;
    private final GUISession session;

    public SectionMenu(GUISession session, Player player, String key, Configuration config) {
        this.session = session;
        ConfigurationSection section = config.getConfigurationSection(key);
        ArrayDeque<PagedItem> configKeys = new ArrayDeque<>();
        UUID uuid = player.getUniqueId();
        for (String stringKey : section.getKeys(false)) {
            String[] tempSplit = stringKey.split("\\.");
            String keyName = tempSplit[tempSplit.length - 1];
            String fullKey = (!section.getCurrentPath().isEmpty() ? section.getCurrentPath() + "." : "") + stringKey;
            Object keyValue = section.get(stringKey);
            ConfigEntry configEntry = new ConfigEntry(uuid, fullKey, keyValue);
            if (section.get(stringKey) == null || section.isConfigurationSection(stringKey)) {
                configKeys.add(new SectionOption(
                        configEntry,
                        keyName,
                        session.getComments().get(fullKey)
                ));
            } else if (keyValue instanceof Boolean) {
                configKeys.add(new BooleanOption(
                        configEntry,
                        keyName,
                        session.getComments().get(fullKey)
                ));
            } else if (keyValue instanceof Integer || keyValue instanceof Long) {
                configKeys.add(new NumberOption(
                        configEntry,
                        keyName,
                        session.getComments().get(fullKey),
                        1
                ));
            } else if (keyValue instanceof Float || keyValue instanceof Double) {
                configKeys.add(new NumberOption(
                        configEntry,
                        keyName,
                        session.getComments().get(fullKey),
                        1f
                ));
            } else if (keyValue instanceof String) {
                configKeys.add(new StringOption(
                        configEntry,
                        keyName,
                        session.getComments().get(fullKey)
                ));
            } else if (keyValue instanceof List) {
                configKeys.add(new ListOption(
                        configEntry,
                        keyName,
                        session.getComments().get(fullKey)
                ));
            }/* else {
                try {
                    Location locationTest = config.getLocation(fullKey);
                    if (locationTest != null) {
                        configKeys.add(new BooleanOption(
                                configEntry,
                                keyName,
                                session.getComments().get(fullKey)
                        ));
                    }
                } catch (Exception ignored) {
                    try {
                        ItemStack itemTest = config.getItemStack(fullKey);
                        if (itemTest != null) {
                            configKeys.add(new BooleanOption(
                                    configEntry,
                                    keyName,
                                    session.getComments().get(fullKey)
                            ));
                        }
                    } catch (Exception ignored2) {}
                }
            }*/
        }

        this.menu = new PagedList(GUIConfig.getInstance(), player, player.getName().toLowerCase() + "-gui-config-section-" + key, configKeys);
    }

    public GUISession getSession() {
        return session;
    }

    public PagedList getMenu() {
        return menu;
    }
}
