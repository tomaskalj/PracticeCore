package moe.lacota.practicecore.party;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PartySeparator {
	private final Party party;

	public List<MiniTeam> separateTeams() {
		List<MiniTeam> list = Arrays.asList(new MiniTeam(), new MiniTeam());

		party.getPlayers().forEach(uuid -> {
			MiniTeam team = getSmallestTeam(list);
			team.players.add(uuid);
		});

		return list;
	}

	private MiniTeam getSmallestTeam(List<MiniTeam> teams) {
		MiniTeam smallestTeam = null;

		for (MiniTeam team : teams) {
			if (smallestTeam == null || team.getSize() < smallestTeam.getSize()) {
				smallestTeam = team;
			}
		}

		return smallestTeam;
	}

	public static class MiniTeam {
		@Getter
		private List<UUID> players = new ArrayList<>();

		public int getSize() {
			return players.size();
		}
	}
}
