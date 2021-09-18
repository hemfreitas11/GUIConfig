package me.bkrmt.bkcore.guiconfig.types;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.PagedList;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.bkgui.BkGUI;
import me.bkrmt.bkcore.bkgui.MenuSound;
import me.bkrmt.bkcore.bkgui.event.ElementResponse;
import me.bkrmt.bkcore.bkgui.menus.numberinput.Modifier;
import me.bkrmt.bkcore.bkgui.menus.numberinput.NumberInputMenu;
import me.bkrmt.bkcore.bkgui.page.Page;
import me.bkrmt.bkcore.guiconfig.ConfigEntry;
import me.bkrmt.bkcore.guiconfig.ConfigKey;
import me.bkrmt.bkcore.xlibs.XMaterial;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NumberOption extends ConfigKey {
    private final Object numberType;
    public NumberOption(ConfigEntry configEntry, String name, List<String> description, Object numberType) {
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
                    translatedLore.add("§7Current Value: ");
                    translatedLore.add("§7" + configEntry.getValue());
                    return translatedLore;
                },
                () -> (isInteger(numberType) ? XMaterial.REPEATER.parseItem() : XMaterial.COMPARATOR.parseItem())
        );
        this.numberType = numberType;
    }

    @Override
    public ElementResponse getElementResponse(PagedList list, Page currentPage) {
        return event -> {
            Player player = (Player) event.getWhoClicked();
            MenuSound.CLICK.play(player);
            BkPlugin plugin = BkGUI.INSTANCE.getInstance();

            NumberInputMenu inputMenu = new NumberInputMenu(
                    player,
                    "§8§l" + ChatColor.stripColor(getProperties().getDisplayName().buildName()),
                    (value, menu, event1) -> {
                        menu.setWipeOnlySelf(true);
                        getConfigEntry().setValue(isInteger(numberType) ? Integer.parseInt(String.valueOf(value)) : value);
                        saveValue();
                        updateLore();
                        list.updateItem(getID(), this);
                        List<String> messageLore = new ArrayList<>();
                        messageLore.add("§aChanged to: ");
                        messageLore.add("§a" + value);
                        MenuSound.SUCCESS.play(player);
                        currentPage.openGui(player);
                        currentPage.displayItemMessage(event.getSlot(), 2, ChatColor.GREEN, messageLore, null);
                    },
                    String.valueOf(getConfigEntry().getValue()),
                    numberType
            )
                    .setValueDispay(XMaterial.WRITABLE_BOOK.parseItem(), 0)
                    .setModifiers(
                            new Modifier(24, XMaterial.ORANGE_STAINED_GLASS_PANE.parseItem(), (isInteger(numberType) ? "1" : "0.1")),
                            new Modifier(25, XMaterial.ORANGE_STAINED_GLASS.parseItem(), (isInteger(numberType) ? "10" : "0.5")),
                            new Modifier(26, XMaterial.BROWN_STAINED_GLASS.parseItem(), (isInteger(numberType) ? "100" : "1")),
                            new Modifier(20, XMaterial.LIME_STAINED_GLASS_PANE.parseItem(), (isInteger(numberType) ? "-1" : "-0.1")),
                            new Modifier(19, XMaterial.LIME_STAINED_GLASS.parseItem(), (isInteger(numberType) ? "-10" : "-0.5")),
                            new Modifier(18, XMaterial.GREEN_STAINED_GLASS.parseItem(), (isInteger(numberType) ? "-100" : "-1"))
                    )
                    .buildMenu();
            currentPage.setSwitchingPages(true);
            inputMenu.getMenu()
                    .addPreviousMenu(currentPage)
                    .openGui(player);
        };
    }

    private static boolean isInteger(Object numberType) {
        return numberType instanceof Integer || numberType instanceof Long;
    }
}
