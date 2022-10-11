package net.fortressgames.fortressranksbungee;

import lombok.Getter;
import lombok.SneakyThrows;
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
	@Getter private boolean mysql;

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
		 * MYSQL
		 */
		File sql = new File(getDataFolder() + "/MYSQL.yml");
		if(!sql.exists()) sql.createNewFile();

		Configuration sqlConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(sql);

		if(!sqlConfig.contains("host")) {
			sqlConfig.set("host", "localhost");
			sqlConfig.set("port", 3306);
			sqlConfig.set("database", "ranks");
			sqlConfig.set("user", "root");
			sqlConfig.set("password", "");

			ConfigurationProvider.getProvider(YamlConfiguration.class).save(sqlConfig, sql);
		}

		/*
		 * Settings
		 */
		File settings = new File(getDataFolder() + "/Settings.yml");
		if(!settings.exists()) settings.createNewFile();

		Configuration settingsConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(settings);

		if(!settingsConfig.contains("MYSQL")) {
			settingsConfig.set("MYSQL", false);
			settingsConfig.set("Default-Rank", "DEFAULT");

			ConfigurationProvider.getProvider(YamlConfiguration.class).save(settingsConfig, settings);
		}

		this.settings = settingsConfig;
		this.mysql = settingsConfig.getBoolean("MYSQL");

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
	}

	/**
	 * Called when the plugin is first loaded by Spigot
	 */
	@Override
	public void onEnable() {
		instance = this;

		if(mysql) {
			RankModule.getInstance().loadRanksFromSQL();
		} else {
			RankModule.getInstance().loadRanksFromConfig();
		}

		// Listeners
		getProxy().getPluginManager().registerListener(this, UserModule.getInstance());

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