package moe.lacota.practicecore.kit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import moe.lacota.practicecore.PracticePlugin;
import moe.lacota.practicecore.duels.DuelType;
import moe.lacota.practicecore.duels.DuelUtil;
import moe.lacota.practicecore.players.PracticePlayer;
import moe.lacota.practicecore.utils.Cooldown;
import moe.lacota.practicecore.utils.ItemUtil;
import moe.lacota.practicecore.utils.Locations;
import moe.lacota.practicecore.utils.PlayerUtil;
import moe.lacota.practicecore.utils.gui.GuiClickable;
import moe.lacota.practicecore.utils.gui.GuiExit;
import moe.lacota.practicecore.utils.gui.GuiFiller;
import moe.lacota.practicecore.utils.gui.GuiFolder;
import moe.lacota.practicecore.utils.gui.GuiPage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 * This needs work.
 */
public class KitEditor implements Listener {
	private final File NO_DEBUFF_DIR;
	private final File DEBUFF_DIR;

	private final PracticePlugin plugin;
	private final Map<UUID, DuelType> editing = new HashMap<>();

	public KitEditor(PracticePlugin plugin) {
		this.plugin = plugin;

		File dir = new File("kits");
		if (!dir.exists()) {
			dir.mkdir();
		}

		NO_DEBUFF_DIR = new File(dir, "nodebuff");
		if (!NO_DEBUFF_DIR.exists()) {
			NO_DEBUFF_DIR.mkdir();
		}

		DEBUFF_DIR = new File(dir, "debuff");
		if (!DEBUFF_DIR.exists()) {
			DEBUFF_DIR.mkdir();
		}

		plugin.getServer().getPluginManager().registerEvents(this, PracticePlugin.getInstance());
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (!editing.containsKey(e.getPlayer().getUniqueId())) {
			return;
		}
		if (!e.hasBlock()) {
			return;
		}

		Player player = e.getPlayer();
		PracticePlayer pp = plugin.getPlayerManager().getPlayer(player);

		if (e.getClickedBlock().getType() == Material.ANVIL) {
			editing.remove(player.getUniqueId());
			PlayerUtil.reset(player);
			Cooldown.addCooldown(player.getName(), "NO_ACTION", 750);
			PlayerUtil.giveItems(player);
			player.teleport(Locations.SPAWN);
		} else if (e.getClickedBlock().getType() == Material.CHEST) {
			DuelType type = editing.remove(player.getUniqueId());
			if (type == DuelType.NO_DEBUFF) {
				NoDebuffConfiguration config = new NoDebuffConfiguration();
				config.sword = ItemUtil.getSlots(player.getInventory(), Material.DIAMOND_SWORD).get(0);
				config.enderPearl = ItemUtil.getSlots(player.getInventory(), Material.ENDER_PEARL).get(0);
				config.carrots = ItemUtil.getSlots(player.getInventory(), Material.GOLDEN_CARROT).get(0);
				config.fireRes = ItemUtil.getSlots(player.getInventory(), PotionType.FIRE_RESISTANCE).get(0);
				config.speedA = ItemUtil.getSlots(player.getInventory(), PotionType.SPEED).get(0);
				config.speedB = ItemUtil.getSlots(player.getInventory(), PotionType.SPEED).get(1);
				config.speedC = ItemUtil.getSlots(player.getInventory(), PotionType.SPEED).get(2);
				config.speedD = ItemUtil.getSlots(player.getInventory(), PotionType.SPEED).get(3);

				pp.setNoDebuffConfiguration(config);
			} else if (type == DuelType.DEBUFF) {
				DebuffConfiguration config = new DebuffConfiguration();
				config.sword = ItemUtil.getSlots(player.getInventory(), Material.DIAMOND_SWORD).get(0);
				config.enderPearl = ItemUtil.getSlots(player.getInventory(), Material.ENDER_PEARL).get(0);
				config.carrots = ItemUtil.getSlots(player.getInventory(), Material.GOLDEN_CARROT).get(0);
				config.fireRes = ItemUtil.getSlots(player.getInventory(), PotionType.FIRE_RESISTANCE).get(0);
				config.speedA = ItemUtil.getSlots(player.getInventory(), PotionType.SPEED).get(0);
				config.speedB = ItemUtil.getSlots(player.getInventory(), PotionType.SPEED).get(1);
				config.speedC = ItemUtil.getSlots(player.getInventory(), PotionType.SPEED).get(2);
				config.speedD = ItemUtil.getSlots(player.getInventory(), PotionType.SPEED).get(3);
				config.slownessA = ItemUtil.getSlots(player.getInventory(), PotionType.SLOWNESS).get(0);
				config.slownessB = ItemUtil.getSlots(player.getInventory(), PotionType.SLOWNESS).get(1);
				config.slownessC = ItemUtil.getSlots(player.getInventory(), PotionType.SLOWNESS).get(2);
				config.poisonA = ItemUtil.getSlots(player.getInventory(), PotionType.POISON).get(0);
				config.poisonB = ItemUtil.getSlots(player.getInventory(), PotionType.POISON).get(1);
				config.poisonC = ItemUtil.getSlots(player.getInventory(), PotionType.POISON).get(2);

				pp.setDebuffConfiguration(config);
			}

			player.sendMessage(ChatColor.GREEN + "Successfully edited your " + ChatColor.LIGHT_PURPLE + type.getName() + ChatColor.GREEN + " kit.");

			PlayerUtil.reset(player);
			Cooldown.addCooldown(player.getName(), "NO_ACTION", 2 * 1000);
			PlayerUtil.giveItems(player);
			player.teleport(Locations.SPAWN);
		}
	}

	public void setEditing(Player player, DuelType type) {
		editing.put(player.getUniqueId(), type);
	}

