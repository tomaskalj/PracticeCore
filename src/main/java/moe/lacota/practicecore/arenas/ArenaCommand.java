package moe.lacota.practicecore.arenas;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import moe.lacota.practicecore.utils.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {
	private ArenaManager arenaManager;
	private Map<UUID, Arena> creating;

	public ArenaCommand(ArenaManager arenaManager) {
		this.arenaManager = arenaManager;
		this.creating = new HashMap<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command c, String l, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command.");
			return true;
		}

		Player player = (Player) sender;

		if (!player.hasPermission("practice.admin")) {
			return true;
		}

		if (args.length == 0) {
			player.sendMessage(ChatColor.GREEN + "Arena Help Index");
			player.sendMessage(ChatColor.GREEN + "/arena create <name> " + ChatColor.GRAY + "- Create an arena.");
			player.sendMessage(ChatColor.GREEN + "/arena setspawn <a / b>" + ChatColor.GRAY + "- Set a spawn point (either a or b)");
			player.sendMessage(ChatColor.GREEN + "/arena remove <name> " + ChatColor.GRAY + "- Create an arena.");
			player.sendMessage(ChatColor.GREEN + "/arena list " + ChatColor.GRAY + "- List all the existing arenas.");
			return true;
		}

		if (args[0].equalsIgnoreCase("create")) {
			if (args.length != 2) {
				player.sendMessage(ChatColor.RED + "Please specify a name.");
				return true;
			}

			Arena arena = new Arena(args[1], null, null);
			creating.put(player.getUniqueId(), arena);

			player.sendMessage(ChatColor.GREEN + "Created arena " + arena.getName() + ". Use /arena setspawn to set the spawns now.");
		} else if (args[0].equalsIgnoreCase("setspawn")) {
			if (!creating.containsKey(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You are not currently creating an arena.");
				return true;
			}

			if (args.length != 2) {
				player.sendMessage(ChatColor.RED + "Please specify whether you would like to set spawn point A or B.");
				return true;
			}

			Arena arena = creating.get(player.getUniqueId());

			if (args[1].equalsIgnoreCase("A")) {
				arena.setSpawnPointA(player.getLocation());
				player.sendMessage(ChatColor.GREEN + "Set spawn point A.");
			} else if (args[1].equalsIgnoreCase("B")) {
				arena.setSpawnPointB(player.getLocation());
				player.sendMessage(ChatColor.GREEN + "Set spawn point B.");
			} else {
				player.sendMessage(ChatColor.RED + args[1] + " is not a valid spawn point type. Valid types are: A, B");
			}

			if (arena.getSpawnPointA() != null && arena.getSpawnPointB() != null) {
				creating.remove(player.getUniqueId());
				player.sendMessage(ChatColor.GREEN + "Successfully created " + arena.getName() + ".");
				arenaManager.addArena(arena);
			}
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (args.length != 2) {
				player.sendMessage(ChatColor.RED + "Please specify an existing arena.");
				return true;
			}

			Arena arena = arenaManager.getByName(args[1]);

			if (arena == null) {
				player.sendMessage(ChatColor.RED + args[1] + " doesn't exist.");
				return true;
			}

			File file = arenaManager.getFile(arena);

			if (!file.exists()) {
				player.sendMessage("An error occurred while deleting " + args[1] + ".");
				return true;
			}

			arenaManager.getArenas().remove(arena);
			file.delete();
			player.sendMessage(ChatColor.YELLOW + arena.getName() + " has been removed.");
		} else if (args[0].equalsIgnoreCase("list")) {
			List<Arena> arenas = new ArrayList<>();
			arenas.addAll(arenaManager.getArenas());
			arenas.addAll(creating.values());

			player.sendMessage(ChatColor.YELLOW + "=== Arenas ===");

			arenas.forEach(arena -> {
				String name = ChatColor.GREEN + arena.getName();
				String spawnPointA = arena.getSpawnPointA() != null ? ChatColor.GREEN + LocationUtil.simpleLocationToString(arena.getSpawnPointA()) : ChatColor.RED + "Not Set";
				String spawnPointB = arena.getSpawnPointB() != null ? ChatColor.GREEN + LocationUtil.simpleLocationToString(arena.getSpawnPointB()) : ChatColor.RED + "Not Set";
				String message = name + ChatColor.GRAY + " : " + spawnPointA + ChatColor.GRAY + " : " + spawnPointB;
				player.sendMessage(message);
			});
		}

		return true;
	}
}
