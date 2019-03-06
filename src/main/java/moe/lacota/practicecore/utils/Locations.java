package moe.lacota.practicecore.utils;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Locations {
	public static final World WORLD = Bukkit.getWorld("world");

	public static final Location SPAWN = new Location(WORLD, -0.5, 76.5, -2.5);

	public static final Location SPAWN_POINT_A = new Location(WORLD, 30.5, 64.5, 30.5, 135, 0);

	public static final Location SPAWN_POINT_B = new Location(WORLD, -29.5, 64.5, -29.5, -45, 0);

	public static final List<Location> POSSIBLE_SPAWNS = Arrays.asList(
			SPAWN_POINT_A, // 
			SPAWN_POINT_B, //
			new Location(WORLD, 0.5, 64.5, -29.5, 0, 0), // 
			new Location(WORLD, 30.5, 64.5, -29.5, 45, 0), //
			new Location(WORLD, 30.5, 64.5, 0.5, 90, 0), //
			new Location(WORLD, 0.5, 64.5, 30.5, -180, 0), //
			new Location(WORLD, -29.5, 64.5, 30.5, -135, 0), //
			new Location(WORLD, -29.5, 64.5, 0, -90, 0));

	public static final Location KIT_EDITOR = new Location(WORLD, -77.5, 123.5, -73.5);
}
