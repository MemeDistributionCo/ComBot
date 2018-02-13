# ComBot
A discord bot which allows the use of external plugins to be installed.

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
