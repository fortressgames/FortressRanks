package net.fortressgames.fortressranksspigot.events;

import net.fortressgames.fortressranksspigot.ranks.Rank;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class RankAddEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	private final UUID uuid;
	private final Rank rank;

	public RankAddEvent(UUID uuid, Rank rank) {
		this.uuid = uuid;
		this.rank = rank;
	}

	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public UUID getPlayerUUID() {
		return this.uuid;
	}

	public Rank getRank() {
		return this.rank;
	}
}