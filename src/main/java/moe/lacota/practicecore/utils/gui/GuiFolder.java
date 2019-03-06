package moe.lacota.practicecore.utils.gui;

import lombok.Getter;
import moe.lacota.practicecore.PracticePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Getter
public class GuiFolder {
	private String name;
	private int size;
	private Inventory inventory;
	private GuiPage currentPage;

	public GuiFolder(String name, int size) {
		this.name = name;
		this.size = size;
		this.inventory = Bukkit.createInventory(null, size, name);

		PracticePlugin.getInstance().getFolders().add(this);
	}

	public void openGui(Player player) {
		player.closeInventory();
		player.openInventory(inventory);
	}

	public void setCurrentPage(GuiPage currentPage) {
		this.currentPage = currentPage;
		this.currentPage.updatePage();
	}
}
