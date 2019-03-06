package moe.lacota.practicecore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import moe.lacota.practicecore.arenas.ArenaCommand;
import moe.lacota.practicecore.arenas.ArenaManager;
import moe.lacota.practicecore.commands.AcceptCommand;
import moe.lacota.practicecore.commands.DuelCommand;
import moe.lacota.practicecore.commands.EloCommand;
import moe.lacota.practicecore.commands.HelpCommand;
import moe.lacota.practicecore.commands.InvCommand;
import moe.lacota.practicecore.commands.SpectateCommand;
import moe.lacota.practicecore.duels.DuelManager;
import moe.lacota.practicecore.elo.EloRatingSystem;
import moe.lacota.practicecore.elo.KFactor;
import moe.lacota.practicecore.kit.KitEditor;
import moe.lacota.practicecore.listeners.DeathListener;
import moe.lacota.practicecore.listeners.HideListener;
import moe.lacota.practicecore.listeners.PlayerListener;
import moe.lacota.practicecore.listeners.WorldListener;
import moe.lacota.practicecore.party.PartyCommand;
import moe.lacota.practicecore.party.PartyManager;
import moe.lacota.practicecore.players.PracticePlayer;
import moe.lacota.practicecore.players.PracticePlayerManager;
import moe.lacota.practicecore.queue.QueueSystem;
import moe.lacota.practicecore.utils.ItemUtil;
import moe.lacota.practicecore.utils.gui.GuiFolder;
import moe.lacota.practicecore.utils.gui.GuiListener;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PracticeCore Plugin for Nexus.
 *
 * @author Lacota
 */
@Getter
public class PracticePlugin extends JavaPlugin {
	@Getter
	private static PracticePlugin instance;

	private PracticePlayerManager playerManager;
	private DuelManager duelManager;
	private PartyManager partyManager;
	private KitEditor kitEditor;
	private ArenaManager arenaManager;
	private QueueSystem queueSystem;
	private EloRatingSystem eloRatingSystem;

	private List<GuiFolder> folders;

	@Override
	public void onEnable() {
		instance = this;

		playerManager = new PracticePlayerManager();
		duelManager = new DuelManager(this);
		partyManager = new PartyManager(this);
		kitEditor = new KitEditor(this);
		arenaManager = new ArenaManager();
		queueSystem = new QueueSystem(this);
		eloRatingSystem = new EloRatingSystem(new KFactor(0, 1200, 25), new KFactor(1201, 1600, 20), new KFactor(1601, 2000, 15), new KFactor(2001, 2500, 10));

		folders = new ArrayList<>();

		File playerDir = new File("players");
		if (!playerDir.exists()) {
			playerDir.mkdir();
		}

		getServer().getPluginManager().registerEvents(new DeathListener(this), this);
		getServer().getPluginManager().registerEvents(new HideListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new WorldListener(), this);
		getServer().getPluginManager().registerEvents(new GuiListener(this), this);

		getServer().getPluginManager().registerEvents(new DuelManager(this), this);

		getCommand("spectate").setExecutor(new SpectateCommand(this));
		getCommand("party").setExecutor(new PartyCommand(this));
		getCommand("duel").setExecutor(new DuelCommand(this));
		getCommand("accept").setExecutor(new AcceptCommand(this));
		getCommand("inv").setExecutor(new InvCommand(this));
		getCommand("arena").setExecutor(new ArenaCommand(arenaManager));
		getCommand("elo").setExecutor(new EloCommand(this));
		getCommand("help").setExecutor(new HelpCommand());

		getServer().getOnlinePlayers().forEach(player -> playerManager.addPlayer(player.getUniqueId()));
	}

	@Override
	public void onDisable() {
		ItemUtil.getDroppedItems().keySet().forEach(Item::remove);
		ItemUtil.getDroppedItems().clear();

		playerManager.getPlayers().values().forEach(PracticePlayer::save);
		playerManager.getPlayers().clear();
	}
}
