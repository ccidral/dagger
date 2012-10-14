package dagger.server.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DirectoryWithJarsClassLoader extends URLClassLoader {

    private static Logger logger = LoggerFactory.getLogger(DirectoryWithJarsClassLoader.class);

    public DirectoryWithJarsClassLoader(File directory) throws MalformedURLException {
        super(urls(directory), new DelegateDaggerClassesToChildClassLoader());
    }

    private static URL[] urls(File directory) throws MalformedURLException {
        File[] jars = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && file.getName().endsWith(".jar");
            }
        });

        URL[] urls = new URL[jars.length];
        int index = 0;

        for(File jar : jars) {
            urls[index++] = jar.toURI().toURL();
        }

        logger.info("{} jars loaded", urls.length);

        return urls;
    }

    private static class DelegateDaggerClassesToChildClassLoader extends ClassLoader {

        @Override
        protected synchronized Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
            if(className.startsWith("dagger"))
                throw new ClassNotFoundException(className);

            return super.loadClass(className, resolve);
        }

    }

}
