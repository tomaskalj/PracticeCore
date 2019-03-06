package moe.lacota.practicecore.arenas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@AllArgsConstructor
public class Arena {
	private String name;

	@Setter
	private Location spawnPointA;
	@Setter
	private Location spawnPointB;
}