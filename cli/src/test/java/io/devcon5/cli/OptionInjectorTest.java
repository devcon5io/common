package io.devcon5.cli;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class OptionInjectorTest {

    @Mock
    private CommandLine commandLine;

    /**
     * The class under test
     */
    private OptionInjector subject;

    @Test
    public void testInjectInto() throws Exception {
        //arrange
        Option a = createOption("a", "aValue");
        Option b = createOption("b", "bValue");
        when(commandLine.getOptions()).thenReturn(new Option[]{a, b});
        subject = new OptionInjector(commandLine);
        CliTarget target = new CliTarget();

        //act
        subject.injectInto(target);

        //assert
        Assert.assertThat(target.a, is("aValue"));
        Assert.assertThat(target.s.a, is("aValue"));
        Assert.assertThat(target.s.b, is("bValue"));
        Assert.assertThat(((CliBase) target).b, is("bValue"));
    }

    private Option createOption(String shortOpt, String value) {
        Option opt = mock(Option.class);
        when(opt.getOpt()).thenReturn(shortOpt);
        if (value != null) {
            when(opt.hasArg()).thenReturn(true);
            when(opt.getValue()).thenReturn(value);
        } else {
            when(opt.getValue()).thenReturn("true");
        }
        return opt;
    }

    ////////////////// Classes that provide the structure for the injection //////////////////

    static class CliBase {

        @CliOption(value = "b",
                   hasArg = true)
        private String b;

    }

    static class CliTarget extends CliBase {

        @CliOption(value = "a",
                   hasArg = true)
        private String a;

        @CliOptionGroup
        private Structured s;

    }

    static class Structured {

        @CliOption(value = "a",
                   hasArg = true)
        private String a;

        @CliOption(value = "b",
                   hasArg = true)
        private String b;
    }
}
