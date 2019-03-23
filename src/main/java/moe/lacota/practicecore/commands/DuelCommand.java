package moe.lacota.practicecore.commands;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import moe.lacota.practicecore.PracticePlugin;
import moe.lacota.practicecore.arenas.Arena;
import moe.lacota.practicecore.duels.DuelRequest;
import moe.lacota.practicecore.duels.DuelType;
import moe.lacota.practicecore.utils.ItemUtil;
import moe.lacota.practicecore.utils.JsonChat;
import moe.lacota.practicecore.utils.gui.GuiClickable;
import moe.lacota.practicecore.utils.gui.GuiExit;
import moe.lacota.practicecore.utils.gui.GuiFiller;
import moe.lacota.practicecore.utils.gui.GuiFolder;
import moe.lacota.practicecore.utils.gui.GuiPage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

@RequiredArgsConstructor
public class DuelCommand implements CommandExecutor {
	private final PracticePlugin plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command c, String l, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command.");
			return true;
		}

		Player player = (Player) sender;

		if (args.length != 1) {
			player.sendMessage(ChatColor.RED + "Please use /duel <player>");
			return true;
		}

		if (plugin.getDuelManager().isFighting(player)) {
			player.sendMessage(ChatColor.RED + "You cannot use this command while fighting.");
			return true;
		}

		if (plugin.getServer().getPlayer(args[0]) == null && !plugin.getServer().getPlayer(args[0]).isOnline()) {
			player.sendMessage(ChatColor.RED + args[0] + " not found.");
			return true;
		}

		if (player.getName().equalsIgnoreCase(args[0])) {
			player.sendMessage(ChatColor.RED + "You cannot challenge yourself to a duel!");
			return true;
		}

		if (plugin.getPartyManager().getTeam(player) != null) {
			player.sendMessage(ChatColor.RED + "You cannot duel someone while in a party.");
			return true;
		}

		Player target = plugin.getServer().getPlayer(args[0]);

		DuelRequest request = new DuelRequest();
		request.requester = player;
		request.requested = target;

		GuiFolder folder = new GuiFolder(ChatColor.GREEN + "Customize your Duel", 9);
		GuiPage arenaPage = new GuiPage(folder);

		int index = 0;
		for (Arena arena : plugin.getArenaManager().getArenas()) {
			arenaPage.addItem(index, new GuiClickable() {
				@Override
				public ItemStack getItemStack() {
					return ItemUtil.createItem(new ItemStack(Material.INK_SACK, 1, (byte) 10), ChatColor.GREEN + arena.getName(), Collections.singletonList(ChatColor.GRAY + "Fight on " + arena.getName()));
				}

				@Override
				public void onClick(InventoryClickEvent event) {
					request.arena = arena;

					GuiPage duelPage = new GuiPage(folder);

					duelPage.addItem(0, new GuiClickable() {
						@Override
						public ItemStack getItemStack() {
							return ItemUtil.createItem(new Potion(PotionType.INSTANT_HEAL, 2).splash().toItemStack(1), ChatColor.GREEN + "NoDebuff", Collections.singletonList(ChatColor.GRAY + "Play with the NoDebuff kit"));
						}

						@Override
						public void onClick(InventoryClickEvent event) {
							request.type = DuelType.NO_DEBUFF;
							request.expires = System.currentTimeMillis() + 60000;

							player.closeInventory();

							onDuelRequest(request);
						}
					});

					duelPage.addItem(1, new GuiClickable() {
						@Override
						public ItemStack getItemStack() {
							return ItemUtil.createItem(new Potion(PotionType.POISON).splash().toItemStack(1), ChatColor.GREEN + "Debuff", Collections.singletonList(ChatColor.GRAY + "Play with the Debuff kit"));
						}

						@Override
						public void onClick(InventoryClickEvent event) {
							request.type = DuelType.DEBUFF;
							request.expires = System.currentTimeMillis() + 60000;

							player.closeInventory();

							onDuelRequest(request);
						}
					});

					duelPage.addItem(8, new GuiExit());

					for (int i = 0; i < folder.getSize(); i++) {
						if (duelPage.getItem(i) == null) {
							duelPage.addItem(i, new GuiFiller());
						}
					}

					folder.setCurrentPage(duelPage);
				}
			});

			index++;
		}

		arenaPage.addItem(8, new GuiExit());

		for (int i = 0; i < folder.getSize(); i++) {
			if (arenaPage.getItem(i) == null) {
				arenaPage.addItem(i, new GuiFiller());
			}
		}

		folder.setCurrentPage(arenaPage);
		folder.openGui(player);

		return true;
	}

	private void onDuelRequest(DuelRequest request) {
		Player requester = request.requester;
		Player requested = request.requested;

		if (plugin.getDuelManager().getDuelRequest(requester, requested) != null) {
			DuelRequest pending = plugin.getDuelManager().getDuelRequest(requester, requested);
			if (!pending.hasExpired()) {
				requester.sendMessage(ChatColor.RED + "You already have a pending duel request to " + requested.getName() + ".");
				return;
			} else {
				plugin.getDuelManager().removeDuelRequest(pending);
			}
		}

		plugin.getDuelManager().addDuelRequest(request);

		requester.sendMessage(ChatColor.GREEN + "You sent a duel request to " + requested.getName());
		JsonChat.sendClickableMessage(requested, ChatColor.LIGHT_PURPLE + requester.getName() + " sent a " + ChatColor.AQUA + request.type.getName() + ChatColor.LIGHT_PURPLE + " duel request to you on " + request.arena.getName() + ". Click to accept!", "Click to accept!", "/accept " + requester.getName());
	}
}
