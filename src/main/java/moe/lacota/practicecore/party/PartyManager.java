package moe.lacota.practicecore.party;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moe.lacota.practicecore.PracticePlugin;
import moe.lacota.practicecore.duels.DuelType;
import moe.lacota.practicecore.utils.ItemUtil;
import moe.lacota.practicecore.utils.PlayerUtil;
import moe.lacota.practicecore.utils.StringUtil;
import moe.lacota.practicecore.utils.gui.GuiClickable;
import moe.lacota.practicecore.utils.gui.GuiExit;
import moe.lacota.practicecore.utils.gui.GuiFiller;
import moe.lacota.practicecore.utils.gui.GuiFolder;
import moe.lacota.practicecore.utils.gui.GuiPage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

@Getter
@RequiredArgsConstructor
public class PartyManager {
	private final PracticePlugin plugin;

	private final List<Party> parties = new ArrayList<>();
	private final Map<UUID, Party> pendingInvites = new HashMap<>();

	public Party createTeam(Player creator) {
		Party party = new Party(creator.getUniqueId());
		parties.add(party);
		return party;
	}

	public void disbandOrLeaveParty(Player player, Party party) {
		if (party.isCreator(player.getUniqueId())) {
			party.message(org.bukkit.ChatColor.RED + player.getName() + " has disbanded the party.");
			deleteTeam(party);
		} else {
			party.message(org.bukkit.ChatColor.RED + player.getName() + " has left the party.");
			party.removePlayer(player.getUniqueId());
		}
	}

	private void deleteTeam(Party party) {
		party.getPlayers().clear();
		parties.remove(party);
	}

	public Party getTeam(Player player) {
		return parties.stream().filter(team -> team.getPlayers().contains(player.getUniqueId())).findFirst().orElse(null);
	}

	public void equipTeamItems(Player player) {
		PlayerUtil.reset(player);

		Party party = getTeam(player);
		if (party.isCreator(player.getUniqueId())) {
			player.getInventory().setItem(4, ItemUtil.createItem(Material.CHEST, ChatColor.GREEN + "Party Manager", (byte) 0));
		}

		player.getInventory().setItem(7, ItemUtil.createItem(Material.ENDER_CHEST, ChatColor.YELLOW + "List players", (byte) 0));
		player.getInventory().setItem(8, ItemUtil.createItem(Material.IRON_FENCE, ChatColor.RED + "Leave your party", (byte) 0));
	}

	public void openTeamGui(Player player) {
		Party party = getTeam(player);

		GuiFolder folder = new GuiFolder(ChatColor.GREEN + "Party Manager", 9);
		GuiPage optionPage = new GuiPage(folder);

		optionPage.addItem(0, new GuiClickable() {
			@Override
			public ItemStack getItemStack() {
				return ItemUtil.createItem(Material.DIAMOND_SWORD, ChatColor.YELLOW + "Party Fight", Arrays.asList(StringUtil.separateDescription("Split your party into different parties and fight each other.")));
			}

			@Override
			public void onClick(InventoryClickEvent event) {
				GuiPage teamFightPage = new GuiPage(folder);

				teamFightPage.addItem(0, new GuiClickable() {
					@Override
					public ItemStack getItemStack() {
						return ItemUtil.createItem(new Potion(PotionType.INSTANT_HEAL, 2).splash().toItemStack(1), ChatColor.GREEN + "NoDebuff");
					}

					@Override
					public void onClick(InventoryClickEvent event) {
						player.closeInventory();

						plugin.getDuelManager().startTeamDuel(DuelType.NO_DEBUFF, party, false);
					}
				});

				teamFightPage.addItem(1, new GuiClickable() {
					@Override
					public ItemStack getItemStack() {
						return ItemUtil.createItem(new Potion(PotionType.POISON).splash().toItemStack(1), ChatColor.GREEN + "Debuff");
					}

					@Override
					public void onClick(InventoryClickEvent event) {
						player.closeInventory();

						plugin.getDuelManager().startTeamDuel(DuelType.DEBUFF, party, false);
					}
				});

				teamFightPage.addItem(8, new GuiExit());

				for (int i = 0; i < folder.getSize(); i++) {
					if (teamFightPage.getItem(i) == null) {
						teamFightPage.addItem(i, new GuiFiller());
					}
				}

				if (party.getSize() >= 2) {
					folder.setCurrentPage(teamFightPage);
				} else {
					player.sendMessage(ChatColor.RED + "You need at least 2 players in your party to do this option.");
					player.closeInventory();
				}
			}
		});

		optionPage.addItem(1, new GuiClickable() {
			@Override
			public ItemStack getItemStack() {
				return ItemUtil.createItem(Material.IRON_SWORD, ChatColor.YELLOW + "Party FFA", Arrays.asList(StringUtil.separateDescription("Fight everyone on your party in a big free for all.")));
			}

			@Override
			public void onClick(InventoryClickEvent event) {
				GuiPage teamFFAPage = new GuiPage(folder);

				teamFFAPage.addItem(0, new GuiClickable() {
					@Override
					public ItemStack getItemStack() {
						return ItemUtil.createItem(new Potion(PotionType.INSTANT_HEAL, 2).splash().toItemStack(1), ChatColor.GREEN + "NoDebuff");
					}

					@Override
					public void onClick(InventoryClickEvent event) {
						player.closeInventory();

						plugin.getDuelManager().startTeamDuel(DuelType.NO_DEBUFF, party, true);
					}
				});

				teamFFAPage.addItem(1, new GuiClickable() {
					@Override
					public ItemStack getItemStack() {
						return ItemUtil.createItem(new Potion(PotionType.POISON).splash().toItemStack(1), ChatColor.GREEN + "Debuff");
					}

					@Override
					public void onClick(InventoryClickEvent event) {
						player.closeInventory();

						plugin.getDuelManager().startTeamDuel(DuelType.DEBUFF, party, true);
					}
				});

				teamFFAPage.addItem(8, new GuiExit());

				for (int i = 0; i < folder.getSize(); i++) {
					if (teamFFAPage.getItem(i) == null) {
						teamFFAPage.addItem(i, new GuiFiller());
					}
				}

				if (party.getSize() >= 2) {
					folder.setCurrentPage(teamFFAPage);
				} else {
					player.sendMessage(ChatColor.RED + "You need at least 2 players in your party to do this option.");
					player.closeInventory();
				}
			}
		});

		optionPage.addItem(8, new GuiExit());

		for (int i = 0; i < folder.getSize(); i++) {
			if (optionPage.getItem(i) == null) {
				optionPage.addItem(i, new GuiFiller());
			}
		}

		folder.setCurrentPage(optionPage);
		folder.openGui(player);
	}
}