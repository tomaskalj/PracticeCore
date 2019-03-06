package moe.lacota.practicecore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {
	public static String locationToString(Location loc) {
		String str = loc.getWorld().getName() + ", ";
		str += loc.getX() + ", ";
		str += loc.getY() + ", ";
		str += loc.getZ() + ", ";
		str += loc.getYaw() + ", ";
		str += loc.getPitch();
		return str;
	}

	public static String simpleLocationToString(Location loc) {
		String str = "";
		str += loc.getBlockX() + ", ";
		str += loc.getBlockY() + ", ";
		str += loc.getBlockZ();
		return str;
	}

	public static Location locationFromString(String str) {
		String[] split = str.split(", ");
		if (split.length == 4) {
			String world = split[0];
			double x = Double.parseDouble(split[1]);
			double y = Double.parseDouble(split[2]);
			double z = Double.parseDouble(split[3]);
			return new Location(Bukkit.getWorld(world), x, y, z);
		} else if (split.length == 6) {
			String world = split[0];
			double x = Double.parseDouble(split[1]);
			double y = Double.parseDouble(split[2]);
			double z = Double.parseDouble(split[3]);
			float yaw = Float.parseFloat(split[4]);
			float pitch = Float.parseFloat(split[5]);
			return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
		}
		return null;
	}
}
