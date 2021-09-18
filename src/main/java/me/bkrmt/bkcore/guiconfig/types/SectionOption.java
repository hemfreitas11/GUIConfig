package me.bkrmt.bkcore.guiconfig.types;

import me.bkrmt.bkcore.PagedList;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.bkgui.event.ElementResponse;
import me.bkrmt.bkcore.bkgui.gui.Rows;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;
import me.bkrmt.bkcore.bkgui.page.Page;
import me.bkrmt.bkcore.guiconfig.ConfigEntry;
import me.bkrmt.bkcore.guiconfig.ConfigKey;
import me.bkrmt.bkcore.guiconfig.GUIConfig;
import me.bkrmt.bkcore.guiconfig.SectionMenu;
import me.bkrmt.bkcore.xlibs.XMaterial;
import me.bkrmt.bkcore.xlibs.XSound;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SectionOption extends ConfigKey {
    private PagedList sectionList;

    public SectionOption(ConfigEntry configEntry, String name, List<String> description) {
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
                    }
                    return translatedLore;
                },
                XMaterial.CHEST::parseItem
        );
    }

    @Override
    public ElementResponse getElementResponse(PagedList list, Page currentPage) {
        return event -> {
            XSound.BLOCK_CHEST_OPEN.play(event.getWhoClicked(), 0.4f, 1f);
            if (sectionList == null) {
                Player player = (Player) event.getWhoClicked();
                SectionMenu sectionMenu = new SectionMenu(GUIConfig.getGuiSessions().get(player.getUniqueId()), player, getConfigEntry().getKey(), getConfigEntry().getConfig());
                sectionList = sectionMenu.getMenu()
                        .setGuiTitle("§8§l" + ChatColor.stripColor(getProperties().getDisplayName().buildName()))
                        .setListRows(3)
                        .setStartingSlot(11)
                        .setListRowSize(5)
                        .setGuiRows(Rows.FIVE)
                        .buildMenu();
                sectionList.getPages().forEach(page -> {
                    page.getGuiSettings().setPageWipeCloseResponse(event1 -> GUIConfig.getGuiSessions().remove(event1.getPlayer().getUniqueId()));
                });
                currentPage.addNextMenu(sectionList.getPages().get(0));
                sectionList.getPages().get(0).addPreviousMenu(currentPage)
                        .setBackMenuButton(
                                18,
                                new ItemBuilder(XMaterial.RED_WOOL)
                                        .setName("{shine red bold} Previous Menu")
                                        .setLore("§7Click here to return to the previous menu."),
                                player.getName().toLowerCase() + "-guiconfig-sectionmenu-back-button-key-" + getConfigEntry().getKey(),
                                event1 -> {
                                    XSound.BLOCK_CHEST_CLOSE.play(event.getWhoClicked(), 0.4f, 1f);
                                    sectionList.getPages().get(0).setSwitchingPages(true);
                                    currentPage.openGui(player);
                                }
                        );
                currentPage.setSwitchingPages(true);
                sectionList.getPages().get(0).openGui(player);
            } else {
                currentPage.setSwitchingPages(true);
                sectionList.openPage(0);
            }
        };
    }
}
