package me.naptie.bukkit.rank.utils;

import me.naptie.bukkit.rank.Main;
import me.naptie.bukkit.rank.Messages;
import me.naptie.bukkit.rank.tools.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MySQLManager {

	private FileConfiguration config;

	private MySQL.Editor editor;

	private boolean debug;

	public MySQLManager(Main plugin) {
		this.config = plugin.getConfig();
		this.debug = config.getBoolean("mysql.debug");
		this.loginToMySQL();
		this.migrate();
		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!RankManager.checkNull()) {
				Main.logger.warning(Messages.getMessage("zh-CN", "RANK_NULL"));
			}
		});
	}

	private void loginToMySQL() {
		MySQL mySQL = new MySQL(this.config.getString("mysql.username"), this.config.getString("mysql.password"));
		mySQL.setAddress(this.config.getString("mysql.address"));
		mySQL.setDatabase(this.config.getString("mysql.database"));
		mySQL.setTable("rank");
		mySQL.setTimezone(this.config.getString("mysql.timezone"));
		mySQL.setUseSSL(this.config.getBoolean("mysql.useSSL"));

		try {
			this.editor = mySQL.connect();
			Bukkit.getConsoleSender().sendMessage(Messages.MYSQL_CONNECTED);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Executors.newSingleThreadExecutor().execute(MySQLManager.this::autoReconnect);
	}

	private void autoReconnect() {
		if (this.debug) {
			Main.logger.info("Reconnecting in 30 minutes...");
		}

		try {
			TimeUnit.MINUTES.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (this.debug) {
			Main.logger.info("Reconnecting...");
		}
		try {
			this.editor = this.editor.getMySQL().reconnect();
		} catch (SQLException e) {
			if (this.debug) {
				Main.logger.info("Reconnection failed!");
			}
			this.autoReconnect();
			return;
		}
		if (this.debug) {
			Main.logger.info("Reconnection succeeded!");
		}
		this.autoReconnect();
	}

	public void migrate(File file) {
		if (file.exists()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

			if (this.debug) {
				Main.logger.info("Migrating " + file.getName() + " to MySQL...");
			}

			this.editor.set(file.getName().replace(".yml", ""), config.getString("rank"));

			if (this.debug) {
				Main.logger.info("Migration from " + file.getName() + " succeeded!");
			}
		} else {
			Main.logger.severe("File '" + file.getName() + "' not found!");
		}
	}

	private void migrate() {
		File[] files = Main.playerDataFolder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getName().endsWith(".yml")) {
					YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

					if (this.debug) {
						Main.logger.info("Migrating " + file.getName() + " to MySQL...");
					}

					this.editor.set(file.getName().replace(".yml", ""), config.getString("rank"));

					if (this.debug) {
						Main.logger.info("Migration from " + file.getName() + " succeeded!");
					}
				}
			}
		} else {
			throw new NullPointerException("Listing files returned null");
		}
	}

	public void save(File file) {
		if (file.exists()) {
			if (file.getName().endsWith(".yml")) {
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

				if (this.debug) {
					Main.logger.info("Migrating to " + file.getName() + " from MySQL...");
				}

				config.set("rank", this.editor.get(file.getName().replace(".yml", "")));

				if (this.debug) {
					Main.logger.info("Migration to " + file.getName() + " succeeded!");
				}
			}
		} else {
			Main.logger.severe("File '" + file.getName() + "' not found!");
		}
	}

	public void save() {
		File[] files = Main.playerDataFolder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.exists()) {
					if (file.getName().endsWith(".yml")) {
						YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

						if (this.debug) {
							Main.logger.info("Migrating to " + file.getName() + " from MySQL...");
						}

						config.set("rank", this.editor.get(file.getName().replace(".yml", "")));
						try {
							config.save(file);
						} catch (IOException e) {
							Main.logger.severe("Error occurred while saving to " + file.getName() + ". Exception: " + e);
						}

						if (this.debug) {
							Main.logger.info("Migration to " + file.getName() + " succeeded!");
						}
					}
				} else {
					Main.logger.severe("File '" + file.getName() + "' not found!");
				}
			}
		} else {
			throw new NullPointerException("Listing files returned null");
		}
	}

	MySQL.Editor getEditor() {
		return editor;
	}
}
