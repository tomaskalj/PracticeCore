package moe.lacota.practicecore.players;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PracticePlayerManager {
	private final Map<UUID, PracticePlayer> players = new HashMap<>();

	public PracticePlayer addPlayer(UUID uuid) {
		PracticePlayer player = new PracticePlayer(uuid);
		players.put(uuid, player);

		return player;
	}

	public PracticePlayer removePlayer(UUID uuid) {
		return players.remove(uuid);
	}

	public PracticePlayer getPlayer(UUID uuid) {
		return players.get(uuid);
	}

	public PracticePlayer getPlayer(Player player) {
		return getPlayer(player.getUniqueId());
	}
}
