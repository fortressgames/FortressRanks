package net.fortressgames.fortressranksspigot.listener;

import net.fortressgames.pluginmessage.PluginMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ReceiveListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if(!channel.equals("BungeeCord")) {
			return;
		}

		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(message);
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			PluginMessage pluginMessage = (PluginMessage) objectInputStream.readObject();

			System.out.println(pluginMessage.getAction());

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}