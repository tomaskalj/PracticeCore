package moe.lacota.practicecore.commands;

import lombok.RequiredArgsConstructor;
import moe.lacota.practicecore.PracticePlugin;
import moe.lacota.practicecore.duels.DuelRequest;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class AcceptCommand implements CommandExecutor {
	private final PracticePlugin plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command c, String l, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command.");
			return true;
		}

		Player player = (Player) sender;

		if (args.length != 1) {
			player.sendMessage(ChatColor.RED + "Please use /accept <player>");
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

		if (plugin.getPartyManager().getTeam(player) != null) {
			player.sendMessage(ChatColor.RED + "You cannot accept a duel while in a party.");
			return true;
		}

		Player target = plugin.getServer().getPlayer(args[0]);

		if (plugin.getDuelManager().getDuelRequest(target, player) != null) {
			DuelRequest request = plugin.getDuelManager().getDuelRequest(target, player);
			if (!request.hasExpired()) {
				plugin.getDuelManager().removeDuelRequest(request);

				player.sendMessage(ChatColor.GREEN + "You accepted " + target.getName() + "'s duel request.");
				target.sendMessage(ChatColor.GREEN + player.getName() + " accepted your duel request.");

				plugin.getDuelManager().startRegularDuel(request.type, request.arena, player, target, false);
			}
		}

		return true;
	}
}
