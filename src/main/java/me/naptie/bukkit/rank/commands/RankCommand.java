package me.naptie.bukkit.rank.commands;

import me.naptie.bukkit.rank.Messages;
import me.naptie.bukkit.rank.Permissions;
import me.naptie.bukkit.rank.objects.Rank;
import me.naptie.bukkit.rank.utils.RankManager;
import me.naptie.bukkit.player.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (commandSender instanceof Player) {
			Player player = (Player) commandSender;
			if (player.hasPermission(Permissions.RANK)) {
				if (strings.length >= 2) {
					//noinspection deprecation
					OfflinePlayer target = Bukkit.getOfflinePlayer(strings[0]);
					if (hasPlayedBefore(target)) {
						Rank rank = RankManager.getRank(strings[1]);
						if (rank != null) {
							if (RankManager.hasRank(player, rank, true) && RankManager.hasHigherRankThan(player, target)) {
								RankManager.setRank(target, rank);
								player.sendMessage(Messages.getMessage(player, "SUCCESS").replace("%player%", target.getName()).replace("%rank%", rank.name().toUpperCase()));
							} else {
								player.sendMessage(Messages.getMessage(player, "PERMISSION_DENIED"));
							}
						} else {
							player.sendMessage(Messages.getMessage(player, "RANK_NOT_FOUND").replace("%rank%", strings[1]));
						}
					} else {
						player.sendMessage(Messages.getMessage(player, "PLAYER_NOT_FOUND").replace("%player%", strings[0]));
					}
				} else {
					player.sendMessage(Messages.getMessage(player, "USAGE").replace("%usage%", "/rank <player> <rank>"));
				}
			} else {
				player.sendMessage(Messages.getMessage(player, "PERMISSION_DENIED"));
			}
		} else if (commandSender instanceof ConsoleCommandSender) {
			if (strings.length >= 2) {
				//noinspection deprecation
				OfflinePlayer target = Bukkit.getOfflinePlayer(strings[0]);
				if (hasPlayedBefore(target)) {
					Rank rank = RankManager.getRank(strings[1]);
					if (rank != null) {
						RankManager.setRank(target, rank);
						commandSender.sendMessage(Messages.getMessage("zh-CN", "SUCCESS").replace("%player%", target.getName()).replace("%rank%", rank.name().toUpperCase()));
					} else {
						commandSender.sendMessage(Messages.getMessage("zh-CN", "RANK_NOT_FOUND").replace("%rank%", strings[1]));
					}
				} else {
					commandSender.sendMessage(Messages.getMessage("zh-CN", "PLAYER_NOT_FOUND").replace("%player%", strings[0]));
				}
			} else {
				commandSender.sendMessage(Messages.getMessage("zh-CN", "USAGE").replace("%usage%", "rank <player> <rank>"));
			}
		} else {
			commandSender.sendMessage(Messages.getMessage("zh-CN", "PERMISSION_DENIED"));
		}
		return true;
	}

	private boolean hasPlayedBefore(OfflinePlayer player) {
		return ConfigManager.getDataFile(player).getName().contains(String.valueOf(player.getUniqueId()));
	}

}
