package net.fortressgames.fortressranksspigot;

import lombok.Getter;
import net.fortressgames.fortressapi.utils.ConsoleMessage;
import net.fortressgames.fortressranksspigot.listener.ReceiveListener;
import org.bukkit.plugin.java.JavaPlugin;

public class FortressRanksSpigot extends JavaPlugin {

	@Getter	private static FortressRanksSpigot instance;
	private boolean bungee;

	/**
	 * Called when plugin first loads by spigot and is called before onEnable
	 */
	@Override
	public void onLoad() {
		// Create Default folder
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}

		getConfig().options().copyDefaults(true);
		saveConfig();

		if(!getConfig().contains("Bungee")) {
			getConfig().set("Bungee", false);
			saveConfig();
		} else {
			bungee = getConfig().getBoolean("Bungee");
		}
	}

	/**
	 * Called when the plugin is first loaded by Spigot
	 */
	@Override
	public void onEnable() {
		instance = this;

		if(bungee) {
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new ReceiveListener());
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