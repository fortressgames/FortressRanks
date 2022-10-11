package net.fortressgames.fortressranksbungee.ranks;

import lombok.SneakyThrows;
import net.fortressgames.fortressranksbungee.FortressRanksBungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class RankModule {

	private final HashMap<String, Rank> ranks = new HashMap<>();
	private static RankModule instance;

	public static RankModule getInstance() {
		if(instance == null) {
			instance = new RankModule();
		}

		return instance;
	}

	public Rank getRank(String rankID) {
		return this.ranks.get(rankID);
	}

	public void loadRanksFromSQL() {
		//TODO
	}

	@SneakyThrows
	public void loadRanksFromConfig() {
		File rankFile = new File(FortressRanksBungee.getInstance().getDataFolder() + "/Ranks.yml");
		Configuration ranksConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(rankFile);

		for(String rank : ranksConfig.getKeys()) {

			this.ranks.put(ranksConfig.getString(rank + ".RankID"), new Rank(
					ranksConfig.getString(rank + ".RankID"), ranksConfig.getString(rank + ".Prefix"), ranksConfig.getInt(rank + ".Power"),
					ranksConfig.getStringList(rank + ".Permissions")
			));
		}
	}
}