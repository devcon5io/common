package io.devcon5.classutils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;

/**
 *
 */
public class JarScannerTest {

    /**
     * The class under test
     */
    private JarScanner subject = new JarScanner();

    @Test
    public void testIgnore_list() throws Exception {
        //prepare
        URL jar1Url = JarScannerTest.class.getResource("test1.zip");
        URL jar2Url = JarScannerTest.class.getResource("test2.zip");
        subject.addJar(Arrays.asList(jar1Url, jar2Url));

        //act
        subject.ignore(Arrays.asList("META-INF", "io.devcon5.mixin"));

        //assert
        Collection<String> pkgs = subject.scanPackages();
        assertTrue(pkgs.contains("io.devcon5.classutils"));
        assertFalse(pkgs.contains("io.devcon5.mixin"));
        assertFalse(pkgs.contains("META-INF"));
    }

    @Test
    public void testIgnore_varargs() throws Exception {
        //prepare
        URL jar1Url = JarScannerTest.class.getResource("test1.zip");
        URL jar2Url = JarScannerTest.class.getResource("test2.zip");
        subject.addJar(Arrays.asList(jar1Url, jar2Url));

        //act
        subject.ignore("META-INF", "io.devcon5.mixin");

        //assert
        Collection<String> pkgs = subject.scanPackages();
        System.out.println(pkgs);
        assertTrue(pkgs.contains("io.devcon5.classutils"));
        assertFalse(pkgs.contains("io.devcon5.mixin"));
        assertFalse(pkgs.contains("META-INF"));
    }

    @Test
    public void testAddJar() throws Exception {
        //prepare
        URL jar1Url = JarScannerTest.class.getResource("test1.zip");
        URL jar2Url = JarScannerTest.class.getResource("test2.zip");

        //act
        subject.addJar(Arrays.asList(jar1Url, jar2Url));

        //assert
        Collection<String> pkgs = subject.scanPackages();
        assertTrue(pkgs.contains("io.devcon5.classutils"));
        assertTrue(pkgs.contains("io.devcon5.mixin"));
        assertTrue(pkgs.contains("META-INF"));
    }

    @Test
    public void testAddJar_varargs() throws Exception {
        //prepare
        URL jar1Url = JarScannerTest.class.getResource("test1.zip");
        URL jar2Url = JarScannerTest.class.getResource("test2.zip");

        //act
        subject.addJar(jar1Url, jar2Url);

        //assert
        Collection<String> pkgs = subject.scanPackages();
        assertTrue(pkgs.contains("io.devcon5.classutils"));
        assertTrue(pkgs.contains("io.devcon5.mixin"));
        assertTrue(pkgs.contains("META-INF"));
    }

    @Test
    public void testScanPackages() throws Exception {
        //prepare
        URL jarUrl = JarScannerTest.class.getResource("test1.zip");
        subject.addJar(jarUrl);

        //act
        Collection<String> pkgs = subject.scanPackages();

        //assert
        assertTrue(pkgs.contains("io"));
        assertTrue(pkgs.contains("io.devcon5"));
        assertTrue(pkgs.contains("io.devcon5.classutils"));
    }

    @Test
    public void testScanClasses() throws Exception {
        //prepare
        URL jarUrl = JarScannerTest.class.getResource("test1.zip");
        subject.addJar(jarUrl);

        //act
        Collection<String> classes = subject.scanClasses();

        //assert
        assertTrue(classes.contains("io.devcon5.classutils.ClassStreams"));
        assertFalse(classes.contains("io"));
        assertFalse(classes.contains("io.devcon5"));
        assertFalse(classes.contains("io.devcon5.classutils"));
    }
}
