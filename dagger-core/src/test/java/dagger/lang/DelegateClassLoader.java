package dagger.lang;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class DelegateClassLoader extends ClassLoader {

    private final Set<String> classesToBeDelegatedToChildrenClassLoaders = new HashSet<String>();
    private final Set<String> resourcesToBeDelegatedToChildrenClassLoaders = new HashSet<String>();

    @Override
    protected synchronized Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        if(classesToBeDelegatedToChildrenClassLoaders.contains(className))
            throw new ClassNotFoundException(className);

        return super.loadClass(className, resolve);
    }

    @Override
    public URL getResource(String resourceName) {
        if(resourceName.endsWith("view/static/foo/bar/static-file-test.html"))
            return null;

        return super.getResource(resourceName);
    }

    public void delegateClassToChildrenClassLoaders(String className) {
        classesToBeDelegatedToChildrenClassLoaders.add(className);
    }

    public void delegateResourceToChildrenClassLoaders(String resourceName) {
        resourcesToBeDelegatedToChildrenClassLoaders.add(resourceName);
    }

}
