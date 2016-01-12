package io.devcon5.cli;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for declaring fields as CLI paramter. The parameter field gets populated by the CLI client
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CliOption {

    /**
     * The short name of the option, i.e. -i (without dash)
     *
     * @return
     */
    String value();

    /**
     * The long name of the option, i.e. --info (without dashes)
     *
     * @return
     */
    String longOpt() default "";

    /**
     * The textual description of the option
     *
     * @return
     */
    String desc() default "";

    /**
     * True, if the paramter has an argument, default is false.
     *
     * @return
     */
    boolean hasArg() default false;

    /**
     * True, if the parameter is required, default is false;
     *
     * @return
     */
    boolean required() default false;

    /**
     * The default value of this parameter. The default value is used, when the parameter is not required and an
     * argument has been ommitted.
     *
     * @return the default value as a string or null, if none is set
     */
    String defaultValue() default "";
}
