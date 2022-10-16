package net.fortressgames.fortressranksbungee.events;

import lombok.Getter;
import net.fortressgames.fortressranksbungee.ranks.Rank;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class RankAddEvent extends Event {

	@Getter private final UUID uuid;
	@Getter private final Rank rank;

	public RankAddEvent(UUID uuid, Rank rank) {
		this.uuid = uuid;
		this.rank = rank;
	}
}