package de.skyslycer.gptreflection;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides static utility methods for accessing private fields
 * and invoking private methods of an object via reflection. It uses caching
 * to optimize reflection operations.
 */
public class ReflectionHelper {

    // Field cache map
    private static final Map<Class<?>, Map<String, Field>> fieldsCache = new HashMap<>();
    // Method cache map
    private static final Map<Class<?>, Map<String, Method>> methodsCache = new HashMap<>();

    /**
     * Get a field's value from an object.
     *
     * @param expectedType The expected type of the field's value.
     * @param object       The object from which the field's value is to be extracted.
     * @param fieldName    The name of the field.
     * @return The value of the specified private field.
     * @throws NoSuchFieldException   if the field does not exist.
     * @throws IllegalAccessException if this Field object is enforcing Java language access control and the underlying field is inaccessible.
     * @throws ClassCastException     if the value of the specified field is not an instance of the expectedType.
     */
    public static <T> T getFieldValue(Class<T> expectedType, Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = getCachedField(object.getClass(), fieldName);
        field.setAccessible(true);
        Object result = field.get(object);
        if (expectedType.isInstance(result)) {
            return expectedType.cast(result);
        } else {
            throw new ClassCastException("The type of field " + fieldName + " is not " + expectedType.getName());
        }
    }

