package net.fortressgames.database.context;

import net.fortressgames.database.Database;
import net.fortressgames.database.models.PlayerRanksDB;
import org.sql2o.Connection;

import java.util.List;

public class PlayerRanksContext {

	public static List<PlayerRanksDB> getPlayerRanks(String uuid) {
		String query = "SELECT * "
				+ "FROM player_ranks "
				+ "WHERE uuid = :uuid";

		try (Connection con = Database.getSql2o().open()) {
			return con.createQuery(query)
					.addParameter("uuid", uuid)
					.executeAndFetch(PlayerRanksDB.class);
		}
	}

	public static boolean insertPlayerRank(String uuid, String rank_id) {
		String query = "INSERT INTO player_ranks(uuid, rank_id) "
				+ "VALUES(:uuid, :rank_id) ";

		try(Connection con = Database.getSql2o().open()) {
			con.createQuery(query)
					.addParameter("uuid", uuid)
					.addParameter("rank_id", rank_id)
					.executeUpdate();
			return con.getResult() > 0;
		}
	}

	public static boolean removePlayerRank(String uuid, String rank_id) {
		String query = "DELETE FROM player_ranks "
				+ "WHERE uuid = :uuid "
				+ "AND rank_id = :rank_id";

		try (Connection con = Database.getSql2o().open()) {
			con.createQuery(query)
					.addParameter("uuid", uuid)
					.addParameter("rank_id", rank_id)
					.executeUpdate();
			return con.getResult() > 0;
		}
	}
}