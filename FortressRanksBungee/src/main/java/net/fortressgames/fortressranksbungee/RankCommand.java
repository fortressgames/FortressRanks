package net.fortressgames.fortressranksbungee;

import net.fortressgames.database.QueryHandler;
import net.fortressgames.database.manager.PlayerRanksManager;
import net.fortressgames.database.models.PlayerRanksDB;
import net.fortressgames.fortressbungeeessentials.Lang;
import net.fortressgames.fortressbungeeessentials.commands.CommandBase;
import net.fortressgames.fortressbungeeessentials.utils.UUIDHandler;
import net.fortressgames.fortressranksbungee.events.RankAddEvent;
import net.fortressgames.fortressranksbungee.events.RankRemoveEvent;
import net.fortressgames.fortressranksbungee.ranks.Rank;
import net.fortressgames.fortressranksbungee.ranks.RankModule;
import net.fortressgames.fortressranksbungee.users.UserModule;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RankCommand extends CommandBase {

	public RankCommand() {
		super("rank", "bungee.command.rank");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if(args.length == 0) {
			sender.sendMessage(Lang.LINE);
			sender.sendMessage(TextComponent.fromLegacyText(ChatColor.YELLOW + "/rank <player> add <group> " + ChatColor.GRAY + "- Add a group"));
			sender.sendMessage(TextComponent.fromLegacyText(ChatColor.YELLOW + "/rank <player> remove <group> " + ChatColor.GRAY + "- Remove a group"));
			sender.sendMessage(TextComponent.fromLegacyText(ChatColor.YELLOW + "/rank <player> info " + ChatColor.GRAY + "- Look at a players groups"));
			sender.sendMessage(Lang.LINE);
			return;
		}

		if(args.length == 2 && args[1].equalsIgnoreCase("info")) {

			UUIDHandler.getUUIDFromName((successful, uuid) -> {
				if(successful) {

					// SQL
					if(FortressRanksBungee.getInstance().isSql()) {
						QueryHandler<List<PlayerRanksDB>> playerRanksQuery = PlayerRanksManager.getPlayerRanks(uuid);

						playerRanksQuery.onComplete(playerGroupsDB -> {

							sender.sendMessage(Lang.LINE);
							sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Ranks:"));

							for(PlayerRanksDB playerRankDB : playerGroupsDB) {
								Rank rank = RankModule.getInstance().getRank(playerRankDB.getRankID());

								sender.sendMessage(TextComponent.fromLegacyText(
										ChatColor.WHITE + rank.rankID() + " : "
										+ rank.prefix()
								));
							}

							sender.sendMessage(Lang.LINE);
						});
						playerRanksQuery.execute();

					// CONFIG
					} else {
						try {
							File playerFile = new File(FortressRanksBungee.getInstance().getDataFolder() + "/PlayerRanks/" + uuid + ".yml");
							Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(playerFile);

							sender.sendMessage(Lang.LINE);
							sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Ranks:"));

							for(String rankString : config.getStringList("Ranks")) {
								Rank rank = RankModule.getInstance().getRank(rankString);

								sender.sendMessage(TextComponent.fromLegacyText(
										ChatColor.WHITE + rank.rankID() + " : "
												+ rank.prefix()
								));
							}
							sender.sendMessage(Lang.LINE);

						} catch (IOException e) {
							sender.sendMessage(TextComponent.fromLegacyText(RankLang.UNKNOWN_USER));
						}
					}

				} else {
					sender.sendMessage(TextComponent.fromLegacyText(RankLang.UNKNOWN_USER));
				}
			}, args[0]);
			return;
		}

		if(args.length == 3) {
			Rank rank;
			if(RankModule.getInstance().getRanks().containsKey(args[2].toUpperCase())) {
				rank = RankModule.getInstance().getRank(args[2].toUpperCase());
			} else {
				sender.sendMessage(TextComponent.fromLegacyText(RankLang.CANNOT_FIND_RANK));
				return;
			}

			UUIDHandler.getUUIDFromName((successful, uuid) -> {

				if(successful) {

					try {
						File playerFile = new File(FortressRanksBungee.getInstance().getDataFolder() + "/PlayerRanks/" + uuid + ".yml");
						Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(playerFile);

						if(args[1].equalsIgnoreCase("add")) {
							if(config.getStringList("Ranks").contains(rank.rankID())) {
								sender.sendMessage(TextComponent.fromLegacyText(RankLang.PLAYER_ALREADY_RANK));
								return;
							}

							UserModule.getInstance().addRank(UUID.fromString(uuid), rank);
							sender.sendMessage(TextComponent.fromLegacyText(RankLang.ADD_RANK));
							return;
						}

						if(args[1].equalsIgnoreCase("remove")) {
							UserModule.getInstance().removeRank(UUID.fromString(uuid), rank);
							sender.sendMessage(TextComponent.fromLegacyText(RankLang.REMOVE_RANK));
							return;
						}

					} catch (IOException e) {
						sender.sendMessage(TextComponent.fromLegacyText(RankLang.UNKNOWN_USER));
					}

				} else {
					sender.sendMessage(TextComponent.fromLegacyText(RankLang.UNKNOWN_USER));
				}
			}, args[0]);
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if(args.length == 1) {
			List<String> players = new ArrayList<>();
			for(ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
				players.add(pp.getName());
			}

			return players;
		}

		if(args.length == 2) {
			return Arrays.asList("info", "add", "remove");
		}

		if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")) {
			List<String> rankID = new ArrayList<>();
			for(Rank rank : RankModule.getInstance().getRanks().values()) {
				rankID.add(rank.rankID());
			}

			return rankID;
		}

		return super.onTabComplete(sender, args);
	}
}