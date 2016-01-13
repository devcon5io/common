package io.devcon5.cli;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to declare a method that is invoked after the options have been injected into the target fields.
 * The methods must not have any arguments. One class may annotate multiple methods with this annotation. If an order
 * of execution is required, the priority must
 * be specified (starting with the lowest number).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostInject {

    /**
     * Denotes the priority of execution, with the lowest value having the highest priority. Default ist 0.
     * @return
     */
    int value() default 0;

}
