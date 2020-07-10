package me.naptie.bukkit.rank.utils;

import org.bukkit.ChatColor;

public class CU {

    public static String t(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String t(int integer) {
        return ChatColor.translateAlternateColorCodes('&', integer + "");
    }

}
