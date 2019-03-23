package moe.lacota.practicecore.duels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.lacota.practicecore.PracticePlugin;
import moe.lacota.practicecore.arenas.Arena;
import moe.lacota.practicecore.duels.Duel.RegularDuel;
import moe.lacota.practicecore.duels.Duel.TeamDuel;
import moe.lacota.practicecore.duels.Duel.TeamFFADuel;
import moe.lacota.practicecore.duels.Duel.TeamSplitDuel;
import moe.lacota.practicecore.party.Party;
import moe.lacota.practicecore.party.PartySeparator.MiniTeam;
import moe.lacota.practicecore.players.PracticePlayer;
import moe.lacota.practicecore.queue.QueueSystem;
import moe.lacota.practicecore.utils.JsonChat;
import moe.lacota.practicecore.utils.Locations;
import moe.lacota.practicecore.utils.PlayerUtil;
import moe.lacota.practicecore.utils.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class DuelManager implements Listener {
	private PracticePlugin plugin;

	private final List<Duel> duels = new ArrayList<>();
	private final Set<DuelRequest> duelRequests = new HashSet<>();
	private final Set<UUID> fighting = new HashSet<>();
	private final Set<Inventory> inventories = new HashSet<>();

	@Getter
	private final QueueSystem queueSystem;

	public DuelManager(PracticePlugin plugin) {
	    this.plugin = plugin;
	    this.queueSystem = new QueueSystem(plugin);
    }

	public List<Duel> getDuels() {
		return duels;
	}

	public boolean isFighting(Player player) {
		return fighting.contains(player.getUniqueId());
	}

	public void addDuelRequest(DuelRequest request) {
		duelRequests.add(request);
	}

	public void addDuelRequest(Player requester, Player requested, DuelType type, Arena arena) {
		DuelRequest request = new DuelRequest();
		request.requester = requester;
		request.requested = requested;
		request.type = type;
		request.arena = arena;
		request.expires = System.currentTimeMillis() + 60000;

		duelRequests.add(request);
	}

	public void removeDuelRequest(DuelRequest request) {
		duelRequests.remove(request);
	}

	public DuelRequest getDuelRequest(Player requester, Player requested) {
		return duelRequests.stream().filter(request -> request.requester == requester).filter(request -> request.requested == requested).findFirst().orElse(null);
	}

	public Inventory getInventory(Player player) {
		return inventories.stream().filter(inventory -> ChatColor.stripColor(inventory.getName()).equalsIgnoreCase(player.getName() + "'s Inventory")).findFirst().orElse(null);
	}

	public Duel getDuel(Player player) {
		if (!isFighting(player)) {
			return null;
		}

		Duel duel = null;

		for (Duel d : duels) {
			if (d instanceof RegularDuel) {
				RegularDuel rd = (RegularDuel) d;

				if (rd.getPlayer() == player || rd.getOther() == player) {
					duel = rd;
					break;
				}
			} else if (d instanceof TeamDuel) {
				TeamDuel td = (TeamDuel) d;

				if (td.getParty().getPlayers().contains(player.getUniqueId())) {
					duel = td;
					break;
				}
			}
		}

		return duel;
	}

	public List<Player> getDuellingWith(Player player) {
		Duel duel = getDuel(player);

		if (duel == null) {
			return null;
		}

		List<Player> players = new ArrayList<>();

		if (duel instanceof RegularDuel) {
			RegularDuel regDuel = (RegularDuel) duel;

			Player with = null;

			if (regDuel.getPlayer() == player) {
				with = regDuel.getOther();
			} else {
				with = regDuel.getPlayer();
			}

			if (with != null) {
				players.add(with);
			}
		} else if (duel instanceof TeamDuel) {
			TeamDuel teamDuel = (TeamDuel) duel;

			if (teamDuel instanceof TeamSplitDuel) {
				TeamSplitDuel teamSplitDuel = (TeamSplitDuel) teamDuel;

				List<Player> teamAPlayers = teamSplitDuel.getTeamA().getPlayers().stream().filter(uuid -> plugin.getServer().getPlayer(uuid) != null).filter(uuid -> plugin.getServer().getPlayer(uuid).isOnline()).map(plugin.getServer()::getPlayer).collect(Collectors.toList());
				List<Player> teamBPlayers = teamSplitDuel.getTeamB().getPlayers().stream().filter(uuid -> plugin.getServer().getPlayer(uuid) != null).filter(uuid -> plugin.getServer().getPlayer(uuid).isOnline()).map(plugin.getServer()::getPlayer).collect(Collectors.toList());

				players.addAll(teamAPlayers);
				players.addAll(teamBPlayers);
			} else if (teamDuel instanceof TeamFFADuel) {
				TeamFFADuel teamFFADuel = (TeamFFADuel) teamDuel;

				players.addAll(teamFFADuel.getPlayers().stream().filter(uuid -> plugin.getServer().getPlayer(uuid) != null)
						.filter(uuid -> plugin.getServer().getPlayer(uuid).isOnline()).map(plugin.getServer()::getPlayer)
						.collect(Collectors.toList()));
			}
		}

		return players;
	}

	public void startRegularDuel(DuelType type, Arena arena, Player player, Player other, boolean ranked) {
		RegularDuel duel = new RegularDuel(type, arena, player, other, ranked);
		duels.add(duel);

//		player.teleport(Locations.SPAWN_POINT_A);
//		other.teleport(Locations.SPAWN_POINT_B);

		player.teleport(arena.getSpawnPointA());
		other.teleport(arena.getSpawnPointB());

		plugin.getServer().getOnlinePlayers().forEach(pl -> {
			if (player.canSee(pl)) {
				player.hidePlayer(pl);
			}
			if (other.canSee(pl)) {
				other.hidePlayer(pl);
			}
		});

		player.showPlayer(other);
		other.showPlayer(player);

		Arrays.asList(player, other).forEach(pl -> {
			if (plugin.getQueueSystem().isInQueue(pl)) {
				plugin.getQueueSystem().exitQueue(pl, false);
			}

			fighting.add(pl.getUniqueId());

			PlayerUtil.reset(pl);

			DuelUtil.applyByDuelType(pl, type);
		});
	}

	public void startTeamDuel(DuelType type, Party party, boolean ffa) {
		if (ffa) {
			TeamFFADuel duel = new TeamFFADuel(type, null, party);
			duels.add(duel);

			List<Player> players = duel.getPlayers().stream().filter(uuid -> plugin.getServer().getPlayer(uuid) != null).filter(uuid -> plugin.getServer().getPlayer(uuid).isOnline()).map(plugin.getServer()::getPlayer).collect(Collectors.toList());

			List<Location> spawnLocations = new ArrayList<>();
			spawnLocations.addAll(Locations.POSSIBLE_SPAWNS);

			players.forEach(player -> {
				player.teleport(spawnLocations.remove(0));

				PlayerUtil.hideFromAllExcept(player, players);

				fighting.add(player.getUniqueId());

				PlayerUtil.reset(player);

				DuelUtil.applyByDuelType(player, type);
			});
		} else {
			TeamSplitDuel duel = new TeamSplitDuel(type, null, party);
			duels.add(duel);

			MiniTeam teamA = duel.getTeamA();
			MiniTeam teamB = duel.getTeamB();

			List<Player> teamAPlayers = teamA.getPlayers().stream().filter(uuid -> plugin.getServer().getPlayer(uuid) != null).filter(uuid -> plugin.getServer().getPlayer(uuid).isOnline()).map(plugin.getServer()::getPlayer).collect(Collectors.toList());
			List<Player> teamBPlayers = teamB.getPlayers().stream().filter(uuid -> plugin.getServer().getPlayer(uuid) != null).filter(uuid -> plugin.getServer().getPlayer(uuid).isOnline()).map(plugin.getServer()::getPlayer).collect(Collectors.toList());

			List<Player> both = new ArrayList<>();
			both.addAll(teamAPlayers);
			both.addAll(teamBPlayers);

//			Arrays.asList(teamAPlayers, teamBPlayers).forEach(list -> {
//				list.forEach(player -> {
//					PlayerUtil.hideFromAllExcept(player, both);
//				});
//			});

			teamAPlayers.forEach(player -> {
				player.teleport(Locations.SPAWN_POINT_A);
				player.sendMessage(ChatColor.GOLD + "You are on Party A and are fighting: " + StringUtil.formatPlayersByPlayerCollection(teamBPlayers));
			});

			teamBPlayers.forEach(other -> {
				other.teleport(Locations.SPAWN_POINT_B);
				other.sendMessage(ChatColor.GOLD + "You are on Party B and are fighting: " + StringUtil.formatPlayersByPlayerCollection(teamAPlayers));
			});

			both.forEach(player -> {
				PlayerUtil.hideFromAllExcept(player, both);

				fighting.add(player.getUniqueId());

				PlayerUtil.reset(player);

				DuelUtil.applyByDuelType(player, type);
			});
		}
	}

	public void onDeath(Player player) {
		if (!isFighting(player)) {
			return;
		}

		Duel duel = getDuel(player);

		if (duel == null) {
			return;
		}

		if (duel instanceof RegularDuel) {
			RegularDuel regDuel = (RegularDuel) duel;

			duels.remove(regDuel);

			Player killer;

			if (regDuel.getPlayer() == player) {
				killer = regDuel.getOther();
			} else {
				killer = regDuel.getPlayer();
			}

			if (killer == null) {
				return;
			}

			for (Player pl : plugin.getServer().getOnlinePlayers()) {
				if (!player.canSee(pl)) {
					player.showPlayer(pl);
				}
				if (!killer.canSee(pl)) {
					killer.showPlayer(pl);
				}
			}

			PracticePlayer ppPlayer = plugin.getPlayerManager().getPlayer(player);
			PracticePlayer ppKiller = plugin.getPlayerManager().getPlayer(killer);

			if (regDuel.isRanked()) {
				/**
				 * Start of ELO changing logic
				 */

				int oldLoserElo = ppPlayer.getEloByDuelType(regDuel.getType());
				int oldWinnerElo = ppKiller.getEloByDuelType(regDuel.getType());

				int newLoserElo = plugin.getEloRatingSystem().getNewRating(oldLoserElo, oldWinnerElo, false);
				int newWinnerElo = plugin.getEloRatingSystem().getNewRating(oldWinnerElo, oldLoserElo, true);

				ppPlayer.setEloByDuelType(regDuel.getType(), newLoserElo);
				ppKiller.setEloByDuelType(regDuel.getType(), newWinnerElo);

				killer.sendMessage(ChatColor.YELLOW + "You won your duel against " + player.getName() + ". Your " + regDuel.getType().getName() + " ELO raised from " + oldWinnerElo + " to " + newWinnerElo + ".");
				player.sendMessage(ChatColor.RED + "You lost your duel against " + killer.getName() + ". Your " + regDuel.getType().getName() + " ELO dropped from " + oldLoserElo + " to " + newLoserElo + ".");

				/**
				 * End of ELO changing logic
				 */
			} else {
				killer.sendMessage(ChatColor.YELLOW + "You won your " + regDuel.getType().getName() + " duel against " + player.getName() + ".");
				player.sendMessage(ChatColor.RED + "You lost your " + regDuel.getType().getName() + " duel against " + killer.getName() + ".");
			}

			Inventory playerInv = player.getInventory();
			Inventory newPlayerInv = plugin.getServer().createInventory(null, playerInv.getSize(), ChatColor.GREEN + player.getName() + "'s Inventory");
			for (int i = 0; i < playerInv.getSize(); i++) {
				newPlayerInv.setItem(i, playerInv.getItem(i));
			}

			Inventory killerInv = killer.getInventory();
			Inventory newKillerInv = plugin.getServer().createInventory(null, killerInv.getSize(), ChatColor.GREEN + killer.getName() + "'s Inventory");
			for (int i = 0; i < killerInv.getSize(); i++) {
				newKillerInv.setItem(i, killerInv.getItem(i));
			}

			Arrays.asList(killer, player).forEach(pl -> {
				PlayerUtil.reset(pl);
				PlayerUtil.giveItems(pl);

				fighting.remove(pl.getUniqueId());

				pl.teleport(Locations.SPAWN);
			});

			inventories.add(newPlayerInv);
			inventories.add(newKillerInv);

			JsonChat.sendClickableMessage(player, ChatColor.GREEN + "Click to see " + killer.getName() + "'s inventory.", "Click to see " + killer.getName() + "'s inventory", "/inv " + killer.getName());
			JsonChat.sendClickableMessage(killer, ChatColor.GREEN + "Click to see " + player.getName() + "'s inventory.", "Click to see " + player.getName() + "'s inventory", "/inv " + player.getName());
		} else if (duel instanceof TeamDuel) {
			TeamDuel teamDuel = (TeamDuel) duel;

			if (teamDuel instanceof TeamSplitDuel) {
				TeamSplitDuel teamSplitDuel = (TeamSplitDuel) teamDuel;

				MiniTeam playerTeam = null;
				String teamType = "";

				if (teamSplitDuel.getTeamA().getPlayers().contains(player.getUniqueId())) {
					playerTeam = teamSplitDuel.getTeamA();
					teamType = "A";
				} else {
					playerTeam = teamSplitDuel.getTeamB();
					teamType = "B";
				}

				playerTeam.getPlayers().remove(player.getUniqueId());

				PlayerUtil.reset(player);
				PlayerUtil.giveItems(player);
				fighting.remove(player.getUniqueId());
				player.teleport(Locations.SPAWN);
				teamDuel.getParty().message(ChatColor.RED + player.getName() + " died!");

				if (playerTeam.getSize() <= 0) {
//					MiniTeam victor = teamType.equalsIgnoreCase("A") ? teamDuel.getTeamB() : teamDuel.getTeamA();
					String victorType = teamType.equalsIgnoreCase("A") ? "B" : "A";

					teamDuel.getParty().message(ChatColor.GOLD + "Party " + victorType + " has won!");

					duels.remove(teamDuel);

					teamDuel.getParty().getPlayers().stream().filter(uuid -> plugin.getServer().getPlayer(uuid) != null).filter(uuid -> plugin.getServer().getPlayer(uuid).isOnline()).map(plugin.getServer()::getPlayer).filter(pl -> isFighting(pl)).forEach(pl -> {
						PlayerUtil.reset(pl);
						PlayerUtil.giveItems(pl);
						fighting.remove(pl.getUniqueId());
						pl.teleport(Locations.SPAWN);
					});
				}
			} else if (teamDuel instanceof TeamFFADuel) {
				TeamFFADuel teamFFADuel = (TeamFFADuel) teamDuel;

				teamFFADuel.getPlayers().remove(player.getUniqueId());

				PlayerUtil.reset(player);
				PlayerUtil.giveItems(player);
				fighting.remove(player.getUniqueId());
				player.teleport(Locations.SPAWN);
				teamDuel.getParty().message(ChatColor.RED + player.getName() + " died!");

				if (teamFFADuel.getPlayers().size() == 1) {
					Player winner = plugin.getServer().getPlayer(teamFFADuel.getPlayers().get(0));
					PlayerUtil.reset(winner);
					PlayerUtil.giveItems(winner);
					fighting.remove(winner.getUniqueId());
					winner.teleport(Locations.SPAWN);

					teamDuel.getParty().message(ChatColor.GOLD + winner.getName() + " has won the Party FFA!");

					duels.remove(teamDuel);
				}
			}
		}
	}

	/*
	 * Start of Listeners
	 */

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void cancelPotionSplash(PotionSplashEvent e) {
		if (!(e.getPotion().getShooter() instanceof Player)) {
			return;
		}

		Player shooter = (Player) e.getPotion().getShooter();

		Duel duel = getDuel(shooter);

		if (duel == null) {
			return;
		}

		List<Player> with = getDuellingWith(shooter);

		if (with.isEmpty()) {
			return;
		}

		Set<LivingEntity> toRemove = e.getAffectedEntities().stream()
				.filter(entity -> entity instanceof Player)
				.filter(entity -> entity != shooter)
				.filter(entity -> !with.contains(entity))
				.collect(Collectors.toSet());

		toRemove.forEach(entity -> e.setIntensity(entity, 0));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onClick(InventoryClickEvent e) {
		Optional<Inventory> inv = inventories.stream().filter(inventory -> inventory.getName().equalsIgnoreCase(e.getInventory().getName())).findFirst();
		if (inv.isPresent()) {
			e.setCancelled(true);
		}
	}

	/*
	 * End of Listeners
	 */
}
