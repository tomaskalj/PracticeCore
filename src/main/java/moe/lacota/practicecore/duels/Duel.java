package moe.lacota.practicecore.duels;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import moe.lacota.practicecore.arenas.Arena;
import moe.lacota.practicecore.party.Party;
import moe.lacota.practicecore.party.PartySeparator;
import moe.lacota.practicecore.party.PartySeparator.MiniTeam;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class Duel {
	private DuelType type;
	private Arena arena;

	public static class RegularDuel extends Duel {
		private Player player;
		private Player other;
		private boolean ranked;

		public RegularDuel(DuelType type, Arena arena, Player player, Player other, boolean ranked) {
			super(type, arena);
			this.player = player;
			this.other = other;
			this.ranked = ranked;
		}

		public Player getPlayer() {
			return player;
		}

		public Player getOther() {
			return other;
		}

		public boolean isRanked() {
			return ranked;
		}
	}

	public static class TeamDuel extends Duel {
		private Party party;

		public TeamDuel(DuelType type, Arena arena, Party party) {
			super(type, arena);
			this.party = party;
		}

		public Party getParty() {
			return party;
		}

	}

	public static class TeamSplitDuel extends TeamDuel {
		private MiniTeam teamA;
		private MiniTeam teamB;

		public TeamSplitDuel(DuelType type, Arena arena, Party party) {
			super(type, arena, party);
			PartySeparator separator = new PartySeparator(party);
			List<MiniTeam> teams = separator.separateTeams();

			if (teams.size() != 2) {
				return;
			}

			teamA = teams.get(0);
			teamB = teams.get(1);
		}

		public MiniTeam getTeamA() {
			return teamA;
		}

		public MiniTeam getTeamB() {
			return teamB;
		}
	}

	public static class TeamFFADuel extends TeamDuel {
		private List<UUID> players;

		public TeamFFADuel(DuelType type, Arena arena, Party party) {
			super(type, arena, party);

			players = new ArrayList<>();
			players.addAll(party.getPlayers());
		}

		public List<UUID> getPlayers() {
			return players;
		}
	}
}
