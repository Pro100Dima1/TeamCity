package api.generators;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class BuildCommands {

    private static final List<CommandLineCommand> COMMAND_LINE_COMMANDS = List.of(
            new CommandLineCommand("echo", "Hello TeamCity"),
            new CommandLineCommand("echo", "Build started"),
            new CommandLineCommand("echo", "Build is running"),
            new CommandLineCommand("echo", "Build finished"),
            new CommandLineCommand("echo", "Test passed"),
            new CommandLineCommand("echo", "Command executed successfully"),
            new CommandLineCommand("echo", "TeamCity Build Step"),
            new CommandLineCommand("echo", "Automation test")
    );

    private BuildCommands() {
    }

    public static CommandLineCommand randomCommandLineCommand() {
        return COMMAND_LINE_COMMANDS.get(
                ThreadLocalRandom.current().nextInt(COMMAND_LINE_COMMANDS.size())
        );
    }
}
