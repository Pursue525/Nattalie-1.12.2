package net.pursue.command;

import net.pursue.command.commands.*;
import net.pursue.event.EventManager;
import net.pursue.event.EventTarget;
import net.pursue.event.world.EventChat;
import net.pursue.utils.client.DebugHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager {

    private final List<Command> commands = new ArrayList<>();

    public void init() {
        commands.add(new Binds());
        commands.add(new Bind());
        commands.add(new Toggle());
        commands.add(new Config());
        commands.add(new AutoL());
        EventManager.instance.register(this);
    }

    private Command getCommandByName(String name) {
        return commands.stream()
                .filter(cmd -> cmd.getName().toLowerCase().equals(name))
                .findFirst()
                .orElse(null);
    }

    private List<String> getCommandSuggestions(String input) {
        return commands.stream()
                .map(Command::getName)
                .filter(name -> name.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }

    @EventTarget
    private void onChat(EventChat event) {

        String message = event.getMessage();
        if (message.startsWith(".")) {
            event.cancelEvent();

            String[] args = message.trim().substring(1).split(" ");
            String commandName = args[0].toLowerCase();
            Command command = getCommandByName(commandName);

            if (command != null) {
                command.execute(Arrays.copyOfRange(args, 1, args.length));
            } else {
                List<String> suggestions = getCommandSuggestions(commandName);
                if (!suggestions.isEmpty()) {
                    DebugHelper.sendMessage("可用指令: " + String.join(" | ", suggestions));
                }
            }
        }
    }
}
