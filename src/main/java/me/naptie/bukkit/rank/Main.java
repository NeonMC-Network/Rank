package me.naptie.bukkit.rank;

import me.naptie.bukkit.rank.commands.RankCommand;
import me.naptie.bukkit.rank.listeners.EventListener;
import me.naptie.bukkit.rank.objects.Rank;
import me.naptie.bukkit.rank.utils.CU;
import me.naptie.bukkit.rank.utils.MySQLManager;
import me.naptie.bukkit.rank.utils.RankManager;
import org.apache.commons.lang.SystemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

	public static Logger logger;
	public static File playerDataFolder;
	public static MySQLManager mySQLManager;
	static Main instance;

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Override
	public void onEnable() {
		instance = this;
		logger = this.getLogger();
		getCommand("rank").setExecutor(new RankCommand());
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		getConfig().options().copyDefaults(true);
		getConfig().options().copyHeader(true);
		saveDefaultConfig();
		for (String language : getConfig().getStringList("languages")) {
			File localeFile = new File(getDataFolder(), language + ".yml");
			if (localeFile.exists()) {
				if (getConfig().getBoolean("update-language-files")) {
					saveResource(language + ".yml", true);
				}
			} else {
				saveResource(language + ".yml", false);
			}
		}
		playerDataFolder = new File(getDataFolder().getAbsolutePath().split("NeonMC" + (SystemUtils.IS_OS_WINDOWS ? "\\\\" : File.separator))[0] + "NeonMC" + File.separator + "PlayerData" + File.separator);
		if (!playerDataFolder.exists()) {
			playerDataFolder.mkdir();
		}
		if (getConfig().getBoolean("mysql.enable")) {
			mySQLManager = new MySQLManager(this);
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			String prefix = RankManager.getPrefix(player);
			String suffix = CU.t(RankManager.getRank(player).equals(Rank.MEMBER) ? "&7" : "&r");
			String displayName = prefix + player.getName() + suffix;
			player.setPlayerListName(displayName);
			player.setDisplayName(displayName);
		}
		/*new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {
					RankManager.updateTablist(online);
				}
			}
		}.runTaskTimer(this, 20L, 20L);*/
		RankManager.synchronize();
		logger.info("Enabled " + getDescription().getName() + " v" + getDescription().getVersion());
	}

	@Override
	public void onDisable() {
		instance = null;
		logger.info("Disabled " + getDescription().getName() + " v" + getDescription().getVersion());
		logger = null;
	}
}
