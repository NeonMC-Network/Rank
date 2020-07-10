package me.naptie.bukkit.rank.utils;

import me.naptie.bukkit.rank.Main;
import me.naptie.bukkit.rank.Messages;
import me.naptie.bukkit.player.utils.ConfigManager;
import me.naptie.bukkit.rank.events.RankChangeEvent;
import me.naptie.bukkit.rank.objects.Rank;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RankManager {

	public static Map<UUID, Rank> storage = new HashMap<>();

	public static void setRank(OfflinePlayer player, Rank rank) {
		storage.put(player.getUniqueId(), rank);
		synchronize();
		if (player.isOnline()) {
			String[] groups = {rank.name().toUpperCase()};
			//noinspection deprecation
			PermissionsEx.getUser((Player) player).setGroups(groups);
			String displayName = getPrefix(rank) + player.getName() + CU.t(rank.equals(Rank.MEMBER) || rank.equals(Rank.HACKER) ? "&7" : "&r");
			((Player) player).setPlayerListName(displayName);
			((Player) player).setDisplayName(displayName);
		}
		ConfigManager.getData(player).set("rank", rank.name().toUpperCase());
		try {
			ConfigManager.getData(player).save(ConfigManager.getDataFile(player));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.mySQLManager.getEditor().set(String.valueOf(player.getUniqueId()), rank.name().toUpperCase());
		Bukkit.getServer().getPluginManager().callEvent(new RankChangeEvent(player, rank));
	}

	public static Rank getRank(String rank) {
		if (rank.equalsIgnoreCase("member")) {
			return Rank.MEMBER;
		}
		if (rank.equalsIgnoreCase("hacker")) {
			return Rank.HACKER;
		}
		if (rank.equalsIgnoreCase("vip")) {
			return Rank.VIP;
		}
		if (rank.equalsIgnoreCase("vip+") || rank.equalsIgnoreCase("vipplus") || rank.equalsIgnoreCase("vip-plus")) {
			return Rank.VIPPlus;
		}
		if (rank.equalsIgnoreCase("mvp")) {
			return Rank.MVP;
		}
		if (rank.equalsIgnoreCase("mvp+") || rank.equalsIgnoreCase("mvpplus") || rank.equalsIgnoreCase("mvp-plus")) {
			return Rank.MVPPlus;
		}
		if (rank.equalsIgnoreCase("mvp++") || rank.equalsIgnoreCase("mvpplusplus") || rank.equalsIgnoreCase("mvp-plusplus") || rank.equalsIgnoreCase("mvp-plus-plus")) {
			return Rank.MVPPlusPlus;
		}
		if (rank.equalsIgnoreCase("up") || rank.equalsIgnoreCase("uploader")) {
			return Rank.UP;
		}
		if (rank.equalsIgnoreCase("helper")) {
			return Rank.HELPER;
		}
		if (rank.equalsIgnoreCase("mod") || rank.equalsIgnoreCase("moderator")) {
			return Rank.MOD;
		}
		if (rank.equalsIgnoreCase("admin") || rank.equalsIgnoreCase("administrator")) {
			return Rank.ADMIN;
		}
		if (rank.equalsIgnoreCase("owner")) {
			return Rank.OWNER;
		}
		return null;
	}

	public static Rank getRank(OfflinePlayer player) {
		if (Main.mySQLManager.getEditor().contains(String.valueOf(player.getUniqueId()))) {
			if (Main.mySQLManager.getEditor().get(String.valueOf(player.getUniqueId())) == null) {
				boolean existence = false;
				for (File file : Objects.requireNonNull(ConfigManager.playerDataFolder.listFiles())) {
					if (file.getName().contains(player.getUniqueId().toString())) {
						setRank(player, getRank(ConfigManager.getData(player).getString("rank")));
						existence = true;
						break;
					}
				}
				if (!existence)
					setRank(player, Rank.MEMBER);
			} else {
				storage.put(player.getUniqueId(), getRank(Main.mySQLManager.getEditor().get(String.valueOf(player.getUniqueId()))));
			}
			synchronize();
		} else {
			boolean existence = false;
			for (File file : Objects.requireNonNull(ConfigManager.playerDataFolder.listFiles())) {
				if (file.getName().contains(player.getUniqueId().toString())) {
					String rank = ConfigManager.getData(player).getString("rank");
					if (rank != null) {
						setRank(player, getRank(rank));
					} else {
						setRank(player, Rank.MEMBER);
						Main.logger.info(Messages.getMessage("zh-CN", "RANK_RESET").replace("%player%", ConfigManager.getData(player).getString("name")));
					}
					existence = true;
					break;
				}
			}
			if (!existence)
				setRank(player, Rank.MEMBER);
			/*if (player.isOnline()) {
				Player onlinePlayer = (Player) player;
				String[] groups = {"MEMBER"};
				//noinspection deprecation
				PermissionsEx.getUser(onlinePlayer).setGroups(groups);
				Bukkit.getServer().getPluginManager().callEvent(new RankChangeEvent(player, Rank.MEMBER));
			}
			storage.put(player.getUniqueId(), Rank.MEMBER);
			synchronize();
			ConfigManager.getData(player).set("rank", "MEMBER");
			Main.mySQLManager.getEditor().set(String.valueOf(player.getUniqueId()), "MEMBER");*/
		}
		return storage.get(player.getUniqueId());
	}

	public static boolean hasRank(OfflinePlayer player, Rank rank, boolean higher) {
		if (higher) return getLevel(getRank(player)) >= getLevel(rank);
		else return getRank(player).equals(rank);
	}

	public static boolean hasHigherRankThan(OfflinePlayer player, OfflinePlayer reference) {
		return getLevel(getRank(player)) >= getLevel(getRank(reference));
	}

	public static String getPrefix(OfflinePlayer player) {
		return getPrefix(getRank(player));
	}

	public static String getRankName(Player player) {
		return getRankName((OfflinePlayer) player);
	}

	private static String getRankName(OfflinePlayer player) {
		String prefix = getPrefix(player);
		if (prefix.contains("[") && prefix.contains("]")) {
			return prefix.replaceAll("[\\[\\]]", "");
		} else {
			return CU.t("&7MEMBER");
		}
	}

	private static int getLevel(Rank rank) {
		if (rank.equals(Rank.MEMBER)) {
			return 0;
		}
		if (rank.equals(Rank.HACKER)) {
			return 1;
		}
		if (rank.equals(Rank.VIP)) {
			return 2;
		}
		if (rank.equals(Rank.VIPPlus)) {
			return 3;
		}
		if (rank.equals(Rank.MVP)) {
			return 4;
		}
		if (rank.equals(Rank.MVPPlus)) {
			return 5;
		}
		if (rank.equals(Rank.MVPPlusPlus)) {
			return 6;
		}
		if (rank.equals(Rank.UP)) {
			return 7;
		}
		if (rank.equals(Rank.HELPER)) {
			return 8;
		}
		if (rank.equals(Rank.MOD)) {
			return 9;
		}
		if (rank.equals(Rank.ADMIN)) {
			return 10;
		}
		if (rank.equals(Rank.OWNER)) {
			return 11;
		}
		return -1;
	}

	public static String getPrefix(Rank rank) {
		if (rank.equals(Rank.MEMBER)) {
			return CU.t("&7");
		}
		if (rank.equals(Rank.HACKER)) {
			return CU.t("&5[HACKER] &5");
		}
		if (rank.equals(Rank.VIP)) {
			return CU.t("&a[VIP] &a");
		}
		if (rank.equals(Rank.VIPPlus)) {
			return CU.t("&a[VIP&6+&a] &a");
		}
		if (rank.equals(Rank.MVP)) {
			return CU.t("&b[MVP] &b");
		}
		if (rank.equals(Rank.MVPPlus)) {
			return CU.t("&b[MVP&c+&b] &b");
		}
		if (rank.equals(Rank.MVPPlusPlus)) {
			return CU.t("&6[MVP&c++&6] &6");
		}
		if (rank.equals(Rank.UP)) {
			return CU.t("&c[&fUP&c] &c");
		}
		if (rank.equals(Rank.HELPER)) {
			return CU.t("&9[HELPER] &9");
		}
		if (rank.equals(Rank.MOD)) {
			return CU.t("&2[MOD] &2");
		}
		if (rank.equals(Rank.ADMIN)) {
			return CU.t("&c[ADMIN] &c");
		}
		if (rank.equals(Rank.OWNER)) {
			return CU.t("&c[OWNER] &c");
		}
		return "";
	}

	static boolean checkNull() {
		File[] files = Main.playerDataFolder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getName().endsWith(".yml")) {
					String uuid = file.getName().replace(".yml", "");
					OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
					Rank rank = RankManager.getRank(player);
					if (rank == null) return false;
				}
			}
		}
		return true;
	}

	public static void synchronize() {
		for (UUID uuid : storage.keySet()) {
			Rank rank = storage.get(uuid);
			me.naptie.bukkit.player.utils.RankManager.storage.put(uuid, rank.name().toUpperCase());
		}
	}

	/*public static void synchronize() {
		for (UUID uuid : storage.keySet()) {
			for (Rank rank : storage.values()) {
				if (storage.get(uuid).equals(rank))
					me.naptie.bukkit.player.utils.RankManager.storage.put(uuid, rank.name().toUpperCase());
			}
		}
	}*/

	/*@SuppressWarnings("deprecation")
	public static void updateTablist(Player target) {
		try {
			Scoreboard scoreboard = target.getScoreboard() != null ? target.getScoreboard() : Bukkit.getScoreboardManager().getNewScoreboard();
			for (Player player : Bukkit.getOnlinePlayers()) {
				int tags = PermissionsEx.getUser(player).getGroups().length - 1;
				String rank = CU.t(PermissionsEx.getUser(player).getGroups()[tags].getPrefix());
				if (rank.length() > 16) {
					rank = rank.substring(0, 16);
				}
				String n = getRankPriority(getRank(player));
				String id = player.getUniqueId().toString().replace("-", "").substring(0, 3);
				Team team = scoreboard.getTeam(n + id);
				if (team == null) {
					team = scoreboard.registerNewTeam(n + id);
				}
				team.setPrefix(rank);
				team.addPlayer(player);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
