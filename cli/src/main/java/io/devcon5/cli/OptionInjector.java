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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.devcon5.classutils.ClassStreams;
import io.devcon5.classutils.TypeConverter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Injects the parameters from the command line into a given target.
 */
public class OptionInjector {

    private final Map<String, Option> options;

    /**
     * Creates a new injector for the options of the given parsed command line.
     *
     * @param commandLine
     */
    public OptionInjector(CommandLine commandLine) {
        this.options = toMap(commandLine);
    }

    public <T> void injectInto(T target) {
        injectParameters(target, options);
        preFlightCheck(target);
    }

    /**
     * Invokes the methodss annotated with {@link PostInject} on the target and its injected option.
     * @param target
     *  the target to invoke the post inject methods
     * @param <T>
     */
    private <T> void preFlightCheck(final T target) {
        if(target == null) {
            return;
        }
        preFlightCheckFields(target);
        final List<Method> methods = getPostInjectMethods(target);
        preFlightCheckMethods(target, methods);
    }

    /**
     * Recursively performs the prefligh check on all {@link CliOptionGroup} annotated fields of the target
     * including supertypes.
     * @param target
     *  the target on whose fields the preflight check should be performed.
     * @param <T>
     */
    private <T> void preFlightCheckFields(final T target) {

        ClassStreams.selfAndSupertypes(target.getClass())
                    .map(Class::getDeclaredFields)
                    .flatMap(Arrays::stream)
                    .filter(field -> field.getAnnotation(CliOptionGroup.class) != null)
                    .forEach(field -> {
                        field.setAccessible(true);
                        try {
                            preFlightCheck(field.get(target));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });
    }

    /**
     * Collects the methods annotated with {@link PostInject} in order of priority.
     * @param target
     *  the target object whose methods should be collected. Supertype methods are collected as well.
     * @param <T>
     * @return
     *  a list of methods annotated with {@link PostInject} ordered by priority
     */
    private <T> List<Method> getPostInjectMethods(final T target) {

        final List<Method> methods = ClassStreams.selfAndSupertypes(target.getClass())
                                           .map(Class::getDeclaredMethods)
                                           .flatMap(Arrays::stream)
                                           .filter(method -> method.getAnnotation(PostInject.class) != null)
                                           .collect(Collectors.toList());
        Collections.sort(methods, (m1, m2) -> m1.getAnnotation(PostInject.class).value() - m2.getAnnotation
                (PostInject.class).value());
        return methods;
    }

    /**
     * Invokes all methods in the list on the target.
     * @param target
     *  the target on which the methods should be ivoked
     * @param methods
     *  the methods to be invoked in order of priority
     * @param <T>
     */
    private <T> void preFlightCheckMethods(final T target, final List<Method> methods) {

        methods.forEach(m -> {m.setAccessible(true);
            try {
                m.invoke(target);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Inject parameter values from the map of options.
     *
     * @param target
     *         the target instance to parse cli parameters
     * @param options
     *         the map of options, mapping short option names to {@link org.apache.commons.cli.Option} instances
     */
    private void injectParameters(Object target, Map<String, Option> options) {
        ClassStreams.selfAndSupertypes(target.getClass()).forEach(type -> {
            for (Field f : type.getDeclaredFields()) {
                Optional.ofNullable(f.getAnnotation(CliOption.class))
                        .ifPresent(opt -> populate(f, target, getEffectiveValue(options, opt)));
                Optional.ofNullable(f.getAnnotation(CliOptionGroup.class))
                        .ifPresent(opt -> populate(f, target, options));
            }
        });
    }

    /**
     * Returns the effective value for the specified option.
     *
     * @param options
     *         the parsed option
     * @param opt
     *         the cli option annotation for which the actual argument should be retrieved
     *
     * @return the effective value for the given option <ul> <li>If the option should have an argument, and an argument
     * is provided, the provided argument is returned</li> <li>If the option should have an argument, and none is
     * specified, the default value is returned</li> <li>If the option has no argument, and is specified, "true" is
     * returned</li> <li>If the option has no argument, and is not specifeid, "false" is returned</li> </ul>
     */
    private String getEffectiveValue(Map<String, Option> options, CliOption opt) {
        final String shortOpt = opt.value();
        if (opt.hasArg()) {
            if (options.containsKey(shortOpt)) {
                return options.get(shortOpt).getValue();
            }
            return opt.defaultValue();
        }
        return Boolean.toString(options.containsKey(opt.value()));
    }

    /**
     * Populates the specified field on the target with the value from the given option.
     *
     * @param field
     *         the field to populate
     * @param target
     *         the target instance containing the field
     * @param stringValue
     *         the value to be set on the field
     */
    private void populate(Field field, Object target, String stringValue) {
        field.setAccessible(true);
        try {
            final Class fieldType = field.getType();
            final Object value = TypeConverter.convert(stringValue).to(fieldType);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not populate field " + field, e);
        }
    }

    /**
     * Populates the specified field on the target with the value from the given option.
     *
     * @param field
     *         the field to populate
     * @param target
     *         the target instance containing the field
     * @param options
     *         a map of all available options
     */
    private void populate(Field field, Object target, Map<String, Option> options) {
        field.setAccessible(true);
        try {
            final Object fieldValue = field.getType().newInstance();
            injectParameters(fieldValue, options);
            field.set(target, fieldValue);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Could not populate field " + field, e);
        }
    }

    /**
     * Creates a map of option short-names to the corresponding option instance.
     *
     * @param cl
     *         the command line containing the parsed option instances
     *
     * @return a map of parameter short namese to their corresponding option instance.
     */
    private Map<String, Option> toMap(CommandLine cl) {
        final Map<String, Option> opts = new HashMap<>();
        for (Option opt : cl.getOptions()) {
            opts.put(opt.getOpt(), opt);
        }
        return opts;
    }
}
