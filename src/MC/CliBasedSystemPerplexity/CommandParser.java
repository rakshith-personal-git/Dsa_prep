package MC.CliBasedSystemPerplexity;

import java.util.Arrays;
import java.util.List;

/**
 * Parses raw command-line strings into structured commands.
 */
public class CommandParser {

    /**
     * Parses an input line into a CommandType and arguments.
     */
    public ParsedCommand parse(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Empty command");
        }
        List<String> tokens = Arrays.asList(trimmed.split("\s+"));
        CommandType type = CommandType.valueOf(tokens.get(0));
        List<String> args = tokens.subList(1, tokens.size());
        return new ParsedCommand(type, args);
    }

    /**
     * Simple value object holding parsed command data.
     */
    public static class ParsedCommand {
        private final CommandType type;
        private final List<String> args;

        public ParsedCommand(CommandType type, List<String> args) {
            this.type = type;
            this.args = args;
        }

        public CommandType getType() {
            return type;
        }

        public List<String> getArgs() {
            return args;
        }
    }
}
