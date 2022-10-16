package net.fortressgames.fortressranksbungee;

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
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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

					try {
						File playerFile = new File(FortressRanksBungee.getInstance().getDataFolder() + "/PlayerRanks/" + uuid + ".yml");
						Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(playerFile);

						sender.sendMessage(Lang.LINE);
						sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Ranks:"));

						for(String rank : config.getStringList("Ranks")) {
							sender.sendMessage(TextComponent.fromLegacyText(ChatColor.WHITE + rank));
						}
						sender.sendMessage(Lang.LINE);

					} catch (IOException e) {
						sender.sendMessage(TextComponent.fromLegacyText(RankLang.UNKNOWN_USER));
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

							if(ProxyServer.getInstance().getPlayer(UUID.fromString(uuid)) != null &&
									ProxyServer.getInstance().getPlayer(UUID.fromString(uuid)).isConnected()) {
								UserModule.getInstance().getUser(ProxyServer.getInstance().getPlayer(UUID.fromString(uuid))).getRanks().add(rank);
								ProxyServer.getInstance().getPlayer(UUID.fromString(uuid)).sendMessage(TextComponent.fromLegacyText(RankLang.UPDATE_RANK));
							}

							List<String> list = config.getStringList("Ranks");
							list.add(rank.rankID());
							config.set("Ranks", list);
							ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, playerFile);

							ProxyServer.getInstance().getPluginManager().callEvent(new RankAddEvent(UUID.fromString(uuid), rank));

							sender.sendMessage(TextComponent.fromLegacyText(RankLang.ADD_RANK));
							return;
						}

						if(args[1].equalsIgnoreCase("remove")) {

							if(ProxyServer.getInstance().getPlayer(UUID.fromString(uuid)) != null &&
									ProxyServer.getInstance().getPlayer(UUID.fromString(uuid)).isConnected()) {
								UserModule.getInstance().getUser(ProxyServer.getInstance().getPlayer(UUID.fromString(uuid))).getRanks().remove(rank);
								ProxyServer.getInstance().getPlayer(UUID.fromString(uuid)).sendMessage(TextComponent.fromLegacyText(RankLang.UPDATE_RANK));

							}

							List<String> list = config.getStringList("Ranks");
							list.remove(rank.rankID());
							config.set("Ranks", list);
							ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, playerFile);

							ProxyServer.getInstance().getPluginManager().callEvent(new RankRemoveEvent(UUID.fromString(uuid), rank));

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
}