package com.mdc.combot.command;

import java.util.Map;

import com.mdc.combot.ComBot;
import com.mdc.combot.plugin.BotPlugin;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PluginInfoCommand implements Command {

	@Override
	public String getLabel() {
		return "plinfo";
	}

	@Override
	public void called(ComBot bot, MessageReceivedEvent e) {
		if(bot.memberHasPerm("bot.plugins.info", e.getMember())) {
			try {
				String pluginName = e.getMessage().getContentRaw().replace(bot.getCommandPrefix(e.getGuild())+getLabel() + " ", "").split(" ")[0];
				Map<BotPlugin,Map<String,String>> plMap = bot.getPluginMap();
				BotPlugin target = null;
				for(BotPlugin pl : plMap.keySet()) {
					String name;
					name = plMap.get(pl).get("name");
					if(name == null || name.equals("")) {
						name = plMap.get(pl).get("main").substring(plMap.get(pl).get("main").lastIndexOf(".")+1);
					}
					if(name.equalsIgnoreCase(pluginName)) {
						target = pl;
						break;
					}
				}
				if(target != null) {
					Map<String,String> pluginData = bot.getPluginMap().get(target);
					String info = pluginData.get("info");
					if(info == null) {
						e.getTextChannel().sendMessage(pluginName + " didn't include an information section.").queue();
					} else {
						e.getTextChannel().sendMessage(pluginName + ":\n" + info).queue();
					}
					String extraMsg = "";
					
					for(String tag : pluginData.keySet()) {
						if(tag.equalsIgnoreCase("main") || tag.equalsIgnoreCase("info")) continue;
						extraMsg+="**" + tag + "**: " + pluginData.get(tag) + "\n";
					}
					e.getTextChannel().sendMessage(extraMsg).queue();
					
				} else {
					e.getTextChannel().sendMessage("Couldn't find a plugin with that name.").queue();
				}
			} catch (Exception ex) {
				e.getTextChannel().sendMessage("Proper syntax: `" + bot.getCommandPrefix(e.getGuild()) + getLabel() + " <plugin name>").queue();
				return;
			}
		}
	}

}
