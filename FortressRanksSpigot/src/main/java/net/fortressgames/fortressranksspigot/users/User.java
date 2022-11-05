package net.fortressgames.fortressranksspigot.users;

import lombok.Getter;
import lombok.SneakyThrows;
import net.fortressgames.database.QueryHandler;
import net.fortressgames.database.manager.PlayerRanksManager;
import net.fortressgames.database.models.PlayerRanksDB;
import net.fortressgames.fortressranksspigot.FortressRanksSpigot;
import net.fortressgames.fortressranksspigot.ranks.Rank;
import net.fortressgames.fortressranksspigot.ranks.RankModule;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {

	private final Player player;
	@Getter private final List<Rank> ranks = new ArrayList<>();

	private final List<PermissionAttachment> playerPerms = new ArrayList<>();

	@SneakyThrows
	public User(Player player) {
		this.player = player;

		if(!FortressRanksSpigot.getInstance().isBungee()) {

			if(FortressRanksSpigot.getInstance().isSql()) {
				// Load ranks
				QueryHandler<List<PlayerRanksDB>> playerRanks = PlayerRanksManager.getPlayerRanks(player.getUniqueId().toString());
				playerRanks.onComplete(rankDB -> {

					if(rankDB.isEmpty()) {
						ranks.add(RankModule.getInstance().getRank(FortressRanksSpigot.getInstance().getConfig().getString("Default-Rank")));
						PlayerRanksManager.insertPlayerRank(player.getUniqueId().toString(),
								RankModule.getInstance().getRank(FortressRanksSpigot.getInstance().getConfig().getString("Default-Rank")).rankID()).execute();
					} else {
						rankDB.forEach(rank -> ranks.add(RankModule.getInstance().getRank(rank.getRankID())));
					}

					loadPermission();
				});
				playerRanks.execute();

			} else {
				File playerFile = new File(FortressRanksSpigot.getInstance().getDataFolder() + "/PlayerRanks/" + player.getUniqueId() + ".yml");

				if(!playerFile.exists()) {
					playerFile.createNewFile();

					YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

					config.set("Ranks", new ArrayList<>(Collections.singleton(
							FortressRanksSpigot.getInstance().getConfig().getString("Default-Rank")
					)));

					config.save(playerFile);
				}

				YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
				config.getStringList("Ranks").forEach(rankID -> this.ranks.add(
						RankModule.getInstance().getRank(rankID)
				));

				loadPermission();
			}
		}
	}

	public void loadPermission() {
		if(ranks.isEmpty()) {
			// no ranks error
			// Adds default rank
			UserModule.getInstance().addRank(player.getUniqueId(),
					RankModule.getInstance().getRank(FortressRanksSpigot.getInstance().getConfig().getString("Default-Rank")));
			return;
		}

		for (PermissionAttachment permissionAttachment : this.playerPerms) {
			player.removeAttachment(permissionAttachment);
		}
		this.playerPerms.clear();

		// Load permissions
		this.ranks.forEach(rank -> rank.permissions().forEach(permission -> {

			if(!permission.contains("bungee")) {
				PermissionAttachment permissionAttachment = player.addAttachment(FortressRanksSpigot.getInstance());
				permissionAttachment.setPermission(permission, true);
				this.playerPerms.add(permissionAttachment);
			}
		}));
	}

	/**
	 * Return highest rank of the player
	 */
	public Rank getHighestRank() {
		Rank returnRank = null;
		for(Rank rank : ranks) {

			if(returnRank == null) {
				returnRank = rank;
				continue;
			}

			if(rank.power() >= returnRank.power()) {
				returnRank = rank;
			}
		}

		return returnRank;
	}
}