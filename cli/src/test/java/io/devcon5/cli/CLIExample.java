package io.devcon5.cli;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
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

    private String postProcessed;

    @PostInject
    private void init(){
        postProcessed = "an " + example;
    }

    @Test
    public void example() {
        //arrange
        String[] exampleArgs = {"-u", "hans", "-p", "wurst", "-x", "example"};

        //act
        CLI.parse(exampleArgs).into(this);
        run();

        //assert
        assertEquals("an example", postProcessed);

    }

    public void run() {
        assertThat(example, is(not(nullValue())));
        assertThat(credentials.user, is(not(nullValue())));
        assertThat(credentials.password, is(not(nullValue())));
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
