package moe.lacota.practicecore.utils.gui;

import moe.lacota.practicecore.utils.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiExit implements GuiClickable {
	@Override
	public ItemStack getItemStack() {
		return ItemUtil.createItem(Material.REDSTONE_BLOCK, ChatColor.RED + "Close Inventory", (byte) 0);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		event.getWhoClicked().closeInventory();
	}
}