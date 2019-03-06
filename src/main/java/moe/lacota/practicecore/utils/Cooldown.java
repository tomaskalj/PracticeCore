package moe.lacota.practicecore.utils;

import java.util.Map;
import java.util.WeakHashMap;
import lombok.Getter;

public class Cooldown {
	@Getter
	private static Map<Object, Map<Object, Long>> map = new WeakHashMap<>();

	public static void addCooldown(Object object, Object type, long millis) {
		Map<Object, Long> cooldown = getMap(object, type);

		if (hasCooldown(object, type)) {
			cooldown.put(type, cooldown.get(type) + millis);
		} else {
			cooldown.put(type, System.currentTimeMillis() + millis);
		}
	}

	public static boolean hasCooldown(Object object, Object type) {
		Map<Object, Long> cooldown = getMap(object, type);

		if (cooldown.get(type) > System.currentTimeMillis()) {
			return true;
		}

		return false;
	}

	public static long getCooldown(Object object, Object type) {
		Map<Object, Long> cooldown = getMap(object, type);

		if (hasCooldown(object, type)) {
			return (cooldown.get(type) - System.currentTimeMillis());
		} else {
			return 0;
		}
	}

	private static Map<Object, Long> getMap(Object object, Object type) {
		if (map == null) {
			map = new WeakHashMap<>();
		}

		if (!map.containsKey(object)) {
			map.put(object, new WeakHashMap<>());
		}

		if (!map.get(object).containsKey(type)) {
			map.get(object).put(type, (long) 0);
		}

		return map.get(object);
	}
}