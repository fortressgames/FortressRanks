package net.fortressgames.fortressranksspigot;

import net.fortressgames.database.QueryHandler;
import net.fortressgames.database.manager.PlayerRanksManager;
import net.fortressgames.database.models.PlayerRanksDB;
import net.fortressgames.fortressapi.Lang;
import net.fortressgames.fortressapi.commands.CommandBase;
import net.fortressgames.fortressapi.utils.MojangAPIUtils;
import net.fortressgames.fortressranksspigot.ranks.Rank;
import net.fortressgames.fortressranksspigot.ranks.RankModule;
import net.fortressgames.fortressranksspigot.users.UserModule;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RankCommand extends CommandBase {

	public RankCommand() {
		super("rank", "bungee.command.rank");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if(args.length == 0) {
			sender.sendMessage(Lang.LINE);
			sender.sendMessage(ChatColor.YELLOW + "/rank <player> add <group> " + ChatColor.GRAY + "- Add a group");
			sender.sendMessage(ChatColor.YELLOW + "/rank <player> remove <group> " + ChatColor.GRAY + "- Remove a group");
			sender.sendMessage(ChatColor.YELLOW + "/rank <player> info " + ChatColor.GRAY + "- Look at a players groups");
			sender.sendMessage(Lang.LINE);
			return;
		}

		if(args.length == 2 && args[1].equalsIgnoreCase("info")) {

			MojangAPIUtils.getUUID((successful, result, exception) -> {

				if(successful) {
					MojangAPIUtils.Profile profile = new ArrayList<>(result.values()).get(0);

					// SQL
					if(FortressRanksSpigot.getInstance().isSql()) {
						QueryHandler<List<PlayerRanksDB>> playerRanksQuery = PlayerRanksManager.getPlayerRanks(profile.uuid().toString());

						playerRanksQuery.onComplete(playerGroupsDB -> {

							sender.sendMessage(Lang.LINE);
							sender.sendMessage(ChatColor.GOLD + "Ranks:");

							for(PlayerRanksDB playerRankDB : playerGroupsDB) {
								sender.sendMessage(ChatColor.WHITE + playerRankDB.getRankID());
							}

							sender.sendMessage(Lang.LINE);
						});
						playerRanksQuery.execute();

					// CONFIG
					} else {
						File playerFile = new File(FortressRanksSpigot.getInstance().getDataFolder() + "/PlayerRanks/" + profile.uuid().toString() + ".yml");
						YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

						sender.sendMessage(Lang.LINE);
						sender.sendMessage(ChatColor.GOLD + "Ranks:");

						for(String rank : config.getStringList("Ranks")) {
							sender.sendMessage(ChatColor.WHITE + rank);
						}

						sender.sendMessage(Lang.LINE);
					}

				} else {
					sender.sendMessage(RankLang.UNKNOWN_USER);
				}
			}, args[0]);
			return;
		}

		if(args.length == 3) {
			Rank rank;
			if(RankModule.getInstance().getRanks().containsKey(args[2].toUpperCase())) {
				rank = RankModule.getInstance().getRank(args[2].toUpperCase());
			} else {
				sender.sendMessage(RankLang.CANNOT_FIND_RANK);
				return;
			}

			MojangAPIUtils.getUUID((successful, result, exception) -> {

				if(successful) {
					MojangAPIUtils.Profile profile = new ArrayList<>(result.values()).get(0);

					File playerFile = new File(FortressRanksSpigot.getInstance().getDataFolder() + "/PlayerRanks/" + profile.uuid().toString() + ".yml");
					YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

					if(args[1].equalsIgnoreCase("add")) {
						if(config.getStringList("Ranks").contains(rank.rankID())) {
							sender.sendMessage(RankLang.PLAYER_ALREADY_RANK);
							return;
						}

						UserModule.getInstance().addRank(profile.uuid(), rank);
						sender.sendMessage(RankLang.ADD_RANK);
						return;
					}

					if(args[1].equalsIgnoreCase("remove")) {
						UserModule.getInstance().removeRank(profile.uuid(), rank);
						sender.sendMessage(RankLang.REMOVE_RANK);
						return;
					}

				} else {
					sender.sendMessage(RankLang.UNKNOWN_USER);
				}
			}, args[0]);
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		if(args.length == 2) {
			return Arrays.asList("info", "add", "remove");
		}

		if(args.length >= 2) {
			if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")) {
				List<String> rankID = new ArrayList<>();
				for(Rank rank : RankModule.getInstance().getRanks().values()) {
					rankID.add(rank.rankID());
				}

				return rankID;
			}
		}

		return super.tabComplete(sender, alias, args);
	}
}