    /**
     * Set a field's value in an object.
     *
     * @param object    The object in which the field's value is to be changed.
     * @param fieldName The name of the field.
     * @param newValue  The new value for the field.
     * @throws NoSuchFieldException   if the field does not exist.
     * @throws IllegalAccessException if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     */
    public static void setFieldValue(Object object, String fieldName, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        Field field = getCachedField(object.getClass(), fieldName);
        field.setAccessible(true);

        if (Modifier.isFinal(field.getModifiers())) {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);

            long offset = unsafe.objectFieldOffset(field);
            Class<?> fieldType = field.getType();

            if (fieldType == int.class) {
                unsafe.putInt(object, offset, (Integer) newValue);
            } else if (fieldType == long.class) {
                unsafe.putLong(object, offset, (Long) newValue);
            } else if (fieldType == boolean.class) {
                unsafe.putBoolean(object, offset, (Boolean) newValue);
            } else if (fieldType == byte.class) {
                unsafe.putByte(object, offset, (Byte) newValue);
            } else if (fieldType == short.class) {
                unsafe.putShort(object, offset, (Short) newValue);
            } else if (fieldType == char.class) {
                unsafe.putChar(object, offset, (Character) newValue);
            } else if (fieldType == float.class) {
                unsafe.putFloat(object, offset, (Float) newValue);
            } else if (fieldType == double.class) {
                unsafe.putDouble(object, offset, (Double) newValue);
            } else {
                unsafe.putObject(object, offset, newValue);
            }
        } else {
            field.set(object, newValue);
        }
    }


    /**
     * Invokes a method on an object.
     *
     * @param returnType The expected return type of the method.
     * @param object     The object on which the method is to be invoked.
     * @param methodName The name of the method.
     * @param args       The arguments to the method.
     * @return The return value of the method.
     * @throws NoSuchMethodException     if a matching method is not found.
     * @throws InvocationTargetException if the underlying method throws an exception.
     * @throws IllegalAccessException    if this Method object is enforcing Java language access control and the underlying method is inaccessible.
     * @throws ClassCastException        if the return type of the method is not an instance of the returnType.
     */
    public static <T> T invokeMethod(Class<T> returnType, Object object, String methodName, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?>[] argClasses = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argClasses[i] = args[i].getClass();
        }
        Method method = getCachedMethod(object.getClass(), methodName, argClasses);
        return invokeMethodPrivate(returnType, object, method, methodName, args);
    }


    /**
     * Invokes a method on an object with specified argument types.
     * This is only useful for primitive arguments.
     *
     * @param <T>        The expected return type of the method.
     * @param returnType The expected return type of the method.
     * @param object     The object on which the method is to be invoked.
     * @param methodName The name of the method.
     * @param argClasses The classes of the arguments to the method.
     * @param args       The arguments to the method.
     * @return The return value of the method.
     * @throws NoSuchMethodException     if a matching method is not found.
     * @throws InvocationTargetException if the underlying method throws an exception.
     * @throws IllegalAccessException    if this Method object is enforcing Java language access control and the underlying method is inaccessible.
     * @throws ClassCastException        if the return type of the method is not an instance of the returnType.
     */
    public static <T> T invokeMethod(Class<T> returnType, Object object, String methodName, List<Class<?>> argClasses, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassCastException {
        Method method = getCachedMethod(object.getClass(), methodName, argClasses.toArray(new Class<?>[0]));
        return invokeMethodPrivate(returnType, object, method, methodName, args);
    }

    /**
     * Invokes a method on an object.
     * This method is private and intended for internal use only.
     *
     * @param <T>        The expected return type of the method.
     * @param returnType The expected return type of the method.
     * @param object     The object on which the method is to be invoked.
     * @param method     The Method object representing the method to be invoked.
     * @param methodName The name of the method.
     * @param args       The arguments to the method.
     * @return The return value of the method.
     * @throws InvocationTargetException if the underlying method throws an exception.
     * @throws IllegalAccessException    if the Method object is enforcing Java language access control and the underlying method is inaccessible.
     * @throws ClassCastException        if the return type of the method is not an instance of the returnType.
     */
    private static <T> T invokeMethodPrivate(Class<T> returnType, Object object, Method method, String methodName, Object... args) throws InvocationTargetException, IllegalAccessException, ClassCastException {
        Object result = method.invoke(object, args);
        if (returnType == Void.class || returnType == void.class || returnType == null) {
            if (result != null) {
                throw new ClassCastException("The return type of the method " + methodName + " is not void");
            }
            return null;
        } else {
            if (returnType.isInstance(result)) {
                return returnType.cast(result);
            } else {
                throw new ClassCastException("The return type of the method " + methodName + " is not " + returnType.getName());
            }
        }
    }

    /**
     * Retrieves a field from cache or from class if it is not in the cache.
     *
     * @param clazz     The class to retrieve the field from.
     * @param fieldName The name of the field.
     * @return The retrieved field.
     * @throws NoSuchFieldException if the field does not exist.
     */
    private static Field getCachedField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        if (!fieldsCache.containsKey(clazz)) {
            fieldsCache.put(clazz, new HashMap<>());
        }
        Map<String, Field> classFields = fieldsCache.get(clazz);
        if (!classFields.containsKey(fieldName)) {
            Field field = clazz.getDeclaredField(fieldName);
            classFields.put(fieldName, field);
        }
        return classFields.get(fieldName);
    }

    /**
     * Retrieves a method from cache or from class if it is not in the cache.
     *
     * @param clazz      The class to retrieve the method from.
     * @param methodName The name of the method.
     * @param argClasses The parameter types of the method.
     * @return The retrieved method.
     * @throws NoSuchMethodException if a matching method is not found.
     */
    private static Method getCachedMethod(Class<?> clazz, String methodName, Class<?>... argClasses) throws NoSuchMethodException {
        if (!methodsCache.containsKey(clazz)) {
            methodsCache.put(clazz, new HashMap<>());
        }
        String methodKey = buildMethodKey(methodName, argClasses);
        Map<String, Method> classMethods = methodsCache.get(clazz);
        if (!classMethods.containsKey(methodKey)) {
            Method method = clazz.getDeclaredMethod(methodName, argClasses);
            method.setAccessible(true);
            classMethods.put(methodKey, method);
        }
        return classMethods.get(methodKey);
    }

    /**
     * Builds a unique key for a method based on its name and parameter types.
     *
     * @param methodName The name of the method.
     * @param argClasses The parameter types of the method.
     * @return The unique key for the method.
     */
    private static String buildMethodKey(String methodName, Class<?>... argClasses) {
        StringBuilder sb = new StringBuilder(methodName);
        for (Class<?> clazz : argClasses) {
            sb.append("-").append(clazz.getName());
        }
        return sb.toString();
    }

}
