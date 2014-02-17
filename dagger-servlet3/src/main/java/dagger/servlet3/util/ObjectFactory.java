package dagger.servlet3.util;

import dagger.DaggerRuntimeException;

public class ObjectFactory {

    public static <T> T createInstanceOf(String className) {
        Class<?> clazz = loadClass(className);
        try {
            return (T) clazz.newInstance();
        } catch (InstantiationException e) {
            throw new DaggerRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new DaggerRuntimeException(e);
        }
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new DaggerRuntimeException(e);
        }
    }

}
