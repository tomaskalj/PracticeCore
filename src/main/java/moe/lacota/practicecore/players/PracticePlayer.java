package moe.lacota.practicecore.players;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import moe.lacota.practicecore.PracticePlugin;
import moe.lacota.practicecore.duels.DuelType;
import moe.lacota.practicecore.kit.DebuffConfiguration;
import moe.lacota.practicecore.kit.NoDebuffConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public class PracticePlayer {
	private UUID uuid;
	private int noDebuffElo;
	private int debuffElo;

	@Setter
	private NoDebuffConfiguration noDebuffConfiguration;
	@Setter
	private DebuffConfiguration debuffConfiguration;

	private final File PLAYER_FILE;

	public PracticePlayer(UUID uuid) {
		this.uuid = uuid;
		this.noDebuffElo = 1000;
		this.debuffElo = 1000;
		this.noDebuffConfiguration = new NoDebuffConfiguration();
		this.debuffConfiguration = new DebuffConfiguration();

		PLAYER_FILE = new File("players", uuid.toString());
		try {
			PLAYER_FILE.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean save() {
		System.out.println("Saving PracticePlayer " + uuid + "...");

		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(PLAYER_FILE));

			out.writeByte(0);
			out.writeInt(noDebuffElo);
			out.writeInt(debuffElo);

			out.flush();
			out.close();

			return PracticePlugin.getInstance().getKitEditor().save(this);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean load() {
		System.out.println("Loading PracticePlayer " + uuid + "...");

		try {
			DataInputStream in = new DataInputStream(new FileInputStream(PLAYER_FILE));

			if (in.readByte() != 0) {
				in.close();
				throw new InvalidParameterException("Invalid version!");
			}

			noDebuffElo = in.readInt();
			debuffElo = in.readInt();

			in.close();

			return PracticePlugin.getInstance().getKitEditor().load(this);
		} catch (Exception e) {
			save();
			e.printStackTrace();
			return false;
		}
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public int getEloByDuelType(DuelType type) {
		switch (type) {
			case DEBUFF:
				return debuffElo;
			case NO_DEBUFF:
				return noDebuffElo;
			default:
				return 0;
		}
	}

	public void setEloByDuelType(DuelType type, int newElo) {
		switch (type) {
			case DEBUFF:
				debuffElo = newElo;
				break;
			case NO_DEBUFF:
				noDebuffElo = newElo;
				break;
			default:
				break;
		}
	}
}
