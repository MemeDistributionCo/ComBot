package com.mdc.combot.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.mdc.combot.ComBot;
import com.mdc.combot.util.Util;

import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PluginAddCommand implements Command {

	@Override
	public String getLabel() {
		return "addplugin";
	}

	@Override
	public void called(ComBot bot, MessageReceivedEvent e) {
		if(bot.memberHasPerm("bot.plugins.add", e.getMember())) {
			List<Attachment> attachments = e.getMessage().getAttachments();
			final List<Attachment> toDownload = new ArrayList<Attachment>();
			for(Attachment a : attachments) {
				if(a.getFileName().endsWith(".jar")) {
					File newPluginFile = new File(Util.PLUGIN_DIR_PATH + File.separatorChar + a.getFileName());
					if(newPluginFile.exists()) {
						newPluginFile.delete();
					}
					toDownload.add(a);
				
				}
			}
			Thread t = new Thread() {
				@Override
				public void run() {
					Logger.getLogger("ComBot").info("User " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + " [" + e.getAuthor().getIdLong() + "] started uploading plugins");
					Logger.getLogger("ComBot").info("Starting plugin download, expected count: " + toDownload.size());
					for(Attachment a : toDownload) {
						File plFile = new File(Util.PLUGIN_DIR_PATH + File.separatorChar + a.getFileName());
						Logger.getLogger("ComBot").info("Downloading: " + a.getFileName());
						a.download(plFile);
						e.getTextChannel().sendMessage("Added " + a.getFileName() + " to plugins folder").queue();
						Logger.getLogger("ComBot").info("Donwload complete: " + a.getFileName());
					}
					Logger.getLogger("ComBot").info("Plugin download complete");
					e.getTextChannel().sendMessage("Plugin upload complete.").queue();
				}
			};
			t.start();
			if(toDownload.size() != 0) {
				e.getTextChannel().sendMessage("Plugin upload started").queue();
			} else {
				e.getTextChannel().sendMessage("Error, couldn't find any plugins to upload").queue();
			}
		}
	}

	
	
}
