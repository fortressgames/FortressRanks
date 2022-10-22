package net.fortressgames.fortressranksspigot.ranks;

import lombok.Getter;

import java.util.List;

public record Rank(@Getter String rankID, @Getter String prefix, @Getter int power, @Getter List<String> permissions) {
}