package moe.lacota.practicecore.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command c, String l, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command.");
			return true;
		}

		Player player = (Player) sender;

		player.sendMessage(ChatColor.GREEN + "/duel <player> - " + ChatColor.YELLOW + "Challenge a player to a duel.");
		player.sendMessage(ChatColor.GREEN + "/elo [player] - " + ChatColor.YELLOW + "Check you or another player's elo.");
		player.sendMessage(ChatColor.GREEN + "/spectate <player> - " + ChatColor.YELLOW + "Spectate a player.");
		player.sendMessage(ChatColor.GREEN + "/party - " + ChatColor.YELLOW + "View the help index for teams.");

		return true;
	}
}
