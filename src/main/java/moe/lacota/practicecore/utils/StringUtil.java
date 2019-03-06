package moe.lacota.practicecore.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;

public class StringUtil {
	public static String[] separateDescription(String descr) {
		StringBuilder builder = new StringBuilder(descr);

		int i = 0;
		while (i + 40 < builder.length() && (i = builder.lastIndexOf(" ", i + 40)) != -1) {
			builder.replace(i, i + 1, "\n");
		}

		return builder.toString().split("\n");
	}

	public static String formatPlayers(Collection<UUID> coll) {
		Set<String> names = coll.stream()
				.map(PlayerUtil::fetchName)
				.sorted(Comparator.naturalOrder())
				.collect(Collectors.toCollection(LinkedHashSet::new));

		StringBuilder formatted = new StringBuilder();
		for (String name : names) {
			formatted.append(name).append(", ");
		}

		return formatted.substring(0, formatted.length() - 2);
	}

	public static String formatPlayersByPlayerCollection(Collection<Player> coll) {
		Set<String> names = coll.stream()
				.map(Player::getName)
				.sorted(Comparator.naturalOrder())
				.collect(Collectors.toCollection(LinkedHashSet::new));

		StringBuilder formatted = new StringBuilder();
		for (String name : names) {
			formatted.append(name).append(", ");
		}

		return formatted.substring(0, formatted.length() - 2);
	}
}