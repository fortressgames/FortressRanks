package net.fortressgames.database.models;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PlayerRanksDB {

	private String uuid;
	private String rankID;

	private static Map<String, String> colMaps = new HashMap<>();

	public static Map<String, String> getColMaps() {
		if(colMaps.size() <= 0) {
			colMaps.put("uuid", "uuid");
			colMaps.put("rank_id", "rankID");
		}
		return colMaps;
	}
}
