package io.devcon5.classutils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A converter for converting string values into primitive types. <br> Created by m4g on 11.01.2016.
 */
public class TypeConverter {

    private static final Map<Class, Class> PRIMITIVE_TO_OBJECT_TYPE_MAP;

    static {
        final Map<Class, Class> map = new HashMap<>();
        map.put(boolean.class, Boolean.class);
        map.put(byte.class, Byte.class);
        map.put(short.class, Short.class);
        map.put(int.class, Integer.class);
        map.put(long.class, Long.class);
        map.put(float.class, Float.class);
        map.put(double.class, Double.class);
        PRIMITIVE_TO_OBJECT_TYPE_MAP = Collections.unmodifiableMap(map);
    }

    /**
     * The value to be converted
     */
    private final String value;

    private TypeConverter(String value) {
        this.value = value;
    }

    /**
     * Creates a new type converter for the given value.
     *
     * @param value
     *         the value to be converted
     *
     * @return a new type converter
     */
    public static TypeConverter convert(String value) {
        return new TypeConverter(value);
    }

    /**
     * Converts the underlying string value to a primitive value.
     *
     * @param targetType
     *         the type into which the string value should be converted.
     *
     * @return the converted value.
     */
    public <T> T to(Class<T> targetType) {
        if (targetType == String.class) {
            return (T) value;
        }
        try {
            final String methodName;
            final Class type;
            if (targetType.isPrimitive()) {
                final String typeName = targetType.getSimpleName();
                methodName = "parse" + typeName.substring(0, 1).toUpperCase() + typeName.substring(1);
                type = PRIMITIVE_TO_OBJECT_TYPE_MAP.get(targetType);
            } else {
                methodName = "valueOf";
                type = targetType;
            }
            return (T) type.getMethod(methodName, String.class).invoke(null, value);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Could not convert value '" + value + "' to type " + targetType.getName(), e);
        }
    }
}
