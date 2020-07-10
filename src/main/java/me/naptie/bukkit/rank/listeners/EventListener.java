package me.naptie.bukkit.rank.listeners;

import me.naptie.bukkit.rank.objects.Rank;
import me.naptie.bukkit.rank.utils.CU;
import me.naptie.bukkit.rank.utils.RankManager;
import me.naptie.bukkit.core.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class EventListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String prefix = RankManager.getPrefix(player);
		String suffix = CU.t(RankManager.getRank(player).equals(Rank.MEMBER) || RankManager.getRank(player).equals(Rank.HACKER) ? "&7" : "&r");
		String displayName = prefix + player.getName() + suffix;
		player.setPlayerListName(displayName);
		player.setDisplayName(displayName);
		String[] groups = {RankManager.getRank(player).name().toUpperCase()};
		//noinspection deprecation
		PermissionsEx.getUser(player).setGroups(groups);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (Main.getInstance().getServerName().contains("mini") || Main.getInstance().getServerName().contains("mega"))
			return;
		Player player = event.getPlayer();
		String prefix = RankManager.getPrefix(player);
		String suffix = CU.t(RankManager.getRank(player).equals(Rank.MEMBER) || RankManager.getRank(player).equals(Rank.HACKER) ? "&7" : "&r");
		String displayName = prefix + player.getName() + suffix;
		player.setDisplayName(displayName);
	}

}
