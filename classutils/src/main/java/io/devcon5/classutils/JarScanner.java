package io.devcon5.classutils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A scanner for contents of jar files. The scanner supports scanning for packages and contents.
 */
public class JarScanner {

    /**
     * Set of entry-prefixes to be excluded from scanning
     */
    private final Set<String> ignore = new HashSet<>();

    /**
     * Set of Jar locators to be scanned
     */
    private final Set<URI> jars = new HashSet<>();

    /**
     * env map for opening Zip-Filesystems in read-only mode
     */
    private static final Map<String, String> READY_ONLY_ENV;

    static {
        final Map<String, String> env = new HashMap<>();
        env.put("create", "false");
        READY_ONLY_ENV = Collections.unmodifiableMap(env);
    }

    /**
     * Excludes the specified folders from scanning. The ignore pattern checks matches packages and classes
     * starting with the specified string.
     *
     * @param folders
     *         folders to be ignored
     *
     * @return this scanner
     */
    public JarScanner ignore(Collection<String> folders) {
        this.ignore.addAll(folders);
        return this;
    }

    /**
     * Excludes the specified folders from scanning. The ignore pattern checks matches packages and classes
     * starting with the specified string.
     *
     * @param folder
     *         folder(s) to be ignored
     *
     * @return this scanner
     */
    public JarScanner ignore(String... folder) {
        return ignore(Arrays.asList(folder));
    }

    /**
     * Adds a jar file to be scanned
     *
     * @param jar
     *         jar(s) to be added to the scanner
     *
     * @return this scanner
     */
    public JarScanner addJar(URL... jar) {
        return addJar(Arrays.asList(jar));
    }

    /**
     * Adds a jar file to be scanned
     *
     * @param jars
     *         jars to be added to the scanner
     *
     * @return this scanner
     */
    public JarScanner addJar(Collection<URL> jars) {
        this.jars.addAll(jars.stream().map(JarScanner::toUri).collect(Collectors.toList()));
        return this;
    }

    private static URI toUri(final URL u) {

        try {
            return u.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Could not convert URL "+u+" to uri",e);
        }
    }

    /**
     * Scans the specified jars for packages and returns a list of all packages found in that jar. The scanner makes no
     * distinction between packages and folders.
     *
     * @return a collection of all package names found in the jar
     */
    public Collection<String> scanPackages() {
        return scanJar(p -> Files.isDirectory(p) && !isIgnored(p.toAbsolutePath()));
    }

    /**
     * Scans the specified jars for packages and returns a list of all packages found in that jar. The scanner makes no
     * distinction between packages and folders.
     *
     * @return a collection of all classes names found in the jar
     */
    public Collection<String> scanClasses() {
        return scanJar(p -> Files.isRegularFile(p) && p.toString().endsWith(".class") && !isIgnored(p.toAbsolutePath()));
    }

    /**
     * Scans the jars of the JarScanner finding all items matching the path filter
     * @param pathFilter
     *  the filter to find items in the jars
     * @return
     *  Collections of items of the jars matching the filter
     */
    private Collection<String> scanJar(Predicate<Path> pathFilter) {
        return jars.parallelStream()
                   .map(JarScanner::createJarUri)
                   .flatMap(u -> scanJar(u, pathFilter).stream())
                   .distinct()
                   .collect(Collectors.toList());
    }

    /**
     * Scans the jar and filters all elements
     *
     * @param u
     *         the uri of the jar file to be scanned
     * @param pathPredicate
     *         the matching predicate for collecting the entries
     *
     * @return a stream of collected strings
     */
    private Collection<String> scanJar(URI u, Predicate<Path> pathPredicate) {
        try (FileSystem fs = FileSystems.newFileSystem(u, READY_ONLY_ENV)) {
            return Files.walk(fs.getPath("/"))
                        .filter(pathPredicate)
                        .map(f -> toFQName(f.toAbsolutePath()))
                        .distinct()
                        .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts the path to a fully qualified java entity (package or class)
     *
     * @param file
     *         the file to be converted
     *
     * @return a string representing the fully qualified package or classname
     */
    private String toFQName(Path file) {
        return file.toString()
                   .replaceAll("(\\/|\\\\)", ".")
                   .replaceAll("(^\\.+|(\\.+|\\.class)$)", "");
    }

    /**
     * Checks if the specified path is on the ignore list
     *
     * @param p
     *         the path to check
     *
     * @return true if the path should be ignored
     */
    private boolean isIgnored(Path p) {
        final String path = toFQName(p);
        return this.ignore.stream().anyMatch(path::startsWith);
    }

    /**
     * Creates a jar-protocol uri for the specified url
     *
     * @param uri
     *         the uri for which to create a jar uri
     *
     * @return the URI for the jar file
     */
    private static URI createJarUri(URI uri) {
        return URI.create("jar:" + uri);
    }
}
