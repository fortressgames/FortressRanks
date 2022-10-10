package net.fortressgames.fortressranksbungee;

import lombok.Getter;
import lombok.SneakyThrows;
import net.fortressgames.fortressbungeeessentials.utils.Config;
import net.fortressgames.fortressbungeeessentials.utils.ConsoleMessage;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;

public class FortressRanksBungee extends Plugin {

	@Getter	private static FortressRanksBungee instance;

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
	}

	/**
	 * Called when the plugin is first loaded by Spigot
	 */
	@Override
	public void onEnable() {
		instance = this;

		File settings = new File(getDataFolder() + "/Ranks.yml");
		if(!settings.exists()) {
			Config config = new Config(settings);

			config.getConfig().set("DEFAULT.RankID", "DEFAULT");
			config.getConfig().set("DEFAULT.Prefix", "&7[DEFAULT]");
			config.getConfig().set("DEFAULT.Power", 0);

			config.getConfig().set("ADMIN.RankID", "ADMIN");
			config.getConfig().set("ADMIN.Prefix", "&c[ADMIN]");
			config.getConfig().set("ADMIN.Power", 50);

			config.getConfig().set("OWNER.RankID", "OWNER");
			config.getConfig().set("OWNER.Prefix", "&c[OWNER]");
			config.getConfig().set("OWNER.Power", 51);

			config.saveConfig();
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