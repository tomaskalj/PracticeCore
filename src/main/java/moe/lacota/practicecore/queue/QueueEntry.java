package moe.lacota.practicecore.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import moe.lacota.practicecore.duels.DuelType;

@AllArgsConstructor
@Getter
public class QueueEntry {
	private DuelType type;
	private int elo;
	private boolean ranked;

	public QueueEntry(int elo, DuelType type, boolean ranked) {
		this.elo = elo;
		this.type = type;
		this.ranked = ranked;
	}
}
