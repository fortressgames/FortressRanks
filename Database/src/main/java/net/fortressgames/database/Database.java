package net.fortressgames.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.Setter;
import net.fortressgames.database.models.PlayerRanksDB;
import org.sql2o.Sql2o;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Database {

	private static Sql2o sql2o;
	private final static Map<String, String> colMaps = new HashMap<>();

	@Setter	private static String host;
	@Setter	private static int port;
	@Setter private static String database;
	@Setter private static String user;
	@Setter private static String password;

	private static void createConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setServerName(host);
		dataSource.setPort(port);
		dataSource.setDatabaseName(database);
		dataSource.setUser(user);
		dataSource.setPassword(password);

		try	{
			dataSource.setUseSSL(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sql2o = new Sql2o(dataSource);

		registerColMaps();

		sql2o.setDefaultColumnMappings(colMaps);
	}

	private static void registerColMaps() {
		colMaps.putAll(PlayerRanksDB.getColMaps());
	}

	public static Sql2o getSql2o() {
		if (sql2o == null) {
			createConnection();
		}
		return sql2o;
	}
}