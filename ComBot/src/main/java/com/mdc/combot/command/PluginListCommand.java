package com.mdc.combot.command;

import java.awt.Color;
import java.util.Map;

import com.mdc.combot.ComBot;
import com.mdc.combot.plugin.BotPlugin;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PluginListCommand implements Command {

	@Override
	public String getLabel() {
		return "plugins";
	}

	@Override
	public void called(ComBot bot, MessageReceivedEvent e) {
		if(bot.memberHasPerm("bot.plugins.list", e.getMember())) {
			Map<BotPlugin, Map<String,String>> plugins = bot.getPluginMap();
			EmbedBuilder eb = new EmbedBuilder().setTitle("ComBot Installed Plugins List").setColor(Color.red);
			for(BotPlugin pl : plugins.keySet()) {
				String name, version, author;
				if(plugins.get(pl).get("name") == null || plugins.get(pl).get("name").equals("")) {
					name = plugins.get(pl).get("main").substring(plugins.get(pl).get("main").lastIndexOf('.')+1);
				} else {
					name = plugins.get(pl).get("name");
				}
				
				version = plugins.get(pl).get("version");
				author = plugins.get(pl).get("author");
				
				if(version == null || author.equals("")) version = "Unspecified Version";
				if(author == null || version.equals("")) author = "Unkown Author";
				//String toAdd = name + " version " + version + " by " + author;
				//toAdd = toAdd.replace("\n","").replace("\t", "");
				eb.addField("Plugin", name, true);
				eb.addField("Version", version, true);
				eb.addField("Author", author, true);
				eb.addBlankField(false);
			}
			e.getTextChannel().sendMessage(eb.build()).queue();
			
		}
	}

}
