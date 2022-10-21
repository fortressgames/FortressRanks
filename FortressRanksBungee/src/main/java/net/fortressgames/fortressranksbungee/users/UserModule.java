package net.fortressgames.fortressranksbungee.users;

import lombok.SneakyThrows;
import net.fortressgames.fortressranksbungee.FortressRanksBungee;
import net.fortressgames.fortressranksbungee.RankLang;
import net.fortressgames.fortressranksbungee.events.RankAddEvent;
import net.fortressgames.fortressranksbungee.events.RankRemoveEvent;
import net.fortressgames.fortressranksbungee.ranks.Rank;
import net.fortressgames.pluginmessage.PluginMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserModule implements Listener {

	private static UserModule instance;
	private final HashMap<ProxiedPlayer, User> users = new HashMap<>();

	public static UserModule getInstance() {
		if(instance == null) {
			instance = new UserModule();
		}

		return instance;
	}

	public User getUser(ProxiedPlayer player) {
		return this.users.get(player);
	}

	@EventHandler
	public void join(PostLoginEvent e) {
		ProxiedPlayer player = e.getPlayer();

		this.users.put(player, new User(player));
	}

	@EventHandler
	public void quit(PlayerDisconnectEvent e) {
		ProxiedPlayer player = e.getPlayer();

		this.users.remove(player);
	}

	@SneakyThrows
	public void addRank(UUID uuid, Rank rank) {
		File playerFile = new File(FortressRanksBungee.getInstance().getDataFolder() + "/PlayerRanks/" + uuid.toString() + ".yml");
		Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(playerFile);

		if(ProxyServer.getInstance().getPlayer(uuid) != null &&
				ProxyServer.getInstance().getPlayer(uuid).isConnected()) {
			UserModule.getInstance().getUser(ProxyServer.getInstance().getPlayer(uuid)).getRanks().add(rank);
			ProxyServer.getInstance().getPlayer(uuid).sendMessage(TextComponent.fromLegacyText(RankLang.UPDATE_RANK));
		}

		List<String> list = config.getStringList("Ranks");
		list.add(rank.rankID());
		config.set("Ranks", list);
		ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, playerFile);

		ProxyServer.getInstance().getPluginManager().callEvent(new RankAddEvent(uuid, rank));

		sendPluginMessage(new PluginMessage("ADD_RANK", true, uuid.toString(), rank.rankID()));
	}

	@SneakyThrows
	public void removeRank(UUID uuid, Rank rank) {
		File playerFile = new File(FortressRanksBungee.getInstance().getDataFolder() + "/PlayerRanks/" + uuid.toString() + ".yml");
		Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(playerFile);

		if(ProxyServer.getInstance().getPlayer(uuid) != null &&
				ProxyServer.getInstance().getPlayer(uuid).isConnected()) {
			UserModule.getInstance().getUser(ProxyServer.getInstance().getPlayer(uuid)).getRanks().remove(rank);
			ProxyServer.getInstance().getPlayer(uuid).sendMessage(TextComponent.fromLegacyText(RankLang.UPDATE_RANK));
		}

		List<String> list = config.getStringList("Ranks");
		list.remove(rank.rankID());
		config.set("Ranks", list);
		ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, playerFile);

		ProxyServer.getInstance().getPluginManager().callEvent(new RankRemoveEvent(uuid, rank));

		sendPluginMessage(new PluginMessage("REMOVE_RANK", true, uuid.toString(), rank.rankID()));
	}

	public void sendPluginMessage(PluginMessage message) {
		for(ServerInfo serverInfo : FortressRanksBungee.getInstance().getProxy().getServers().values()) {
			serverInfo.sendData("BungeeCord", message.toByteArray());
		}
	}
}