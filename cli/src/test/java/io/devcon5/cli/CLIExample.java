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
