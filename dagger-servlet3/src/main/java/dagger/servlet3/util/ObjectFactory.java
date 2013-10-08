package dagger.servlet3.util;

import dagger.servlet3.DaggerServletException;

public class ObjectFactory {

    public static <T> T createInstanceOf(String className) throws DaggerServletException {
        Class<?> clazz = loadClass(className);
        try {
            return (T) clazz.newInstance();
        } catch (InstantiationException e) {
            throw new DaggerServletException(e);
        } catch (IllegalAccessException e) {
            throw new DaggerServletException(e);
        }
    }

    private static Class<?> loadClass(String className) throws DaggerServletException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new DaggerServletException(e);
        }
    }

}
