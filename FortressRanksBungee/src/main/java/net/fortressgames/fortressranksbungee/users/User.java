package net.fortressgames.fortressranksbungee.users;

import lombok.Getter;
import lombok.SneakyThrows;
import net.fortressgames.database.QueryHandler;
import net.fortressgames.database.manager.PlayerRanksManager;
import net.fortressgames.database.models.PlayerRanksDB;
import net.fortressgames.fortressranksbungee.FortressRanksBungee;
import net.fortressgames.fortressranksbungee.ranks.Rank;
import net.fortressgames.fortressranksbungee.ranks.RankModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {

	private final ProxiedPlayer player;
	@Getter private final List<Rank> ranks = new ArrayList<>();

	@SneakyThrows
	public User(ProxiedPlayer player) {
		this.player = player;

		if(FortressRanksBungee.getInstance().isSql()) {
			// Load ranks
			QueryHandler<List<PlayerRanksDB>> playerRanks = PlayerRanksManager.getPlayerRanks(player.getUniqueId().toString());
			playerRanks.onComplete(rankDB -> {

				if(rankDB.isEmpty()) {
					ranks.add(RankModule.getInstance().getRank(FortressRanksBungee.getInstance().getSettings().getString("Default-Rank")));
					PlayerRanksManager.insertPlayerRank(player.getUniqueId().toString(),
							RankModule.getInstance().getRank(FortressRanksBungee.getInstance().getSettings().getString("Default-Rank")).rankID()).execute();
				} else {
					rankDB.forEach(rank -> ranks.add(RankModule.getInstance().getRank(rank.getRankID())));
				}

				loadPermission();
			});
			playerRanks.execute();

		} else {
			File playerFile = new File(FortressRanksBungee.getInstance().getDataFolder() + "/PlayerRanks/" + player.getUniqueId().toString() + ".yml");
			if(!playerFile.exists()) {
				playerFile.createNewFile();

				Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(playerFile);
				config.set("Ranks", new ArrayList<>(Collections.singleton(
						FortressRanksBungee.getInstance().getSettings().getString("Default-Rank")
				)));

				ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, playerFile);
			}

			Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(playerFile);
			config.getStringList("Ranks").forEach(rankID -> this.ranks.add(
					RankModule.getInstance().getRank(rankID)
			));

			loadPermission();
		}
	}

	private void loadPermission() {
		this.ranks.forEach(rank -> rank.permissions().forEach(permission -> {

			if(permission.contains("bungee")) {
				player.setPermission(permission, true);
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

			if(returnRank.power() >= rank.power()) {
				returnRank = rank;
			}
		}

		return returnRank;
	}
}