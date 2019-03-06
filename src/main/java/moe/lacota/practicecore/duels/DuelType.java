package moe.lacota.practicecore.duels;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DuelType {
	NO_DEBUFF("NoDebuff"),
	DEBUFF("Debuff");

	private final String name;

	public static DuelType getByName(String name) {
		for (DuelType type : values()) {
			if (type.getName().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}
