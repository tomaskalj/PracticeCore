package moe.lacota.practicecore.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class ItemUtil {
	@Getter
	private static Map<Item, UUID> droppedItems = new HashMap<>();

	public static List<Integer> getSlots(Inventory inv, Material type) {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i).getType() == type) {
				list.add(i);
			}
		}
		return list;
	}

	public static List<Integer> getSlots(Inventory inv, PotionType type) {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i).getType() != Material.POTION) {
				continue;
			}

			Potion potion = Potion.fromItemStack(inv.getItem(i));
			if (potion == null) {
				continue;
			}

			if (potion.getType() == type) {
				list.add(i);
			}
		}
		return list;
	}

	public static String getName(ItemStack item) {
		if (item == null) {
			return "";
		}
		if (item.getItemMeta() == null) {
			return "";
		}
		if (item.getItemMeta().getDisplayName() == null) {
			return "";
		}

		return item.getItemMeta().getDisplayName();
	}

	public static ItemStack createItem(ItemStack item, String displayName, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack createItem(ItemStack item, String displayName) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack createItem(Material item, String displayName, List<String> lore) {
		return createItem(new ItemStack(item), displayName, lore);
	}

	public static ItemStack createItem(Material type, String displayName, short data) {
		ItemStack item = new ItemStack(type, 1, data);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		return item;
	}
}
