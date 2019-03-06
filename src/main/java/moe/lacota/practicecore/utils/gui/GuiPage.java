package moe.lacota.practicecore.utils.gui;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.entity.Player;

public class GuiPage {
	private GuiFolder folder;
	@Getter
	private Map<Integer, GuiItem> items;

	public GuiPage(GuiFolder folder) {
		this.folder = folder;
		this.items = new HashMap<>();
	}

	public void updatePage() {
		folder.getInventory().clear();
		items.forEach((slot, item) -> folder.getInventory().setItem(slot, item.getItemStack()));
		folder.getInventory().getViewers().forEach(viewer -> ((Player) viewer).updateInventory());
	}

	public void addItem(int slot, GuiItem item) {
		items.put(slot, item);
	}

	public GuiItem getItem(int slot) {
		return items.get(slot);
	}
}
