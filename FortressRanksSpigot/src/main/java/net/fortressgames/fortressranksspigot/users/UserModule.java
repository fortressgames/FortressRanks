package net.fortressgames.fortressranksspigot.users;

import lombok.SneakyThrows;
import net.fortressgames.fortressranksspigot.FortressRanksSpigot;
import net.fortressgames.fortressranksspigot.RankLang;
import net.fortressgames.fortressranksspigot.events.RankAddEvent;
import net.fortressgames.fortressranksspigot.events.RankRemoveEvent;
import net.fortressgames.fortressranksspigot.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserModule implements Listener {

	private static UserModule instance;
	private final HashMap<Player, User> users = new HashMap<>();

	public static UserModule getInstance() {
		if(instance == null) {
			instance = new UserModule();
		}

		return instance;
	}

	public User getUser(Player player) {
		return this.users.get(player);
	}

	public void addUser(Player player) {
		this.users.put(player, new User(player));
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		addUser(player);
	}

	@EventHandler
	public void quit(PlayerQuitEvent e) {
		this.users.remove(e.getPlayer());
	}

	@SneakyThrows
	public void addRank(UUID uuid, Rank rank) {
		File playerFile = new File(FortressRanksSpigot.getInstance().getDataFolder() + "/PlayerRanks/" + uuid.toString() + ".yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

		Player target = Bukkit.getPlayer(uuid);

		if(target != null) {
			UserModule.getInstance().getUser(target).getRanks().add(rank);
			target.sendMessage(RankLang.UPDATE_RANK);
		}

		List<String> list = config.getStringList("Ranks");
		list.add(rank.rankID());
		config.set("Ranks", list);

		config.save(playerFile);

		Bukkit.getPluginManager().callEvent(new RankAddEvent(uuid, rank));
	}

	@SneakyThrows
	public void removeRank(UUID uuid, Rank rank) {
		File playerFile = new File(FortressRanksSpigot.getInstance().getDataFolder() + "/PlayerRanks/" + uuid.toString() + ".yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

		Player target = Bukkit.getPlayer(uuid);

		if(target != null) {
			UserModule.getInstance().getUser(target).getRanks().remove(rank);
			target.sendMessage(RankLang.UPDATE_RANK);
		}

		List<String> list = config.getStringList("Ranks");
		list.remove(rank.rankID());
		config.set("Ranks", list);

		config.save(playerFile);

		Bukkit.getPluginManager().callEvent(new RankRemoveEvent(uuid, rank));
	}
}