package moe.lacota.practicecore.utils.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface GuiClickable extends GuiItem {
	void onClick(InventoryClickEvent event);
}