	public boolean isEditing(Player player) {
		return editing.containsKey(player.getUniqueId());
	}

	public void openInventory(Player player) {
		GuiFolder folder = new GuiFolder(ChatColor.GREEN + "Kit Editor", 9);
		GuiPage page = new GuiPage(folder);

		page.addItem(0, new GuiClickable() {
			@Override
			public ItemStack getItemStack() {
				return ItemUtil.createItem(new Potion(PotionType.INSTANT_HEAL, 2).splash().toItemStack(1), ChatColor.GREEN + "NoDebuff");
			}

			@Override
			public void onClick(InventoryClickEvent event) {
				KitEditor.this.onClick(player, DuelType.NO_DEBUFF);
			}
		});

		page.addItem(1, new GuiClickable() {
			@Override
			public ItemStack getItemStack() {
				return ItemUtil.createItem(new Potion(PotionType.POISON).splash().toItemStack(1), ChatColor.GREEN + "Debuff");
			}

			@Override
			public void onClick(InventoryClickEvent event) {
				KitEditor.this.onClick(player, DuelType.DEBUFF);
			}
		});

		page.addItem(8, new GuiExit());

		for (int i = 0; i < folder.getSize(); i++) {
			if (page.getItem(i) == null) {
				page.addItem(i, new GuiFiller());
			}
		}

		folder.setCurrentPage(page);
		folder.openGui(player);
	}

	private void onClick(Player player, DuelType type) {
		player.teleport(Locations.KIT_EDITOR);
		player.sendMessage(ChatColor.GREEN + "You are now editing the " + ChatColor.LIGHT_PURPLE + type.getName() + ChatColor.GREEN + " kit.");
		player.sendMessage(ChatColor.GREEN + "When you are done editing your kit, click the chest to save. Otherwise, click the anvil to return to spawn.");
		PlayerUtil.reset(player);
		DuelUtil.applyByDuelType(player, type);

		plugin.getKitEditor().setEditing(player, type);
	}

	public boolean load(PracticePlayer player) {
		System.out.println("Loading kit configurations for " + player.getUniqueId() + "...");

		File file;
		DataInputStream in;

		try {
			file = new File(NO_DEBUFF_DIR, player.getUniqueId().toString());
			in = new DataInputStream(new FileInputStream(file));

			NoDebuffConfiguration noDebuffConfig = new NoDebuffConfiguration();
			noDebuffConfig.sword = in.readInt();
			noDebuffConfig.enderPearl = in.readInt();
			noDebuffConfig.carrots = in.readInt();
			noDebuffConfig.fireRes = in.readInt();
			noDebuffConfig.speedA = in.readInt();
			noDebuffConfig.speedB = in.readInt();
			noDebuffConfig.speedC = in.readInt();
			noDebuffConfig.speedD = in.readInt();

			player.setNoDebuffConfiguration(noDebuffConfig);

			in.close();

			file = new File(DEBUFF_DIR, player.getUniqueId().toString());
			in = new DataInputStream(new FileInputStream(file));

			DebuffConfiguration debuffConfig = new DebuffConfiguration();
			debuffConfig.sword = in.readInt();
			debuffConfig.enderPearl = in.readInt();
			debuffConfig.carrots = in.readInt();
			debuffConfig.fireRes = in.readInt();
			debuffConfig.speedA = in.readInt();
			debuffConfig.speedB = in.readInt();
			debuffConfig.speedC = in.readInt();
			debuffConfig.speedD = in.readInt();
			debuffConfig.slownessA = in.readInt();
			debuffConfig.slownessB = in.readInt();
			debuffConfig.slownessC = in.readInt();
			debuffConfig.poisonA = in.readInt();
			debuffConfig.poisonB = in.readInt();
			debuffConfig.poisonC = in.readInt();

			player.setDebuffConfiguration(debuffConfig);

			in.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean save(PracticePlayer player) {
		System.out.println("Saving kit configurations for " + player.getUniqueId() + "...");

		File file;
		DataOutputStream out;

		try {
			file = new File(NO_DEBUFF_DIR, player.getUniqueId().toString());
			out = new DataOutputStream(new FileOutputStream(file));

			NoDebuffConfiguration noDebuffConfig = player.getNoDebuffConfiguration();
			out.writeInt(noDebuffConfig.sword);
			out.writeInt(noDebuffConfig.enderPearl);
			out.writeInt(noDebuffConfig.carrots);
			out.writeInt(noDebuffConfig.fireRes);
			out.writeInt(noDebuffConfig.speedA);
			out.writeInt(noDebuffConfig.speedB);
			out.writeInt(noDebuffConfig.speedC);
			out.writeInt(noDebuffConfig.speedD);

			out.flush();
			out.close();

			file = new File(DEBUFF_DIR, player.getUniqueId().toString());
			out = new DataOutputStream(new FileOutputStream(file));

			DebuffConfiguration debuffConfig = player.getDebuffConfiguration();
			out.writeInt(debuffConfig.sword);
			out.writeInt(debuffConfig.enderPearl);
			out.writeInt(debuffConfig.carrots);
			out.writeInt(debuffConfig.fireRes);
			out.writeInt(debuffConfig.speedA);
			out.writeInt(debuffConfig.speedB);
			out.writeInt(debuffConfig.speedC);
			out.writeInt(debuffConfig.speedD);
			out.writeInt(debuffConfig.slownessA);
			out.writeInt(debuffConfig.slownessB);
			out.writeInt(debuffConfig.slownessC);
			out.writeInt(debuffConfig.poisonA);
			out.writeInt(debuffConfig.poisonB);
			out.writeInt(debuffConfig.poisonC);

			out.flush();
			out.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}