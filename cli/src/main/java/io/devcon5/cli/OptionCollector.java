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

import static org.apache.commons.cli.Option.builder;

import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import io.devcon5.classutils.ClassStreams;

/**
 * Collector for collecting {@link Option}s into an {@link org.apache.commons.cli.Options} instance for a giving type.
 * The method traverses the class hierarchy collecting all options from all supertypes.<br> The collector detects {@link
 * io.devcon5.cli.CliOption} annotated fields and resolves {@link io.devcon5.cli.CliOptionGroup} fields.
 */
public class OptionCollector {

    /**
     * Collects all Options for a given type. The collector traverses the type hierarchy and resolves Option groups.
     *
     * @param type
     *         the type to collect options from
     *
     * @return
     */
    public Options collectFrom(Class type) {
        final Options opts = new Options();
        collectOptions(type, opts);
        return opts;
    }

    private Class collectOptions(final Class targetType, final Options opts) {

        ClassStreams.selfAndSupertypes(targetType).forEach(type -> {
            collectOptionFields(type, opts);
            collectOptionGroup(type, opts);
        });

        return targetType;
    }

    private void collectOptionGroup(final Class targetType, final Options opts) {
        Arrays.stream(targetType.getDeclaredFields())
              .filter(field -> field.getAnnotation(CliOptionGroup.class) != null)
              .map(field -> field.getType())
              .forEach(fieldType -> collectOptions(fieldType, opts));

    }

    private void collectOptionFields(final Class targetType, final Options opts) {
        Arrays.stream(targetType.getDeclaredFields())
              .map(field -> Optional.ofNullable(field.getAnnotation(CliOption.class)))
              .filter(Optional::isPresent)
              .map(opt -> toOption(opt.get()))
              .forEach(opt -> {
                  if (opts.hasShortOption(opt.getOpt())) {
                      throw new RuntimeException("Ambiguous short option definitions found for " + opt.getOpt());
                  } else if (opt.hasLongOpt() && opts.hasLongOption(opt.getLongOpt())) {
                      throw new RuntimeException("Ambiguous long option definitions found for " + opt.getLongOpt());
                  }
                  opts.addOption(opt);
              });
    }

    /**
     * Converts a {@link io.devcon5.cli.CliOption} into a {@link org.apache.commons.cli.Option}.
     *
     * @param cliParam
     *         the cli annotation to convert
     *
     * @return the Apache CLI Option instance.
     */
    private Option toOption(CliOption cliParam) {
        Option.Builder builder = builder(cliParam.value()).hasArg(cliParam.hasArg())
                                                          .required(cliParam.required());
        if (!cliParam.longOpt().isEmpty()) {
            builder.longOpt(cliParam.longOpt());
        }

        if (!cliParam.desc().isEmpty()) {
            builder.desc(cliParam.desc());
        }

        return builder.build();
    }
}
