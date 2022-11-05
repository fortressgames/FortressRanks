package net.fortressgames.fortressranksspigot.listener;

import net.fortressgames.fortressapi.utils.ConsoleMessage;
import net.fortressgames.fortressranksspigot.events.RankAddEvent;
import net.fortressgames.fortressranksspigot.events.RankRemoveEvent;
import net.fortressgames.fortressranksspigot.ranks.RankModule;
import net.fortressgames.fortressranksspigot.users.UserModule;
import net.fortressgames.rankschannelmessage.RanksChannelMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.UUID;

public class ReceiveListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if(!channel.equals("BungeeCord")) {
			return;
		}

		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(message);
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			RanksChannelMessage pluginMessage = (RanksChannelMessage) objectInputStream.readObject();

			if(!pluginMessage.isSilent()) {
				System.out.println(ConsoleMessage.YELLOW + "[Bungee Message Channel] " + pluginMessage.getAction() + ", " + pluginMessage.getArgs() + ConsoleMessage.RESET);
			}

			UUID uuid = UUID.fromString(pluginMessage.getArgs().get(0).toString());

			switch (pluginMessage.getAction()) {
				case "LOAD_RANKS" -> {
					for(String rank : pluginMessage.getArgs().get(1).toString().replace(" ", "").replace("[", "").replace("]", "").split(",")) {
						UserModule.getInstance().getUser(Bukkit.getPlayer(uuid)).getRanks().add(RankModule.getInstance().getRank(rank));
					}
				}

				case "ADD_RANK" -> {
					Bukkit.getPluginManager().callEvent(new RankAddEvent(uuid, RankModule.getInstance().getRank(pluginMessage.getArgs().get(1).toString())));

					UserModule.getInstance().getUser(Bukkit.getPlayer(uuid)).getRanks()
							.add(RankModule.getInstance().getRank(pluginMessage.getArgs().get(1).toString()));
				}

				case "REMOVE_RANK" -> {
					Bukkit.getPluginManager().callEvent(new RankRemoveEvent(uuid, RankModule.getInstance().getRank(pluginMessage.getArgs().get(1).toString())));

					UserModule.getInstance().getUser(Bukkit.getPlayer(uuid)).getRanks()
							.remove(RankModule.getInstance().getRank(pluginMessage.getArgs().get(1).toString()));
				}
			}

			UserModule.getInstance().getUser(Bukkit.getPlayer(uuid)).loadPermission();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}