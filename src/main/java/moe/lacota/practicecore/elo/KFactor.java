package moe.lacota.practicecore.elo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KFactor {
	private int startIndex;
	private int endIndex;
	private double value;
}
