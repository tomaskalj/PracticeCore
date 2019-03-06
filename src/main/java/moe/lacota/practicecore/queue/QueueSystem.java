package moe.lacota.practicecore.queue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import moe.lacota.practicecore.PracticePlugin;
import moe.lacota.practicecore.duels.DuelType;
import moe.lacota.practicecore.party.Party;
import moe.lacota.practicecore.utils.ItemUtil;
import moe.lacota.practicecore.utils.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class QueueSystem {
//	public static void main(String[] args) {
//		int elo = 1200;
//		List<Integer> list = Arrays.asList(1169, 1108, 1294, 1230);
//
//		int closestElo = Math.abs(list.get(0) - elo);
//		int closestIndex = 0;
//
//		for (int i = 0; i < list.size(); i++) {
//			int tempClosest = Math.abs(list.get(i) - elo);
//			if (tempClosest < closestElo) {
//				closestIndex = i;
//				closestElo = tempClosest;
//			}
//		}
//
//		System.out.println(list.get(closestIndex));
//	}

	private PracticePlugin plugin;
	private Map<UUID, QueueEntry> queued;

	public QueueSystem(PracticePlugin plugin) {
		this.plugin = plugin;
		queued = new HashMap<>();
	}

	public boolean isInQueue(Player player) {
		return queued.containsKey(player.getUniqueId());
	}

	public int getInDuelQueue(DuelType type, boolean ranked) {
		int count = 0;

		for (QueueEntry entry : queued.values()) {
			if (entry.getType() != type) {
				continue;
			}
			if (entry.isRanked() != ranked) {
				continue;
			}
			count++;
		}

		return count;
	}

	public void enterQueue(Player player, DuelType type, boolean ranked) {
		if (queued.containsKey(player.getUniqueId())) {
			player.sendMessage(ChatColor.RED + "You are already in a queue.\nClick the red dye to exit your current queue.");
			player.closeInventory();
			return;

//			QueueEntry queue = queued.get(player.getUniqueId());
//			if(queue.getType() == type) {
//				queued.remove(player.getUniqueId());
//				player.sendMessage(ChatColor.GREEN + "You have left the " + (ranked ? "ranked " : "unranked ") + type.getName() + " queue.");
//				return;
//			}
		}

		Party party = plugin.getPartyManager().getTeam(player);

		if (party != null) {
			player.sendMessage(ChatColor.RED + "You may not enter any queues while in a party.");
			return;
		}

		int elo = plugin.getPlayerManager().getPlayer(player).getEloByDuelType(type);

		if (ranked) {
			player.sendMessage(ChatColor.YELLOW + "You joined " + ChatColor.GREEN + type.getName() + ChatColor.YELLOW + " Ranked queue with " + ChatColor.GREEN + elo + " elo.");
		} else {
			player.sendMessage(ChatColor.YELLOW + "You joined " + ChatColor.GREEN + type.getName() + ChatColor.YELLOW + " Unranked queue.");
		}

		player.getInventory().clear();
		player.getInventory().setItem(0, ItemUtil.createItem(Material.INK_SACK, ChatColor.RED + "Exit Queue", (short) 1));

		QueueEntry queueEntry = new QueueEntry(elo, type, ranked);
		queued.put(player.getUniqueId(), queueEntry);

		List<UUID> players = queued.entrySet().stream()
				.filter(entry -> Bukkit.getPlayer(entry.getKey()) != player)
				.filter(entry -> entry.getValue().getType() == type)
				.filter(entry -> entry.getValue().isRanked() == ranked)
				.map(entry -> entry.getKey())
				.collect(Collectors.toList());

		if (players.size() < 1) {
			return;
		}

		Player matched;

		if (ranked) {
			matched = searchQueue(player, players);
		} else {
			matched = Bukkit.getPlayer(players.get(0));
		}

		if (matched != null) {
			QueueEntry entry = queued.get(matched.getUniqueId());

			if (entry.getType() == type && entry.isRanked() == ranked) {
				plugin.getDuelManager().startRegularDuel(type, plugin.getArenaManager().getRandomArena(), player, matched, ranked);
			}
		}
	}

	private Player searchQueue(Player player, List<UUID> players) {
		Player matched = null;

		final QueueEntry entry = queued.get(player.getUniqueId());
		final int elo = entry.getElo();

		int starting = 200;
		int min = elo - starting;
		int max = elo + starting;
		int tries = 0;

		while (true) {
			if (tries >= 10) {
				player.sendMessage(ChatColor.RED + "No matches found.");
				break;
			}

			if (starting % 100 == 0) {
				player.sendMessage(ChatColor.YELLOW + "Searching in elo range " + ChatColor.GREEN + "[" + min + " -> " + max + "]");
			}

			int closestElo = Math.abs(queued.get(players.get(0)).getElo() - elo);
			int closestIndex = 0;

			for (int i = 0; i < players.size(); i++) {
				int tempClosest = Math.abs(queued.get(players.get(i)).getElo() - elo);
				if (tempClosest < closestElo) {
					closestIndex = i;
					closestElo = tempClosest;
				}
			}

			if (MathUtil.isWithin(Math.abs(closestElo - elo), min, max)) {
				matched = Bukkit.getPlayer(players.get(closestIndex));
				player.sendMessage(ChatColor.YELLOW + "Found " + ChatColor.GREEN + matched.getName() + ChatColor.YELLOW + " who has an elo of " + ChatColor.GREEN + closestElo + ".");
				break;
			}

			tries++;
			starting += 100;
			min = elo - starting;
			max = elo + starting;
		}

		return matched;
	}

	public void exitQueue(Player player, boolean msg) {
		if (!queued.containsKey(player.getUniqueId())) {
			return;
		}

		QueueEntry entry = queued.remove(player.getUniqueId());
		if (msg) {
			player.sendMessage(ChatColor.RED + "You have left the " + (entry.isRanked() ? "ranked " : "unranked ") + entry.getType().getName() + " queue.");
		}
	}
}
