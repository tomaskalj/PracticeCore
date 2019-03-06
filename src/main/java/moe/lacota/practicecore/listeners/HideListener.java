package moe.lacota.practicecore.listeners;

import com.comphenix.EntityHider;
import com.comphenix.EntityHider.Policy;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import moe.lacota.practicecore.PracticePlugin;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Hide all of the things players aren't supposed to see. :S
 *
 * @author Lacota
 */
public class HideListener implements Listener {
	private final Set<UUID> hidden = new HashSet<>();
	private final EntityHider entityHider;

	public HideListener() {
		entityHider = new EntityHider(PracticePlugin.getInstance(), Policy.BLACKLIST);

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PracticePlugin.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.WORLD_EVENT, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			@Override
			public void onPacketSending(PacketEvent e) {
				if (e.getPacketType() == PacketType.Play.Server.WORLD_EVENT) {
					Player player = e.getPlayer();

					if (hidden.contains(player.getUniqueId())) {
//						System.out.println("Hid potion for " + player.getName());

						e.setCancelled(true);
						hidden.remove(player.getUniqueId());
					}
				} else if (e.getPacketType() == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
					Player player = e.getPlayer();

					String name = e.getPacket().getStrings().read(0);

					if (name.equalsIgnoreCase("game.player.hurt") || name.equalsIgnoreCase("random.bow")
							|| name.equalsIgnoreCase("random.bowhit") || name.equalsIgnoreCase("random.pop")
							|| name.equalsIgnoreCase("random.drink") || name.equalsIgnoreCase("random.burp")
							|| name.equalsIgnoreCase("random.eat") || name.startsWith("step.")) {
						if (hidden.contains(player.getUniqueId())) {
//							System.out.println("Canceled sound effect " + name + " to " + player.getName());

							e.setCancelled(true);
							hidden.remove(player.getUniqueId());
						}
					}
				}
			}
		});
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (player.getFoodLevel() >= 20) {
			return;
		}
		if (e.getItem() == null) {
			return;
		}
		if (e.getItem().getType() == Material.AIR) {
			return;
		}
		if (e.getItem().getType() != Material.GOLDEN_CARROT) {
			return;
		}

		player.getWorld().getPlayers().stream()
				.filter(other -> other.getLocation().distanceSquared(player.getLocation()) <= 2500)
				.filter(other -> !player.canSee(other))
				.forEach(other -> hidden.add(other.getUniqueId()));
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();

		player.getWorld().getPlayers().stream()
				.filter(other -> other.getLocation().distanceSquared(player.getLocation()) <= 2500)
				.filter(other -> !player.canSee(other))
				.forEach(other -> hidden.add(other.getUniqueId()));
	}

	@EventHandler
	public void onItemConsume(PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();

		player.getWorld().getPlayers().stream()
				.filter(other -> other.getLocation().distanceSquared(player.getLocation()) <= 2500)
				.filter(other -> !player.canSee(other))
				.forEach(other -> hidden.add(other.getUniqueId()));
	}

	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent e) {
		Player player = e.getPlayer();

		player.getWorld().getPlayers().stream()
				.filter(other -> other.getLocation().distanceSquared(player.getLocation()) <= 2500)
				.filter(other -> !player.canSee(other))
				.forEach(other -> hidden.add(other.getUniqueId()));
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		Entity item = e.getItemDrop();

		player.getWorld().getPlayers().stream()
				.filter(other -> !player.canSee(other))
				.forEach(other -> entityHider.hideEntity(other, item));
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player player = (Player) e.getEntity().getShooter();
		Entity proj = e.getEntity();

		player.getWorld().getPlayers().stream()
				.filter(other -> !player.canSee(other))
				.forEach(other -> entityHider.hideEntity(other, proj));
	}

	@EventHandler
	public void onPotionSplash(PotionSplashEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player shooter = (Player) e.getEntity().getShooter();

		shooter.getWorld().getPlayers().stream()
				.filter(other -> other.getLocation().distanceSquared(shooter.getLocation()) <= 2500)
				.filter(other -> !shooter.canSee(other))
				.forEach(other -> hidden.add(other.getUniqueId()));
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}

		if (e.getDamager() instanceof Player) {
			Player player = (Player) e.getEntity();

			player.getWorld().getPlayers().stream()
					.filter(other -> !other.equals(player))
					.filter(other -> !other.equals(e.getDamager()))
					.filter(other -> other.getLocation().distanceSquared(player.getLocation()) <= 2500)
					.filter(other -> !player.canSee(other))
					.forEach(other -> hidden.add(other.getUniqueId()));
		} else if (e.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) e.getDamager();

			if (!(proj instanceof EnderPearl)) {
				return;
			}

			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}

		e.getEntity().getWorld().getPlayers().stream()
				.filter(other -> !other.equals(e.getEntity().getShooter()))
				.filter(other -> other.getLocation().distanceSquared(e.getEntity().getLocation()) <= 2500)
				.filter(other -> !((Player) e.getEntity().getShooter()).canSee(other))
				.forEach(other -> hidden.add(other.getUniqueId()));
	}
}
