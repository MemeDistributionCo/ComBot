package com.mdc.combot.command;

import java.util.Map;

import com.mdc.combot.ComBot;
import com.mdc.combot.plugin.BotPlugin;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PluginListCommand implements Command {

	@Override
	public String getLabel() {
		return "plugins";
	}

	@Override
	public void called(ComBot bot, MessageReceivedEvent e) {
		if (bot.memberHasPerm("bot.plugins.list", e.getMember())) {
			Map<BotPlugin, Map<String, String>> plMap = bot.getPluginMap();
			String finMsg = "Simple Plugin List: \n";
			int counter = 1;
			for (Map<String, String> infoMap : plMap.values()) {
				if (infoMap.get("name") != null) {
					finMsg += "**" + counter + "**. " + infoMap.get("name") + "\n";
				} else {
					finMsg += "**" + counter + "**. " + infoMap.get("main").substring(infoMap.get("main").lastIndexOf('.') + 1)
							+ "\n";
				}
				counter++;
			}
			e.getTextChannel().sendMessage(finMsg).queue();
		}
	}

}
