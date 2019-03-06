package moe.lacota.practicecore.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class JsonChat {
	public static void sendMessage(Player player, String msg, String hoverMsg) {
		TextComponent message = new TextComponent(msg);
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMsg).create()));
		player.spigot().sendMessage(message);
	}

	public static void sendClickableMessage(Player player, String msg, String hoverMsg, String clickCommand) {
		TextComponent message = new TextComponent(msg);
		message.setClickEvent(new ClickEvent(Action.RUN_COMMAND, clickCommand));
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMsg).create()));
		player.spigot().sendMessage(message);
	}
}
