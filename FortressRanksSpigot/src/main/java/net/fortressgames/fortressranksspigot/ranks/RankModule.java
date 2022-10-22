package net.fortressgames.fortressranksspigot.ranks;

import lombok.Getter;
import lombok.SneakyThrows;
import net.fortressgames.fortressranksspigot.FortressRanksSpigot;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class RankModule {

	@Getter private final HashMap<String, Rank> ranks = new HashMap<>();
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

	@SneakyThrows
	public void loadRanksFromConfig() {
		File rankFile = new File(FortressRanksSpigot.getInstance().getDataFolder() + "/Ranks.yml");
		YamlConfiguration ranksConfig = YamlConfiguration.loadConfiguration(rankFile);

		for(String rank : ranksConfig.getKeys(false)) {

			this.ranks.put(ranksConfig.getString(rank + ".RankID").toUpperCase(), new Rank(
					ranksConfig.getString(rank + ".RankID"), ranksConfig.getString(rank + ".Prefix"), ranksConfig.getInt(rank + ".Power"),
					ranksConfig.getStringList(rank + ".Permissions")
			));
		}
	}
}