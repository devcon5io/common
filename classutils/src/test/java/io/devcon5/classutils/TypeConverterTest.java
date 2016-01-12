package io.devcon5.classutils;

import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Created by m4g on 11.01.2016.
 */
public class TypeConverterTest {

    @Test
    public void convertTo_() throws Exception {
        //Arrange
        String input = "true";
        Class type = boolean.class;
        //Act
        Object result = TypeConverter.convert(input).to(type);

        //Assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_boolean() throws Exception {
        //arrange
        String input = "true";
        Class type = boolean.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_Boolean() throws Exception {
        //arrange
        String input = "true";
        Class type = Boolean.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_byte() throws Exception {
        //arrange
        String input = "1";
        Class type = byte.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_Byte() throws Exception {
        //arrange
        String input = "1";
        Class type = Byte.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_short() throws Exception {
        //arrange
        String input = "1";
        Class type = short.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_Short() throws Exception {
        //arrange
        String input = "1";
        Class type = Short.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_int() throws Exception {
        //arrange
        String input = "1";
        Class type = int.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_Integer() throws Exception {
        //arrange
        String input = "1";
        Class type = Integer.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_long() throws Exception {
        //arrange
        String input = "1";
        Class type = long.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_Long() throws Exception {
        //arrange
        String input = "1";
        Class type = Long.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_float() throws Exception {
        //arrange
        String input = "1";
        Class type = float.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_Float() throws Exception {
        //arrange
        String input = "1";
        Class type = Float.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_double() throws Exception {
        //arrange
        String input = "1";
        Class type = double.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_Double() throws Exception {
        //arrange
        String input = "1";
        Class type = Double.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }

    @Test
    public void testConvertTo_String() throws Exception {
        //arrange
        String input = "1";
        Class type = String.class;
        //act
        Object result = TypeConverter.convert(input).to(type);

        //assert
        assertThat(result, isA(type));
    }
}
