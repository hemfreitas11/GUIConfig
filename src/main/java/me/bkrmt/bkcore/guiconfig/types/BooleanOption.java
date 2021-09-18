package me.bkrmt.bkcore.guiconfig.types;

import me.bkrmt.bkcore.PagedList;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.bkgui.MenuSound;
import me.bkrmt.bkcore.bkgui.event.ElementResponse;
import me.bkrmt.bkcore.bkgui.page.Page;
import me.bkrmt.bkcore.guiconfig.ConfigEntry;
import me.bkrmt.bkcore.guiconfig.ConfigKey;
import me.bkrmt.bkcore.xlibs.XMaterial;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class BooleanOption extends ConfigKey {
    public BooleanOption(ConfigEntry configEntry, String name, List<String> description) {
        super(configEntry,
                () -> cleanName(name),
                () -> {
                    List<String> translatedLore = new ArrayList<>();
                    if (description != null) {
                        List<String> wrappedLore = Utils.wrapLore(description, 45);
                        if (!Utils.isBlank(wrappedLore))
                            translatedLore.add(" ");
                        wrappedLore.forEach(line -> {
                            line = "§7" + line.replace("#", "");
                            if (!StringUtils.isWhitespace(line)) {
                                translatedLore.add(line);
                            }
                        });
                        if (!Utils.isBlank(translatedLore)) translatedLore.add(" ");
                    }
                    translatedLore.add("§7Current Value: " + configEntry.getValue());
                    return translatedLore;
                },
                () -> (boolean) configEntry.getValue() ? XMaterial.GREEN_STAINED_GLASS.parseItem() : XMaterial.RED_STAINED_GLASS.parseItem()
        );
    }

    @Override
    public ElementResponse getElementResponse(PagedList list, Page currentPage) {
        return event -> {
            Object currentValue = getConfigEntry().getValue();
            boolean newValue = !(boolean) currentValue;
            getConfigEntry().setValue(newValue);
            saveValue();
            updateLore();
            if (newValue)
                MenuSound.SUCCESS.play(event.getWhoClicked());
            else
                MenuSound.WARN.play(event.getWhoClicked());

            updateDisplayItem();
            list.updateItem(getID(), this);
            currentPage.displayItemMessage(event.getSlot(), 2, ChatColor.GREEN, "§aChanged to: " + (newValue ? "§atrue" : "§cfalse"), null);
        };
    }
}
