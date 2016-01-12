package io.devcon5.cli;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 */
public class CLIExample {

    @CliOption(value = "x",
               hasArg = true)
    private String example;

    @CliOptionGroup
    private Structured credentials;

    @Test
    public void example() {
        //arrange
        String[] exampleArgs = {"-u", "hans", "-p", "wurst", "-x", "example"};

        //act
        CLIExample example = CLI.inject(exampleArgs).into(new CLIExample());
        example.run();

        //assert
    }

    public void run() {
        assertThat(example, is(not(null)));
        assertThat(credentials.user, is(not(null)));
        assertThat(credentials.password, is(not(null)));
    }

    static class Structured {

        @CliOption(value = "u",
                   hasArg = true)
        private String user;

        @CliOption(value = "p",
                   hasArg = true)
        private String password;
    }

}
