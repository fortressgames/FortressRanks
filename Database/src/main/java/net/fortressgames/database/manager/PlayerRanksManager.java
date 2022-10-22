package net.fortressgames.database.manager;

import net.fortressgames.database.QueryHandler;
import net.fortressgames.database.context.PlayerRanksContext;
import net.fortressgames.database.models.PlayerRanksDB;

import java.util.List;

public class PlayerRanksManager {

	public static QueryHandler<List<PlayerRanksDB>> getPlayerRanks(String uuid) {

		return new QueryHandler<>(() -> PlayerRanksContext.getPlayerRanks(uuid));
	}

	public static QueryHandler<Boolean> insertPlayerRank(String uuid, String rank_id) {

		return new QueryHandler<>(() -> PlayerRanksContext.insertPlayerRank(uuid, rank_id));
	}

	public static QueryHandler<Boolean> removePlayerRank(String uuid, String rank_id) {

		return new QueryHandler<>(() -> PlayerRanksContext.removePlayerRank(uuid, rank_id));
	}
}