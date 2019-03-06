package moe.lacota.practicecore.duels;

import java.util.Arrays;
import moe.lacota.practicecore.PracticePlugin;
import moe.lacota.practicecore.kit.DebuffConfiguration;
import moe.lacota.practicecore.kit.NoDebuffConfiguration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class DuelUtil {
	public static void applyByDuelType(Player player, DuelType type) {
		switch (type) {
			case DEBUFF:
				applyDebuff(player.getInventory(), PracticePlugin.getInstance().getPlayerManager().getPlayer(player).getDebuffConfiguration());
				break;
			case NO_DEBUFF:
				applyNoDebuff(player.getInventory(), PracticePlugin.getInstance().getPlayerManager().getPlayer(player).getNoDebuffConfiguration());
				break;
			default:
				break;
		}
	}

	public static void applyNoDebuff(PlayerInventory inv, NoDebuffConfiguration config) {
		inv.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		inv.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		inv.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		inv.setBoots(new ItemStack(Material.DIAMOND_BOOTS));

		Arrays.asList(inv.getHelmet(), inv.getChestplate(), inv.getLeggings(), inv.getBoots()).forEach(item -> {
			item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			item.addEnchantment(Enchantment.DURABILITY, 3);
		});

		inv.getBoots().addEnchantment(Enchantment.PROTECTION_FALL, 4);

		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
		sword.addEnchantment(Enchantment.DURABILITY, 3);

		inv.setItem(config.sword, sword);
		inv.setItem(config.enderPearl, new ItemStack(Material.ENDER_PEARL, 16));
		inv.setItem(config.carrots, new ItemStack(Material.GOLDEN_CARROT, 64));
		inv.setItem(config.fireRes, new Potion(PotionType.FIRE_RESISTANCE).extend().toItemStack(1));

		Arrays.asList(config.speedA, config.speedB, config.speedC, config.speedD).forEach(i -> inv.setItem(i, new Potion(PotionType.SPEED, 2).toItemStack(1)));

		for (int i = 0; i < 36; i++) {
			if (inv.getItem(i) == null) {
				inv.setItem(i, new Potion(PotionType.INSTANT_HEAL, 2).splash().toItemStack(1));
			}
		}
	}

	public static void applyDebuff(PlayerInventory inv, DebuffConfiguration config) {
		inv.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		inv.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		inv.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		inv.setBoots(new ItemStack(Material.DIAMOND_BOOTS));

		Arrays.asList(inv.getHelmet(), inv.getChestplate(), inv.getLeggings(), inv.getBoots()).forEach(item -> {
			item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
			item.addEnchantment(Enchantment.DURABILITY, 3);
		});

		inv.getBoots().addEnchantment(Enchantment.PROTECTION_FALL, 4);

		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
		sword.addEnchantment(Enchantment.DURABILITY, 3);

		inv.setItem(config.sword, sword);
		inv.setItem(config.enderPearl, new ItemStack(Material.ENDER_PEARL, 16));
		inv.setItem(config.carrots, new ItemStack(Material.GOLDEN_CARROT, 64));
		inv.setItem(config.fireRes, new Potion(PotionType.FIRE_RESISTANCE).extend().toItemStack(1));

		Arrays.asList(config.speedA, config.speedB, config.speedC, config.speedD).forEach(i -> inv.setItem(i, new Potion(PotionType.SPEED, 2).toItemStack(1)));

		Arrays.asList(config.slownessA, config.slownessB, config.slownessC).forEach(i -> inv.setItem(i, new Potion(PotionType.SLOWNESS).splash().toItemStack(1)));

		Arrays.asList(config.poisonA, config.poisonB, config.poisonC).forEach(i -> inv.setItem(i, new Potion(PotionType.POISON).splash().toItemStack(1)));

		for (int i = 0; i < 36; i++) {
			if (inv.getItem(i) == null) {
				inv.setItem(i, new Potion(PotionType.INSTANT_HEAL, 2).splash().toItemStack(1));
			}
		}
	}
}
