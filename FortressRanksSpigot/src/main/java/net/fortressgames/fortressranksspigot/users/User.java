package net.fortressgames.fortressranksspigot.users;

import lombok.Getter;
import lombok.SneakyThrows;
import net.fortressgames.fortressranksspigot.FortressRanksSpigot;
import net.fortressgames.fortressranksspigot.ranks.Rank;
import net.fortressgames.fortressranksspigot.ranks.RankModule;
import org.bukkit.ChatColor;
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

	public void loadPermission() {
		for (PermissionAttachment permissionAttachment : this.playerPerms) {
			player.removeAttachment(permissionAttachment);
		}
		this.playerPerms.clear();

		// Load permissions
		for(Rank rank : this.ranks) {
			if(rank.permissions() == null) {
				player.kickPlayer(ChatColor.RED + "Looks like something when wrong! please try again");
				return;
			}

			for(String permission : rank.permissions()) {

				PermissionAttachment permissionAttachment = player.addAttachment(FortressRanksSpigot.getInstance());
				permissionAttachment.setPermission(permission, true);
				this.playerPerms.add(permissionAttachment);
			}
		}
	}
}