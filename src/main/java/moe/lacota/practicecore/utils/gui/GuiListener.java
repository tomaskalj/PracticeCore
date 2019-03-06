package moe.lacota.practicecore.utils.gui;

import java.util.Optional;
import moe.lacota.practicecore.PracticePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiListener implements Listener {
	private PracticePlugin plugin;

	public GuiListener(PracticePlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory() == null) {
			return;
		}
		if (e.getCurrentItem() == null) {
			return;
		}

		Optional<GuiFolder> found = plugin.getFolders().stream().filter(folder -> folder.getInventory().getName().equalsIgnoreCase(e.getInventory().getName())).findFirst();
		if (!found.isPresent()) {
			return;
		}

		GuiFolder folder = found.get();
		GuiItem item = folder.getCurrentPage().getItem(e.getSlot());

		if (item == null) {
			return;
		}

		e.setCancelled(true);

		if (item instanceof GuiClickable) {
			((GuiClickable) item).onClick(e);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (e.getInventory() == null) {
			return;
		}

		Optional<GuiFolder> found = plugin.getFolders().stream().filter(folder -> folder.getInventory().getName().equalsIgnoreCase(e.getInventory().getName())).findFirst();
		if (!found.isPresent()) {
			return;
		}

		GuiFolder folder = found.get();
		plugin.getFolders().remove(folder);
	}
}
