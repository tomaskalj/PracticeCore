package moe.lacota.practicecore.listeners;

import lombok.RequiredArgsConstructor;
import moe.lacota.practicecore.PracticePlugin;
import moe.lacota.practicecore.players.PracticePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class DeathListener implements Listener {
	private final PracticePlugin plugin;

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		e.setDroppedExp(0);
		e.getDrops().clear();
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}

		if (e.getCause() == DamageCause.ENTITY_ATTACK || e.getCause() == DamageCause.PROJECTILE || e.getCause() == DamageCause.ENTITY_EXPLOSION) {
			return;
		}

		Player player = (Player) e.getEntity();
		PracticePlayer pp = plugin.getPlayerManager().getPlayer(player);

		if (pp == null) {
			e.setCancelled(true);
			return;
		}

		if (player.getHealth() - e.getFinalDamage() <= 0) {
			plugin.getDuelManager().onDeath(player);

			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) e.getEntity();
		PracticePlayer pp = plugin.getPlayerManager().getPlayer(player);

		if (pp == null) {
			e.setCancelled(true);
			return;
		}

		if (player.getHealth() - e.getFinalDamage() <= 0) {
			plugin.getDuelManager().onDeath(player);

			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		plugin.getDuelManager().onDeath(player);
	}
}
