package moe.lacota.practicecore.party;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import moe.lacota.practicecore.PracticePlugin;
import org.bukkit.Bukkit;

@Getter
public class Party {
	private UUID creator;
	private List<UUID> players;

	public Party(UUID creator) {
		this.creator = creator;
		this.players = new ArrayList<>();
		this.players.add(creator);
	}

	public boolean isCreator(UUID uuid) {
		return creator.compareTo(uuid) == 0;
	}

	public void addPlayer(UUID uuid) {
		players.add(uuid);
	}

	public void removePlayer(UUID uuid) {
		players.remove(uuid);
	}

	public int getSize() {
		return players.size();
	}

	public void message(String message) {
		players.stream().filter(uuid -> Bukkit.getPlayer(uuid) != null)
				.filter(uuid -> PracticePlugin.getInstance().getServer().getPlayer(uuid).isOnline())
				.map(PracticePlugin.getInstance().getServer()::getPlayer)
				.forEach(player -> player.sendMessage(message));
	}
}
