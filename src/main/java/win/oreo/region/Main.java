package win.oreo.region;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.region.command.RegionCommand;
import win.oreo.region.command.RegionCompleter;
import win.oreo.region.listener.InventoryListener;
import win.oreo.region.listener.RegionListener;
import win.oreo.region.manager.YmlManager;
import win.oreo.region.region.RegionUtil;
import win.oreo.region.util.Color;

public final class Main extends JavaPlugin {
    public YmlManager ymlManager;
    private RegionUtil regionUtil;

    @Override
    public void onEnable() {
        this.regionUtil = new RegionUtil();
        Bukkit.getConsoleSender().sendMessage(getConfigMessage("messages.plugin-enable"));
        getCommand("region").setExecutor(new RegionCommand());
        getCommand("region").setTabCompleter(new RegionCompleter());
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new RegionListener(), this);
        ymlManager = new YmlManager(this);
        regionUtil.initialize();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(getConfigMessage("messages.plugin-disable"));
        regionUtil.save();
    }

    public static String getConfigMessage(String path) {
        FileConfiguration config = JavaPlugin.getPlugin(Main.class).getConfig();
        String text = config.getString(path);
        String prefix = config.getString("prefix");
        if (text == null) {
            return ChatColor.RED +"ERROR";
        }
        return Color.format(prefix + " " + text);
    }

    public static String getConfigMessage(String path, String[] args) {
        FileConfiguration config = JavaPlugin.getPlugin(Main.class).getConfig();
        String text = config.getString(path);
        String prefix = config.getString("prefix");
        if (text == null) {
            return ChatColor.RED +"ERROR";
        }

        boolean open = false;
        StringBuilder chars = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c == '%') {
                if (open) {
                    final char[] CHARACTERS = chars.toString().toCharArray();
                    if (CHARACTERS[0] == 'a' && CHARACTERS[1] == 'r' && CHARACTERS[2] == 'g') {
                        final int ARG = Integer.parseInt(String.valueOf(CHARACTERS[3]));

                        text = text.replace(chars.toString(), args[ARG]);

                        chars = new StringBuilder();
                    }
                    open = false;
                } else {
                    open = true;
                }
                continue;
            }

            if (open) {
                chars.append(c);
            }
        }

        return Color.format(prefix + " " + text.replace("%", ""));
    }
}
