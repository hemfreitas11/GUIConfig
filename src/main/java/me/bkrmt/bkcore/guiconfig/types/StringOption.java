package me.bkrmt.bkcore.guiconfig.types;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.PagedList;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.bkgui.MenuSound;
import me.bkrmt.bkcore.bkgui.event.ElementResponse;
import me.bkrmt.bkcore.bkgui.page.Page;
import me.bkrmt.bkcore.guiconfig.ConfigEntry;
import me.bkrmt.bkcore.guiconfig.ConfigKey;
import me.bkrmt.bkcore.guiconfig.GUIConfig;
import me.bkrmt.bkcore.input.InputRunnable;
import me.bkrmt.bkcore.input.PlayerInput;
import me.bkrmt.bkcore.xlibs.XMaterial;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StringOption extends ConfigKey {
    public StringOption(ConfigEntry configEntry, String name, List<String> description) {
        super(configEntry,
                () -> cleanName(name),
                () -> {
                    List<String> translatedLore = new ArrayList<>();
                    if (description != null) {
                        List<String> wrappedLore;
                        if (name.equalsIgnoreCase("language")) {
                            wrappedLore = new ArrayList<>();
                            boolean startAdding = false;
                            for (String line : Utils.wrapLore(description, 45)) {
                                if (startAdding) {
                                    if (!line.contains("----")) {
                                        wrappedLore.add(line);
                                    }
                                }
                                else {
                                    if (line.contains("discord.gg/2MHgyjCuPc")) {
                                        startAdding = true;
                                        wrappedLore.add(line);
                                    }
                                }
                            }
                        } else {
                            wrappedLore = Utils.wrapLore(description, 45);
                        }
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
                    if (String.valueOf(configEntry.getValue()).length() > 40) {
                        for (String wrappedLine : Utils.wrappedToList((String) configEntry.getValue(), 45)) {
                            translatedLore.add("§7" + wrappedLine);
                        }
                    } else {
                        translatedLore.add("§7\"" + configEntry.getValue() + "§r§7\"");
                    }
                    return translatedLore;
                },
                XMaterial.STRING::parseItem
        );
    }

    @Override
    public ElementResponse getElementResponse(PagedList list, Page currentPage) {
        return event -> {
            Player player = (Player) event.getWhoClicked();
            MenuSound.CLICK.play(player);
            BkPlugin plugin = GUIConfig.getInstance();
            String cancelInput = plugin.getConfigManager().getConfig().getString("cancel-input");
            InputRunnable cancelRunnable = input -> {
                currentPage.openGui(player);
                currentPage.displayItemMessage(event.getSlot(), 2, ChatColor.RED, plugin.getLangFile().get((OfflinePlayer) event.getWhoClicked(), "error.input.canceled"), null);
            };
            currentPage.setSwitchingPages(true);
            new PlayerInput(plugin, player, currentPage, input -> {
                if (!input.equalsIgnoreCase(cancelInput)) {
                    getConfigEntry().setValue(input);
                    saveValue();
                    updateLore();
                    list.updateItem(getID(), this);
                    List<String> messageLore = new ArrayList<>();
                    messageLore.add("§aChanged to: ");
                    if (input.length() > 45) {
                        for (String line : Utils.wrappedToList(WordUtils.wrap(input, 45), 45)) {
                            if (!line.isEmpty())
                                messageLore.add("§a" + line);
                        }
                    } else {
                        messageLore.add("§a\"" + input + "\"");
                    }
                    MenuSound.SUCCESS.play(player);
                    currentPage.openGui(player);
                    currentPage.displayItemMessage(event.getSlot(), 2, ChatColor.GREEN, messageLore, null);
                }
            }, cancelRunnable)
                    .setTimeout(60, cancelRunnable)
                    .setCancellable(true)
                    .setTitle("§7Type the new value")
                    .setSubTitle("§7Type '§c" + cancelInput + "§7' to cancel")
                    .sendInput();
        };
    }
}
