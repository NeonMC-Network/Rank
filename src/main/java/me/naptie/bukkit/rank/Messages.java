package me.naptie.bukkit.rank;

import me.naptie.bukkit.player.utils.ConfigManager;
import me.naptie.bukkit.rank.utils.CU;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Messages {

    public static final String MYSQL_CONNECTED = me.naptie.bukkit.player.utils.CU.t("[" + Main.instance.getDescription().getName() + "] " + getMessage("zh-CN", "MYSQL_CONNECTED"));

    public static String getMessage(String language, String message) {
        return CU.t(YamlConfiguration.loadConfiguration(new File(Main.instance.getDataFolder(), language + ".yml")).getString(message));
    }

    public static String getMessage(OfflinePlayer player, String message) {
        return CU.t(getLanguage(player).getString(message));
    }

    private static YamlConfiguration getLanguage(OfflinePlayer player) {
        File locale = new File(Main.instance.getDataFolder(), ConfigManager.getLanguageName(player) + ".yml");
        return YamlConfiguration.loadConfiguration(locale);
    }

}
