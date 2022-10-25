package net.fortressgames.fortressranksbungee.users;

import lombok.SneakyThrows;
import net.fortressgames.database.manager.PlayerRanksManager;
import net.fortressgames.fortressranksbungee.FortressRanksBungee;
import net.fortressgames.fortressranksbungee.RankLang;
import net.fortressgames.fortressranksbungee.events.RankAddEvent;
import net.fortressgames.fortressranksbungee.events.RankRemoveEvent;
import net.fortressgames.fortressranksbungee.ranks.Rank;
import net.fortressgames.rankschannelmessage.RanksChannelMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.util.ArrayList;
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
		this.users.remove(e.getPlayer());
	}

	@EventHandler
	public void switchServers(ServerSwitchEvent e) {
		ProxiedPlayer player = e.getPlayer();

		List<String> rankList = new ArrayList<>();
		getUser(player).getRanks().forEach(rank -> rankList.add(rank.rankID()));

		sendPluginMessage(player, new RanksChannelMessage("LOAD_RANKS", false, player.getUniqueId().toString(), rankList));
	}

	@SneakyThrows
	public void addRank(UUID uuid, Rank rank) {
		ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);

		if(FortressRanksBungee.getInstance().isSql()) {
			PlayerRanksManager.insertPlayerRank(uuid.toString(), rank.rankID()).execute();

		} else {
			File playerFile = new File(FortressRanksBungee.getInstance().getDataFolder() + "/PlayerRanks/" + uuid.toString() + ".yml");
			Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(playerFile);

			List<String> list = config.getStringList("Ranks");
			list.add(rank.rankID());
			config.set("Ranks", list);
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, playerFile);
		}

		if(target != null && target.isConnected()) {
			UserModule.getInstance().getUser(target).getRanks().add(rank);
			target.sendMessage(TextComponent.fromLegacyText(RankLang.UPDATE_RANK));

			sendPluginMessage(target, new RanksChannelMessage("ADD_RANK", false, uuid.toString(), rank.rankID()));
		}

		ProxyServer.getInstance().getPluginManager().callEvent(new RankAddEvent(uuid, rank));
	}

	@SneakyThrows
	public void removeRank(UUID uuid, Rank rank) {
		ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);

		if(FortressRanksBungee.getInstance().isSql()) {
			PlayerRanksManager.removePlayerRank(uuid.toString(), rank.rankID()).execute();

		} else {
			File playerFile = new File(FortressRanksBungee.getInstance().getDataFolder() + "/PlayerRanks/" + uuid.toString() + ".yml");
			Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(playerFile);

			List<String> list = config.getStringList("Ranks");
			list.remove(rank.rankID());
			config.set("Ranks", list);
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, playerFile);
		}

		if(target != null && target.isConnected()) {
			UserModule.getInstance().getUser(target).getRanks().remove(rank);
			target.sendMessage(TextComponent.fromLegacyText(RankLang.UPDATE_RANK));

			sendPluginMessage(target, new RanksChannelMessage("REMOVE_RANK", false, uuid.toString(), rank.rankID()));
		}

		ProxyServer.getInstance().getPluginManager().callEvent(new RankRemoveEvent(uuid, rank));
	}

	public void sendPluginMessage(ProxiedPlayer player, RanksChannelMessage message) {
		player.getServer().getInfo().sendData("BungeeCord", message.toByteArray());
	}
}