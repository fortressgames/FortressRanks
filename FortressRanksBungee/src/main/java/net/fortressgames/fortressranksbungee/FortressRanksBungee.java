package net.fortressgames.fortressranksbungee;

import lombok.Getter;
import lombok.SneakyThrows;
import net.fortressgames.database.Database;
import net.fortressgames.fortressbungeeessentials.utils.ConsoleMessage;
import net.fortressgames.fortressranksbungee.ranks.RankModule;
import net.fortressgames.fortressranksbungee.users.UserModule;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

public class FortressRanksBungee extends Plugin {

	@Getter	private static FortressRanksBungee instance;

	@Getter private boolean sql;

	@Getter private Configuration settings;

	/**
	 * Called when plugin first loads by spigot and is called before onEnable
	 */
	@Override
	@SneakyThrows
	public void onLoad() {
		// Create Default folder
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}

		File playerRanks = new File(getDataFolder() + "/PlayerRanks");
		if(!playerRanks.exists()) playerRanks.mkdir();

		/*
		 * SQL
		 */
		File sql = new File(getDataFolder() + "/sql.yml");
		if(!sql.exists()) sql.createNewFile();

		Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(sql);

		if(!config.contains("host")) {
			config.set("host", "localhost");
			config.set("port", 3306);
			config.set("database", "database");
			config.set("user", "root");
			config.set("password", "");

			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, sql);
		}

		/*
		 * Settings
		 */
		File settings = new File(getDataFolder() + "/Settings.yml");
		if(!settings.exists()) settings.createNewFile();

		Configuration settingsConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(settings);

		if(!settingsConfig.contains("Default-Rank")) {
			settingsConfig.set("Default-Rank", "DEFAULT");
		}

		if(!settingsConfig.contains("SQL")) {
			settingsConfig.set("SQL", false);
		} else {
			this.sql = settingsConfig.getBoolean("SQL");
		}

		ConfigurationProvider.getProvider(YamlConfiguration.class).save(settingsConfig, settings);
		this.settings = settingsConfig;

		/*
		 * Ranks
		 */
		File ranks = new File(getDataFolder() + "/Ranks.yml");
		if(!ranks.exists()) {
			ranks.createNewFile();

			Configuration ranksConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(ranks);

			ranksConfig.set("DEFAULT.RankID", "DEFAULT");
			ranksConfig.set("DEFAULT.Prefix", "&7[DEFAULT]");
			ranksConfig.set("DEFAULT.Power", 0);
			ranksConfig.set("DEFAULT.Permissions", new ArrayList<>());

			ranksConfig.set("ADMIN.RankID", "ADMIN");
			ranksConfig.set("ADMIN.Prefix", "&c[ADMIN]");
			ranksConfig.set("ADMIN.Power", 50);
			ranksConfig.set("ADMIN.Permissions", new ArrayList<>());

			ranksConfig.set("OWNER.RankID", "OWNER");
			ranksConfig.set("OWNER.Prefix", "&c[OWNER]");
			ranksConfig.set("OWNER.Power", 51);
			ranksConfig.set("OWNER.Permissions", new ArrayList<>());

			ConfigurationProvider.getProvider(YamlConfiguration.class).save(ranksConfig, ranks);
		}

		if(this.sql) {
			Database.setHost(config.getString("host"));
			Database.setPort(config.getInt("port"));
			Database.setDatabase(config.getString("database"));
			Database.setUser(config.getString("user"));
			Database.setPassword(config.getString("password"));
		}
	}

	/**
	 * Called when the plugin is first loaded by Spigot
	 */
	@Override
	public void onEnable() {
		instance = this;

		RankModule.getInstance().loadRanksFromConfig();

		// Listeners
		getProxy().getPluginManager().registerListener(this, UserModule.getInstance());

		// Commands
		getProxy().getPluginManager().registerCommand(this, new RankCommand());

		getLogger().info(ConsoleMessage.GREEN + "Version: " + getDescription().getVersion() + " Enabled!" + ConsoleMessage.RESET);
	}

	/**
	 * Called when the server is restarted or stopped
	 */
	@Override
	public void onDisable() {
		getLogger().info(ConsoleMessage.RED + "Version: " + getDescription().getVersion() + " Disabled!" + ConsoleMessage.RESET);
	}
}

//TODO
// MYSQL for user ranks