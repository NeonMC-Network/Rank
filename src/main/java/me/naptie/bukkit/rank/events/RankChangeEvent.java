package me.naptie.bukkit.rank.events;

import me.naptie.bukkit.rank.objects.Rank;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RankChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private OfflinePlayer player;
    private Rank rank;

    public RankChangeEvent(OfflinePlayer player, Rank rank) {
        this.player = player;
        this.rank = rank;
    }

    public Player getPlayer() {
        return player.isOnline() ? (Player) player : null;
    }

    public OfflinePlayer getOfflinePlayer() {
        return player;
    }

    public Rank getRank() {
        return rank;
    }

}
