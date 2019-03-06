package moe.lacota.practicecore.commands;

import lombok.RequiredArgsConstructor;
import moe.lacota.practicecore.PracticePlugin;
import moe.lacota.practicecore.utils.Locations;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SpectateCommand implements CommandExecutor {
	private final PracticePlugin plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command c, String l, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command.");
			return true;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			if (player.getGameMode() == GameMode.SPECTATOR) {
				player.setGameMode(GameMode.SURVIVAL);
				player.setAllowFlight(false);
				player.teleport(Locations.SPAWN);
				player.sendMessage(ChatColor.AQUA + "You have stopped spectating.");
				return true;
			}

			player.sendMessage(ChatColor.RED + "Please use /spectate <player>");
			return true;
		}

		if (plugin.getDuelManager().isFighting(player)) {
			player.sendMessage(ChatColor.RED + "You cannot use this command while fighting.");
			return true;
		}

		if (plugin.getServer().getPlayer(args[0]) == null && !plugin.getServer().getPlayer(args[0]).isOnline()) {
			player.sendMessage(ChatColor.RED + args[0] + " is not online.");
			return true;
		}

		Player target = plugin.getServer().getPlayer(args[0]);

		player.setGameMode(GameMode.SPECTATOR);
		player.setAllowFlight(true);
		player.teleport(target);
		player.sendMessage(ChatColor.AQUA + "You are now spectating " + target.getName() + ". Type /spectate to stop spectating.");

		return true;
	}
}
