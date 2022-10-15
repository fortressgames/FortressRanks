package net.fortressgames.fortressranksbungee.users;

import lombok.Getter;
import lombok.SneakyThrows;
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

	@Getter private final List<Rank> ranks = new ArrayList<>();

	@SneakyThrows
	public User(ProxiedPlayer player) {
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