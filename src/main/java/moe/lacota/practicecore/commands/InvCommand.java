package moe.lacota.practicecore.commands;

import lombok.RequiredArgsConstructor;
import moe.lacota.practicecore.PracticePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@RequiredArgsConstructor
public class InvCommand implements CommandExecutor {
	private final PracticePlugin plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command c, String l, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command.");
			return true;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Please use /inv <player>");
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
		Inventory inv = plugin.getDuelManager().getInventory(target);

		if (inv == null) {
			System.out.println(target.getName() + "'s inventory is null");
			return true;
		}

		player.openInventory(inv);

		return true;
	}
}
