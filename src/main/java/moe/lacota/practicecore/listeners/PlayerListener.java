package moe.lacota.practicecore.listeners;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import moe.lacota.practicecore.PracticePlugin;
import moe.lacota.practicecore.duels.Duel;
import moe.lacota.practicecore.duels.DuelType;
import moe.lacota.practicecore.party.Party;
import moe.lacota.practicecore.players.PracticePlayer;
import moe.lacota.practicecore.utils.Cooldown;
import moe.lacota.practicecore.utils.ItemUtil;
import moe.lacota.practicecore.utils.Locations;
import moe.lacota.practicecore.utils.MathUtil;
import moe.lacota.practicecore.utils.PlayerUtil;
import moe.lacota.practicecore.utils.gui.GuiClickable;
import moe.lacota.practicecore.utils.gui.GuiExit;
import moe.lacota.practicecore.utils.gui.GuiFiller;
import moe.lacota.practicecore.utils.gui.GuiFolder;
import moe.lacota.practicecore.utils.gui.GuiPage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
	private final PracticePlugin plugin;

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);

		final PracticePlayer player = plugin.getPlayerManager().addPlayer(e.getPlayer().getUniqueId());

		if (!e.getPlayer().hasPlayedBefore()) {
			player.save();
		}

		if (player == null || !player.load()) {
			e.getPlayer().kickPlayer(ChatColor.RED + "Failed to load player data.\nTry again soon.");
			return;
		}

		final Player pl = player.getPlayer();

		Bukkit.getScheduler().runTaskLater(PracticePlugin.getInstance(), () -> pl.teleport(Locations.SPAWN), 1);

		PlayerUtil.reset(pl);
		PlayerUtil.giveItems(pl);

		Bukkit.getOnlinePlayers().stream().filter(other -> plugin.getDuelManager().isFighting(other)).forEach(other -> {
			List<Player> with = plugin.getDuelManager().getDuellingWith(other);

			other.hidePlayer(pl);
			if (!with.isEmpty()) {
				with.forEach(w -> w.hidePlayer(pl));
			}
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		e.setQuitMessage(null);

		Player player = e.getPlayer();
		Party party = plugin.getPartyManager().getTeam(player);

		if (party != null) {
			plugin.getPartyManager().disbandOrLeaveParty(player, party);
		}

		PracticePlayer pp = plugin.getPlayerManager().removePlayer(player.getUniqueId());
		pp.save();
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (!e.hasItem()) {
			return;
		}
		if (!e.getAction().name().contains("RIGHT")) {
			return;
		}

		if (Cooldown.hasCooldown(player.getName(), "NO_ACTION")) {
			return;
		}

		if (e.getItem().getType() == Material.BOOK) {
			e.setCancelled(true);

			plugin.getKitEditor().openInventory(player);
		} else if (e.getItem().getType() == Material.DIAMOND_SWORD) {
			if (plugin.getDuelManager().isFighting(player)) {
				return;
			}
			if (plugin.getKitEditor().isEditing(player)) {
				return;
			}

			e.setCancelled(true);

			GuiFolder folder = new GuiFolder(ChatColor.GREEN + "Ranked Queues", 9);
			GuiPage page = new GuiPage(folder);

			page.addItem(0, new GuiClickable() {
				@Override
				public ItemStack getItemStack() {
					return ItemUtil.createItem(new Potion(PotionType.INSTANT_HEAL, 2).splash().toItemStack(1), ChatColor.GREEN + "NoDebuff", Arrays.asList(plugin.getDuelManager().getQueueSystem().getInDuelQueue(DuelType.NO_DEBUFF, true) + " Queued"));
				}

				@Override
				public void onClick(InventoryClickEvent event) {
					player.closeInventory();

					plugin.getDuelManager().getQueueSystem().enterQueue(player, DuelType.NO_DEBUFF, true);
				}
			});

			page.addItem(1, new GuiClickable() {
				@Override
				public ItemStack getItemStack() {
					return ItemUtil.createItem(new Potion(PotionType.POISON).splash().toItemStack(1), ChatColor.GREEN + "Debuff", Arrays.asList(plugin.getDuelManager().getQueueSystem().getInDuelQueue(DuelType.DEBUFF, true) + " Queued"));
				}

				@Override
				public void onClick(InventoryClickEvent event) {
					player.closeInventory();

					plugin.getDuelManager().getQueueSystem().enterQueue(player, DuelType.DEBUFF, true);
				}
			});

			page.addItem(8, new GuiExit());

			for (int i = 0; i < folder.getSize(); i++) {
				if (page.getItem(i) == null) {
					page.addItem(i, new GuiFiller());
				}
			}

			folder.setCurrentPage(page);
			folder.openGui(player);
		} else if (e.getItem().getType() == Material.IRON_SWORD) {
			if (plugin.getDuelManager().isFighting(player)) {
				return;
			}
			if (plugin.getKitEditor().isEditing(player)) {
				return;
			}

			e.setCancelled(true);

			GuiFolder folder = new GuiFolder(ChatColor.GREEN + "Unranked Queues", 9);
			GuiPage page = new GuiPage(folder);

			page.addItem(0, new GuiClickable() {
				@Override
				public ItemStack getItemStack() {
					return ItemUtil.createItem(new Potion(PotionType.INSTANT_HEAL, 2).splash().toItemStack(1), ChatColor.GREEN + "NoDebuff", Arrays.asList(plugin.getDuelManager().getQueueSystem().getInDuelQueue(DuelType.NO_DEBUFF, false) + " Queued"));
				}

				@Override
				public void onClick(InventoryClickEvent event) {
					player.closeInventory();

					plugin.getDuelManager().getQueueSystem().enterQueue(player, DuelType.NO_DEBUFF, false);
				}
			});

			page.addItem(1, new GuiClickable() {
				@Override
				public ItemStack getItemStack() {
					return ItemUtil.createItem(new Potion(PotionType.POISON).splash().toItemStack(1), ChatColor.GREEN + "Debuff", Arrays.asList(plugin.getDuelManager().getQueueSystem().getInDuelQueue(DuelType.DEBUFF, false) + " Queued"));
				}

				@Override
				public void onClick(InventoryClickEvent event) {
					player.closeInventory();

					plugin.getDuelManager().getQueueSystem().enterQueue(player, DuelType.DEBUFF, false);
				}
			});

			page.addItem(8, new GuiExit());

			for (int i = 0; i < folder.getSize(); i++) {
				if (page.getItem(i) == null) {
					page.addItem(i, new GuiFiller());
				}
			}

			folder.setCurrentPage(page);
			folder.openGui(player);
		} else if (e.getItem().getType() == Material.WATCH) {
			e.setCancelled(true);
		} else if (e.getItem().getType() == Material.ENDER_PEARL) {
			if (Cooldown.hasCooldown(player.getName(), "ENDER_PEARL")) {
				e.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are on cooldown for " + MathUtil.millisToRoundedTime(Cooldown.getCooldown(player.getName(), "ENDER_PEARL")) + " seconds.");
				return;
			}

			Cooldown.addCooldown(player.getName(), "ENDER_PEARL", 16 * 1000);
		} else if (e.getItem().getType() == Material.INK_SACK) {
			e.setCancelled(true);

			plugin.getDuelManager().getQueueSystem().exitQueue(player, true);
			PlayerUtil.reset(player);
			PlayerUtil.giveItems(player);
		} else if (e.getItem().getType() == Material.CHEST) {
			e.setCancelled(true);

			plugin.getPartyManager().openTeamGui(player);
		} else if (e.getItem().getType() == Material.ENDER_CHEST) {
			player.performCommand("party list");
		} else if (e.getItem().getType() == Material.IRON_FENCE) {
			player.performCommand("party leave");
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();

		if (e.getInventory() == null) {
			return;
		}
		if (e.getCurrentItem() == null) {
			return;
		}

		if (e.getInventory().getName().equalsIgnoreCase("container.crafting") && !plugin.getDuelManager().isFighting(player) && !plugin.getKitEditor().isEditing(player)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) e.getEntity();

		if (!plugin.getDuelManager().isFighting(player)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) e.getEntity();

		if (!plugin.getDuelManager().isFighting(player)) {
			e.setCancelled(true);
			e.setFoodLevel(20);
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		if (!plugin.getDuelManager().isFighting(e.getPlayer())) {
			e.setCancelled(true);
		} else {
			ItemUtil.getDroppedItems().put(e.getItemDrop(), e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		if (!plugin.getDuelManager().isFighting(e.getPlayer())) {
			e.setCancelled(true);
		} else {
			Player dropper = Bukkit.getPlayer(ItemUtil.getDroppedItems().get(e.getItem()));

//			RegularDuel duel = PracticePlugin.getDuelManager().getRegularDuel(dropper);
//			
//			if(duel == null)
//				return;
//			
//			Player with = PracticePlugin.getDuelManager().getDuellingWith(dropper);
//			
//			if(with == null)
//				return;

			if (dropper == null || !dropper.isOnline()) {
				e.getItem().remove();
				return;
			}

			Duel duel = plugin.getDuelManager().getDuel(dropper);

			if (duel == null) {
				return;
			}

			List<Player> with = plugin.getDuelManager().getDuellingWith(dropper);

			if (with.isEmpty()) {
				return;
			}

			if (e.getPlayer() != dropper && !with.contains(e.getPlayer())) {
				e.setCancelled(true);
			}

//			if(e.getPlayer() != dropper && e.getPlayer() != with)
//				e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}
}
