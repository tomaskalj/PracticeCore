package moe.lacota.practicecore.commands;

import lombok.RequiredArgsConstructor;
import moe.lacota.practicecore.PracticePlugin;
import moe.lacota.practicecore.duels.DuelManager.DuelType;
import moe.lacota.practicecore.players.PracticePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class EloCommand implements CommandExecutor {
	private final PracticePlugin plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command c, String l, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command.");
			return true;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			player.performCommand("elo " + player.getName());
			return true;
		}

		if (plugin.getServer().getPlayer(args[0]) == null && !plugin.getServer().getPlayer(args[0]).isOnline()) {
			player.sendMessage(ChatColor.RED + args[0] + " is not online.");
			return true;
		}

		Player target = plugin.getServer().getPlayer(args[0]);
		PracticePlayer ppTarget = plugin.getPlayerManager().getPlayer(target);

		player.sendMessage(ChatColor.AQUA + target.getName() + "'s Statistics");
		for (DuelType type : DuelType.values()) {
			player.sendMessage(ChatColor.YELLOW + type.getName() + ": " + ChatColor.GREEN + ppTarget.getEloByDuelType(type));
		}

		return true;
	}
}
