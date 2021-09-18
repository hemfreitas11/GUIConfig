package me.bkrmt.bkcore.guiconfig;

import me.bkrmt.bkcore.bkgui.MenuSound;
import me.bkrmt.bkcore.bkgui.gui.Rows;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;
import me.bkrmt.bkcore.config.ConfigUpdater;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.bkcore.xlibs.XMaterial;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GUISession {
    private final Player player;
    private final Configuration config;
    private final HashMap<String, List<String>> comments;

    public GUISession(Player player, Configuration config) {
        this.player = player;
        this.config = config;
        comments = new HashMap<>();
        loadComments(config.getFile());
        SectionMenu sectionMenu = new SectionMenu(this, player, "", config);
        sectionMenu.getMenu()
                .setGuiTitle("§8§lConfig Main Page")
                .setListRows(3)
                .setStartingSlot(11)
                .setListRowSize(5)
                .setButtonSlots(18, 26)
                .setGuiRows(Rows.SIX)
                .buildMenu()
                .getPages()
                .forEach(
                        page -> page.getGuiSettings().setPageWipeCloseResponse(event -> GUIConfig.getGuiSessions().remove(event.getPlayer().getUniqueId()))
                );
        List<String> reloadLore = new ArrayList<>();
        reloadLore.add(" ");
        reloadLore.add("§7Click here to reload all the configs");
        reloadLore.add("§7and messages of the plugin.");
        reloadLore.add(" ");
        reloadLore.add("§7If you changed the language option you need to");
        reloadLore.add("§7restart or reload the server for it to take effect,");
        reloadLore.add("§7this option won't work.");
        sectionMenu.getMenu().getPages().get(0).pageSetItem(49,
                new ItemBuilder(XMaterial.ENDER_EYE)
                        .setName("{shine gold bold}Reload Configs and Messages")
                        .setLore(reloadLore),
                player.getName().toLowerCase() + "-gui-config-section-reload-button",
                event -> {
                    MenuSound.SUCCESS.play(event.getWhoClicked());
                    GUIConfig.getInstance().getConfigManager().reloadConfigs();
                    GUIConfig.getInstance().getLangFile().reloadMessages();
                    sectionMenu.getMenu().getPages().get(0).displayItemMessage(
                            event.getSlot(),
                            2,
                            ChatColor.GREEN,
                            "§aAll configs and messages have been reloaded!",
                            null
                    );
                });
        sectionMenu.getMenu().getPages().get(0).openGui(player);
    }

    public Player getPlayer() {
        return player;
    }

    private void loadComments(File configFile) {
        BufferedReader newReader;
        try {
            newReader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        List<String> newLines = null;
        boolean error = false;
        try {
            newLines = newReader.lines().collect(Collectors.toList());
        } catch (Exception e) {
            try {
                newReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        } finally {
            try {
                newReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                error = true;
            }
        }

        if (error) return;

        if (newLines != null) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            boolean isLang = configFile.getPath().contains("lang") && configFile.getName().endsWith(".yml");
            boolean startAdding = false;
            try {
                Yaml yaml = new Yaml();
                Map<String, String> comments = ConfigUpdater.parseComments(newLines, new ArrayList<>(), config, yaml);
                for (String key : comments.keySet()) {
                    String commentRaw = comments.get(key);
                    String[] commentLines = StringUtils.chomp(commentRaw).replace("\r", "").split("\n");
                    List<String> lore = new ArrayList<>();
                    for (String commentLine : commentLines) {
                        if (!StringUtils.isBlank(commentLine) && !StringUtils.isWhitespace(commentLine)) {
                            if (isLang) {
                                if (startAdding) {
                                    if (!commentLine.contains("----")) {
                                        lore.add("§7" + commentLine.replace("#", "").trim());
                                    }
                                } else {
                                    if (commentLine.contains("discord.gg/2MHgyjCuPc")) {
                                        startAdding = true;
                                        lore.add("§7" + commentLine.replace("#", "").trim());
                                    }
                                }
                            } else {
                               lore.add("§7" + commentLine.replace("#", "").trim());
                            }
                        }
                    }
                    getComments().put(key, lore);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Configuration getConfig() {
        return config;
    }

    public HashMap<String, List<String>> getComments() {
        return comments;
    }
}
