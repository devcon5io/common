package io.devcon5.classutils;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

/**
 */
public class ClassStreamsTest {

    @Test
    public void testSupertypes() throws Exception {

        //arrange

        //act
        List<Class> superTypes = ClassStreams.supertypes(Leaf.class).collect(Collectors.toList());

        //assert
        assertThat(superTypes, hasSize(2));
        assertThat(superTypes, contains(Intermediate.class, BaseType.class));
    }

    @Test
    public void testSelfAndSupertypes() throws Exception {

        //arrange

        //act
        List<Class> superTypes = ClassStreams.selfAndSupertypes(Leaf.class).collect(Collectors.toList());

        //assert
        assertThat(superTypes, hasSize(3));
        assertThat(superTypes, contains(Leaf.class, Intermediate.class, BaseType.class));
    }

    static class BaseType {

    }

    static class Intermediate extends BaseType {

    }

    static class Leaf extends Intermediate {

    }
}
