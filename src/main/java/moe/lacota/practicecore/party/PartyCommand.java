package moe.lacota.practicecore.party;

import lombok.RequiredArgsConstructor;
import moe.lacota.practicecore.PracticePlugin;
import moe.lacota.practicecore.utils.JsonChat;
import moe.lacota.practicecore.utils.PlayerUtil;
import moe.lacota.practicecore.utils.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PartyCommand implements CommandExecutor {
	private final PracticePlugin plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command c, String l, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command.");
			return true;
		}

		Player player = (Player) sender;

		if (plugin.getDuelManager().isFighting(player)) {
			player.sendMessage(ChatColor.RED + "You cannot use this command while fighting.");
			return true;
		}

		if (args.length == 0) {
			player.sendMessage(ChatColor.GRAY + "Create a party: " + ChatColor.GREEN + "/party create");
			player.sendMessage(ChatColor.GRAY + "Invite players:  " + ChatColor.GREEN + "/party invite <player>");
			player.sendMessage(ChatColor.GRAY + "List players in party: " + ChatColor.GREEN + "/party list");
			player.sendMessage(ChatColor.GRAY + "Leave your party: " + ChatColor.GREEN + "/party leave");
			player.sendMessage(ChatColor.GRAY + "Kick a player from your party: " + ChatColor.GREEN + "/party kick <player>");
			return true;
		}

		if (args[0].equalsIgnoreCase("create")) {
			if (plugin.getPartyManager().getTeam(player) != null) {
				player.sendMessage(ChatColor.RED + "You are already in a party! To leave your party type /party leave.");
				return true;
			}

			plugin.getPartyManager().createTeam(player);
			plugin.getPartyManager().equipTeamItems(player);
//			player.getInventory().setItem(1, ItemUtil.createItem(Material.CHEST, ChatColor.GREEN + "Party Manager", (byte) 0));
			player.sendMessage(ChatColor.GREEN + "Your party has been created. For more help type /party.");
		} else if (args[0].equalsIgnoreCase("invite")) {
			Party party = plugin.getPartyManager().getTeam(player);

			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are not in a party.");
				return true;
			}

			if (!party.isCreator(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You must be the creator of your party to invite players.");
				return true;
			}

			if (args.length != 2) {
				player.sendMessage(ChatColor.RED + "Please use /party <invite> <player>");
				return true;
			}

			if (plugin.getServer().getPlayer(args[1]) == null && !plugin.getServer().getPlayer(args[1]).isOnline()) {
				player.sendMessage(ChatColor.RED + args[1] + " not found.");
				return true;
			}

			Player target = plugin.getServer().getPlayer(args[1]);

			if (plugin.getPartyManager().getTeam(target) != null) {
				player.sendMessage(ChatColor.RED + target.getName() + " is already in a party.");
				return true;
			}

			plugin.getPartyManager().getPendingInvites().put(target.getUniqueId(), party);

			player.sendMessage(ChatColor.GREEN + "You sent " + target.getName() + " an invite to join your party.");
			JsonChat.sendClickableMessage(target, ChatColor.LIGHT_PURPLE + player.getName() + " sent you an invite to join their party. Click to join!", ChatColor.AQUA + "Join " + player.getName() + "'s Party", "/party join " + player.getName());
		} else if (args[0].equalsIgnoreCase("list")) {
			Party party = plugin.getPartyManager().getTeam(player);

			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are not in a party.");
				return true;
			}

			String namesString = StringUtil.formatPlayers(party.getPlayers());

			player.sendMessage(ChatColor.GREEN + "Party Members (" + party.getSize() + "): " + ChatColor.GRAY + namesString);
		} else if (args[0].equalsIgnoreCase("leave")) {
			Party party = plugin.getPartyManager().getTeam(player);

			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are not in a party.");
				return true;
			}

			player.sendMessage(ChatColor.GRAY + "You have left your party.");

			plugin.getPartyManager().disbandOrLeaveParty(player, party);

			PlayerUtil.reset(player);
			PlayerUtil.giveItems(player);
		} else if (args[0].equalsIgnoreCase("join")) {
			if (!plugin.getPartyManager().getPendingInvites().containsKey(player.getUniqueId())) {
				return true;
			}

			Party party = plugin.getPartyManager().getPendingInvites().remove(player.getUniqueId());

			args[1] = PlayerUtil.fetchName(party.getCreator());

			if (PlayerUtil.fetchName(party.getCreator()).equalsIgnoreCase(args[1])) {
				party.getPlayers().add(player.getUniqueId());
				player.sendMessage(ChatColor.GREEN + "You have joined " + args[1] + "'s party.");
				plugin.getPartyManager().equipTeamItems(player);
			}
		} else if (args[0].equalsIgnoreCase("kick")) {
			Party party = plugin.getPartyManager().getTeam(player);

			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are not in a party.");
				return true;
			}

			if (!party.isCreator(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You must be the creator of your party to kick players.");
				return true;
			}

			if (args.length != 2) {
				player.sendMessage(ChatColor.RED + "Please use /party <invite> <player>");
				return true;
			}

			if (plugin.getServer().getPlayer(args[1]) == null && !plugin.getServer().getPlayer(args[1]).isOnline()) {
				player.sendMessage(ChatColor.RED + args[1] + " not found.");
				return true;
			}

			Player target = plugin.getServer().getPlayer(args[1]);

			Party targetParty = plugin.getPartyManager().getTeam(target);

			if (targetParty == null) {
				player.sendMessage(ChatColor.RED + target.getName() + " is not on a party.");
				return true;
			}

			if (party != targetParty) {
				player.sendMessage(ChatColor.RED + target.getName() + " is not on your party.");
				return true;
			}

			party.removePlayer(target.getUniqueId());
			party.message(ChatColor.RED + target.getName() + " has been removed from the party.");
			PlayerUtil.reset(target);
			PlayerUtil.giveItems(target);
		}

		return true;
	}
}