package me.naptie.bukkit.rank.objects;

import org.bukkit.ChatColor;

public enum Rank {
    MEMBER("x", ChatColor.GRAY),
    HACKER("y", ChatColor.DARK_PURPLE),
    VIP("t", ChatColor.GREEN),
    VIPPlus("s", ChatColor.GREEN),
    MVP("r", ChatColor.AQUA),
    MVPPlus("q", ChatColor.AQUA),
    MVPPlusPlus("p", ChatColor.GOLD),
    UP("l", ChatColor.RED),
    HELPER("e", ChatColor.BLUE),
    MOD("c", ChatColor.DARK_GREEN),
    ADMIN("b", ChatColor.RED),
    OWNER("a", ChatColor.RED);

    private String priority;
    private ChatColor color;

    Rank(String priority, ChatColor color) {
        this.priority = priority;
        this.color = color;
    }

    public String getPriority() {
        return this.priority;
    }

    public ChatColor getColor() {
        return this.color;
    }
}
