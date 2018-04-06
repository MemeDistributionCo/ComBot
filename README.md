# ComBot
A discord bot which allows the use of external plugins. This bot uses the [JDA](https://github.com/DV8FromTheWorld/JDA) wrapper created by [DV8FromTheWorld](https://github.com/DV8FromTheWorld)

![Combot-image](https://memedistributionco.github.io/img/combot.png)

## Plugin Downloads

[ComBot Website](https://memedistributionco.github.io/combot/)

## Bot Discord
[For more help or info, join the discord](https://discord.gg/kgpguGh)

## Download
Get the launcher [here](https://github.com/MemeDistributionCo/ComBot/releases) and read about setup help [here](https://github.com/MemeDistributionCo/ComBot/wiki)

## Features:
- Choose what features you want added to your bot through the use of user created Plugins.
- Users can create and distribute plugins for public use
- Built in permission system which binds permissions to users and roles. Replaceable by other plugins.
- Restart and Shutdown bot from Discord
- Add plugins through discord
- Configuration file
- Auto Updating: The bot updates itself

## Permissions?
[Read about permissions here](https://github.com/MemeDistributionCo/ComBot/wiki/Permissions)

## Commands
- `~restart`
- `~shutdown`
- `~plugins`
- `~plinfo`
- `~addplugin`

## Docs
[Docs v1.3.2](https://memedistributionco.github.io/docs/ComBot/v1.3.2/index.html)

## How to add a plugin:

Navigate to your `~/ComBot/plugins/` folder and add place any plugins you want there. Restarting the bot will enable of the plugins in the folder.

## How to make a plugin

You can either look at the example below, or look at the [wiki pages](https://github.com/MemeDistributionCo/ComBot/wiki)

A simple plugin example:
```Java
package my.pack.age;

import com.mdc.combot.ComBot;
import com.mdc.combot.command.Command;
import com.mdc.combot.plugin.BotPlugin;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class TestVersionPlugin implements BotPlugin {

	@Override
	public void disable() {
  
	}

	@Override
	public void enable() {
		command = new Command() {

			@Override
			public void called(ComBot bot, MessageReceivedEvent e) {
				e.getTextChannel().sendMessage("Bot version: " + bot.getVersion() + "!").complete();
			}

			@Override
			public String getLabel() {
				return "version";
			}
			
		};
		ComBot.getBot().registerCommand(command);
		System.out.println("Version Plugin enabled!");
	}

}
```

plugin.txt:
```
main:my.pack.age.TestVersionPlugin
```

Project directory:
```
VersionPlugin:
  src:
    my:
      pack:
        age:
          TestVersionPlugin.java
  plugin.txt
  ReferencedLibraries:
    ComBot.vx.x.x.jar
```

### Note
Plugin creators are responsible for properly enabling and disabling their plugin.
