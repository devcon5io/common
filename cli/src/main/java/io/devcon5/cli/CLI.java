/*
 * Copyright 2015-2016 DevCon5 GmbH, info@devcon5.ch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.devcon5.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * CLI helper to parse arguments and parse them into a business class with field annotated to map the arguments.
 * Use {@link CliOption} which fields should be populated from command line argument or {@link CliOptionGroup} to
 * declare objects that are populated by a set of arguments. To perform additional initialization steps annotate a
 * parameter-less method with {@link PostInject}.
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
