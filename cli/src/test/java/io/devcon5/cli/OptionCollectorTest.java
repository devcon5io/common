package io.devcon5.cli;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

/**
 */
public class OptionCollectorTest {

    @Test
    public void testCollectFrom() throws Exception {
        //arrange
        OptionCollector oc = new OptionCollector();

        //act
        Options opts = oc.collectFrom(Leaf.class);

        //assert
        assertThat(opts, hasOptions("a", "b", "c", "d", "e", "f", "g"));
    }

    @Test(expected = RuntimeException.class)
    public void testCollectFrom_ambigueShortOpt() throws Exception {
        //arrange
        OptionCollector oc = new OptionCollector();

        //act
        oc.collectFrom(AmbigueLeaf.class);

    }

    private Matcher<? super Options> hasOptions(String... shortOpts) {
        return new BaseMatcher<Options>() {
            @Override
            public boolean matches(Object opts) {
                if (opts instanceof Options) {
                    final List<String> expected = Arrays.asList(shortOpts);
                    final List<String> actual = ((Options) opts).getOptions()
                                                                .stream()
                                                                .map(Option::getOpt)
                                                                .collect(toList());
                    return expected.stream().allMatch(e -> actual.contains(e));

                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("contains option " + Arrays.asList(shortOpts));
            }
        };
    }

    static class Structured {

        @CliOption("a")
        private boolean a;

        @CliOption("b")
        private boolean b;

    }

    static class Base {

        @CliOption("c")
        private boolean c;

        @CliOptionGroup
        private Structured structured;

    }

    static class BaseStructured {

        @CliOption("d")
        private boolean d;

        @CliOption("e")
        private boolean e;

    }

    static class LeafStructured extends BaseStructured {

        @CliOption("f")
        private boolean f;
    }

    static class Leaf extends Base {

        @CliOption("g")
        private boolean g;

        @CliOptionGroup
        private LeafStructured structured;

    }

    static class AmbigueLeaf extends Base {

        //this option is ambigue
        @CliOption("a")
        private boolean a;

        @CliOptionGroup
        private LeafStructured structured;

    }

}
