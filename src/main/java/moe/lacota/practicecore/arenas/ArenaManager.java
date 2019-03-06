package moe.lacota.practicecore.arenas;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import moe.lacota.practicecore.utils.LocationUtil;
import org.bukkit.Location;

public class ArenaManager {
	private final File ARENA_DIR;

	private List<Arena> arenas;
	private Random random;

	public ArenaManager() {
		arenas = new ArrayList<>();
		random = new Random();

		ARENA_DIR = new File("arenas");
		if (!ARENA_DIR.exists()) {
			ARENA_DIR.mkdir();
		}

		loadArenas();
	}

	public List<Arena> getArenas() {
		return arenas;
	}

	public Arena getRandomArena() {
		return arenas.get(random.nextInt(arenas.size()));
	}

	public Arena getByName(String name) {
		return arenas.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public File getFile(Arena arena) {
		File file = new File(ARENA_DIR, arena.getName());
		return file;
	}

	public void loadArenas() {
		for (File file : Objects.requireNonNull(ARENA_DIR.listFiles())) {
			try {
				DataInputStream in = new DataInputStream(new FileInputStream(file));

				String name = in.readUTF();
				Location spawnPointA = LocationUtil.locationFromString(in.readUTF());
				Location spawnPointB = LocationUtil.locationFromString(in.readUTF());

				Arena arena = new Arena(name, spawnPointA, spawnPointB);
				arenas.add(arena);

				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addArena(Arena arena) {
		if (arenas.contains(arena)) {
			return;
		}

		addArena(arena.getName(), arena.getSpawnPointA(), arena.getSpawnPointB());
	}

	public void addArena(String name, Location spawnPointA, Location spawnPointB) {
		Arena arena = new Arena(name, spawnPointA, spawnPointB);

		System.out.println("Adding new arena " + arena.getName() + "...");

		File file;
		DataOutputStream out;

		try {
			file = new File(ARENA_DIR, arena.getName());
			out = new DataOutputStream(new FileOutputStream(file));

			out.writeUTF(name);
			out.writeUTF(LocationUtil.locationToString(spawnPointA));
			out.writeUTF(LocationUtil.locationToString(spawnPointB));

			out.flush();
			out.close();

			arenas.add(arena);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
