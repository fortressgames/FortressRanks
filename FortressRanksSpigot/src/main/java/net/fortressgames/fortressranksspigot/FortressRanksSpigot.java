package net.fortressgames.fortressranksspigot;

import lombok.Getter;
import lombok.SneakyThrows;
import net.fortressgames.database.Database;
import net.fortressgames.fortressapi.commands.CommandModule;
import net.fortressgames.fortressapi.players.PlayerModule;
import net.fortressgames.fortressapi.utils.ConsoleMessage;
import net.fortressgames.fortressranksspigot.listener.ReceiveListener;
import net.fortressgames.fortressranksspigot.ranks.RankModule;
import net.fortressgames.fortressranksspigot.users.UserModule;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class FortressRanksSpigot extends JavaPlugin {

	@Getter	private static FortressRanksSpigot instance;
	@Getter private boolean bungee;
	@Getter private boolean sql;

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

		YamlConfiguration config = YamlConfiguration.loadConfiguration(sql);

		if(!config.contains("host")) {
			config.set("host", "localhost");
			config.set("port", 3306);
			config.set("database", "database");
			config.set("user", "root");
			config.set("password", "");

			config.save(sql);
		}

		/*
		 * Settings
		 */
		getConfig().options().copyDefaults(true);
		saveConfig();

		if(!getConfig().contains("Bungee")) {
			getConfig().set("Bungee", false);
			saveConfig();
		} else {
			bungee = getConfig().getBoolean("Bungee");
		}

		if(!getConfig().contains("Default-Rank")) {
			getConfig().set("Default-Rank", "DEFAULT");
			saveConfig();
		}

		if(!getConfig().contains("SQL")) {
			getConfig().set("SQL", false);
			saveConfig();
		} else {
			this.sql = getConfig().getBoolean("SQL");
		}

		/*
		 * Ranks
		 */
		File ranks = new File(getDataFolder() + "/Ranks.yml");
		if(!ranks.exists()) {
			ranks.createNewFile();

			YamlConfiguration ranksConfig = YamlConfiguration.loadConfiguration(ranks);

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

			ranksConfig.save(ranks);
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

		getServer().getPluginManager().registerEvents(UserModule.getInstance(), this);

		if(bungee) {
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new ReceiveListener());
		} else {
			CommandModule.registerCommand(new RankCommand());
		}

		// Adds players after reload
		for(Player pp : PlayerModule.getInstance().getOnlinePlayers()) {
			UserModule.getInstance().addUser(pp);
		}

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