package de.skyslycer.gptreflection.tests;

import de.skyslycer.gptreflection.ReflectionHelper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReflectionHelperTest {

    @Test
    public void testGetFieldValue() throws Exception {
        TestObject testObject = new TestObject();
        assertEquals(123, ReflectionHelper.getFieldValue(Integer.class, testObject, "privateInt"));
        assertEquals("Hello, World!", ReflectionHelper.getFieldValue(String.class, testObject, "privateString"));
    }

    @Test
    public void testSetFieldValue() throws Exception {
        TestObject testObject = new TestObject();
        ReflectionHelper.setFieldValue(testObject, "privateInt", 321);
        assertEquals(321, ReflectionHelper.getFieldValue(Integer.class, testObject, "privateInt"));

        ReflectionHelper.setFieldValue(testObject, "privateString", "Goodbye, World!");
        assertEquals("Goodbye, World!", ReflectionHelper.getFieldValue(String.class, testObject, "privateString"));

        ReflectionHelper.setFieldValue(testObject, "privateFinalInt", 654);
        assertEquals(654, ReflectionHelper.getFieldValue(Integer.class, testObject, "privateFinalInt"));
    }

    @Test
    public void testInvokeMethod() throws Exception {
        TestObject testObject = new TestObject();
        assertEquals(7, ReflectionHelper.invokeMethod(Integer.class, testObject, "add", List.of(int.class, int.class), 3, 4));
    }

    @Test
    public void testInvokeVoidMethod() throws Exception {
        TestObject testObject = new TestObject();
        ReflectionHelper.invokeMethod(Void.class, testObject, "reset");
        assertEquals(0, ReflectionHelper.getFieldValue(Integer.class, testObject, "value"));
    }

    @Test
    public void testInvokeOverloadedMethodNoParameters() throws Exception {
        TestObject testObject = new TestObject();
        ReflectionHelper.invokeMethod(null, testObject, "overloadMethod");
        assertEquals("no parameters", ReflectionHelper.getFieldValue(String.class, testObject, "message"));
    }

    @Test
    public void testInvokeOverloadedMethodWithParameters() throws Exception {
        TestObject testObject = new TestObject();
        ReflectionHelper.invokeMethod(Void.class, testObject, "overloadMethod", "test message");
        assertEquals("test message", ReflectionHelper.getFieldValue(String.class, testObject, "message"));
    }

    @Test
    public void testExceptionWhenInvalidField() {
        TestObject testObject = new TestObject();
        assertThrows(NoSuchFieldException.class, () -> ReflectionHelper.getFieldValue(Object.class, testObject, "nonexistentField"));
        assertThrows(NoSuchFieldException.class, () -> ReflectionHelper.setFieldValue(testObject, "nonexistentField", null));
    }

    @Test
    public void testExceptionWhenInvalidMethod() {
        TestObject testObject = new TestObject();
        assertThrows(NoSuchMethodException.class, () -> ReflectionHelper.invokeMethod(Object.class, testObject, "nonexistentMethod"));
    }

    @Test
    public void testExceptionWhenWrongFieldType() {
        TestObject testObject = new TestObject();
        assertThrows(ClassCastException.class, () -> ReflectionHelper.getFieldValue(String.class, testObject, "privateInt"));
    }

    @Test
    public void testExceptionWhenWrongReturnType() {
        TestObject testObject = new TestObject();
        assertThrows(ClassCastException.class, () -> ReflectionHelper.invokeMethod(String.class, testObject, "add", List.of(int.class, int.class), 3, 4));
    }

    public static class TestObject {
        private final int privateFinalInt = 456;
        private final int privateInt = 123;
        private final String privateString = "Hello, World!";
        private int value = 0;
        private String message = "default";

        private int add(int x, int y) {
            return x + y;
        }

        public void reset() {
            value = 0;
        }

        public void overloadMethod() {
            message = "no parameters";
        }

        public void overloadMethod(String newMessage) {
            message = newMessage;
        }
    }

}

