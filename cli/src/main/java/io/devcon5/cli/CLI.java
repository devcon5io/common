package io.devcon5.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * CLI helper to parse arguments and parse them into a business class with field annotated to map the arguments.
 */
public class CLI {

    private final String[] args;

    private CLI(String... args) {
        this.args = new String[args.length];
        System.arraycopy(args, 0, this.args, 0, args.length);
    }

    /**
     * Creates a new CLI instance for the given arguments.
     *
     * @param args
     *         the arguments to parse
     *
     * @return
     */
    public static CLI parse(String... args) {
        return new CLI(args);
    }

    /**
     * The target object to parse the arguments into. One CLI can be injected into multiple targets. The target may
     * have fields annotated with {@link io.devcon5.cli.CliOption} or {@link io.devcon5.cli.CliOptionGroup} which are
     * required to populate those fields with values from the argline
     *
     * @param target
     *         the target to parse values into
     * @param <T>
     *         the type of the target
     *
     * @return true, if the arguments could be successfully parsed and assigned
     */
    public <T> boolean into(T target) {

        final Options opts = new OptionCollector().collectFrom(target.getClass());
        final CommandLineParser parser = new DefaultParser();
        try {
            final CommandLine commandLine = parser.parse(opts, args);
            new OptionInjector(commandLine).injectInto(target);
        } catch (ParseException e) {
            printUsage(opts);
            return false;
        }
        return true;
    }

    /**
     * Prints out the options for the cli
     */
    public void printUsage(Options opts) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("[command]", opts);
    }

}
