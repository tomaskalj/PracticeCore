package moe.lacota.practicecore.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import moe.lacota.practicecore.PracticePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerUtil {
	private static final double DEFAULT_HEALTH = 20;

	public static void reset(Player player) {
		player.setMaxHealth(DEFAULT_HEALTH);
		player.setHealth(DEFAULT_HEALTH);
		player.setFoodLevel(20);
		player.setSaturation(1f);
		player.setFireTicks(0);
		player.setExhaustion(0);
		player.setLevel(0);
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.setGameMode(GameMode.SURVIVAL);

		player.closeInventory();

		if (player.getVehicle() != null) {
			player.getVehicle().eject();
		}

		player.getActivePotionEffects().forEach(effect -> player.addPotionEffect(new PotionEffect(effect.getType(), 0, 0), true));

		Iterator<Entry<Object, Map<Object, Long>>> it = Cooldown.getMap().entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, Map<Object, Long>> set = it.next();

			if (set.getKey().equals(player.getName())) {
				it.remove();
			}
		}
	}

	public static void giveItems(Player player) {
		if (PracticePlugin.getInstance().getPartyManager().getTeam(player) != null) {
			PracticePlugin.getInstance().getPartyManager().equipTeamItems(player);
			return;
		}

		player.getInventory().setItem(0, ItemUtil.createItem(Material.BOOK, ChatColor.GREEN + "Edit Kits", (byte) 0));
		player.getInventory().setItem(7, ItemUtil.createItem(Material.IRON_SWORD, ChatColor.GREEN + "Unranked Queues", (byte) 0));
		player.getInventory().setItem(8, ItemUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "Ranked Queues", (byte) 0));
	}

	public static String fetchName(UUID uuid) {
		String name = null;
		Player player = Bukkit.getPlayer(uuid);

		if (player != null && player.isOnline()) {
			name = player.getName();
		}

		if (name == null) {
			try {
				URL url = new URL("https://mcapi.ca/name/uuid/" + uuid.toString().replaceAll("-", ""));
				HttpURLConnection request = (HttpURLConnection) url.openConnection();
				request.connect();

				JsonParser jsonParser = new JsonParser();
				JsonElement jsonElement = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));
				JsonObject jsonObject = jsonElement.getAsJsonObject();

				if (!jsonObject.isJsonNull() && !jsonObject.get("name").isJsonNull()) {
					name = jsonObject.get("name").getAsString();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return name;
	}

	public static void hideFromAllExcept(Player player, Collection<Player> exempt) {
		PracticePlugin.getInstance().getServer().getOnlinePlayers().forEach(pl -> {
			if (player.canSee(pl)) {
				if (!exempt.contains(pl)) {
					player.hidePlayer(pl);
				}
			}
		});
	}
}
