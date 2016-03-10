package io.devcon5.cli;

import static org.apache.commons.cli.Option.builder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Base CLI client that allows to check parameters before executing the command, printing usage information and
 * extending the accepted parameter list. Created by m4g on 24.11.2015.
 */
@Deprecated
public class CliClient {

    private CommandLine commandLine;

    public void run(String... args) throws Exception {
        try {
            final CommandLine cl = parseArgs(args);
            if (cl.hasOption("help")) {
                printUsage();
                return;
            }
            if (preFlightCheck(cl)) {
                execute(args);
            } else {
                printUsage();
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            printUsage();
        }
    }

    /**
     * Executes the cli command.
     *
     * @param args
     *         the original arguments passed to the main method. Any implementing class may derive additional parameters
     *         from the command line args.
     */
    protected void execute(String... args) throws Exception {
    }

    /**
     * Performs a pre-fligh check to verify the command line parameters are set-up correctly
     *
     * @param cl
     *         the command line to check
     *
     * @return
     */
    protected boolean preFlightCheck(CommandLine cl) {
        return true;
    }

    /**
     * Parses the arguments to configure the importer
     *
     * @param args
     *
     * @throws org.apache.commons.cli.ParseException
     */
    public CommandLine parseArgs(String... args) throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        this.commandLine = parser.parse(getOpts(), args);
        return this.commandLine;
    }

    /**
     * Override this method to define options for the CliClient
     *
     * @return an empty options handle
     */
    protected Options getOpts() {
        final Options opts = new Options();
        opts.addOption(builder("h").longOpt("help").desc("prints out the help").build());
        return opts;
    }

    /**
     * The parsed command line
     *
     * @return
     */
    protected CommandLine getCommandLine() {
        return commandLine;
    }

    /**
     * Prints out the options for the cli
     */
    public void printUsage() {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("[command]", getOpts());
    }
}
