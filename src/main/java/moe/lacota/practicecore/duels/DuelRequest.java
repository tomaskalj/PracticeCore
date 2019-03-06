package moe.lacota.practicecore.duels;

import moe.lacota.practicecore.arenas.Arena;
import org.bukkit.entity.Player;

public class DuelRequest {
	public Player requester;
	public Player requested;
	public Arena arena;
	public DuelType type;
	public long expires;

	public boolean hasExpired() {
		return System.currentTimeMillis() >= expires;
	}
}
