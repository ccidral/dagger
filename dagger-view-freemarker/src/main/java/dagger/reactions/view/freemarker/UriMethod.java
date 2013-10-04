package dagger.reactions.view.freemarker;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

import java.util.List;

public class UriMethod implements TemplateMethodModel {

    private final String contextPath;

    public UriMethod(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 1)
            throw new TemplateModelException("Wrong number of arguments. " +
                "Expected one string argument, got "+arguments.size()+" arguments.");

        String relativePath = (String) arguments.get(0);

        return joinPaths(
            prependSlashTo(contextPath),
            prependSlashTo(relativePath)
        );
    }

    private String prependSlashTo(String path) {
        if(path == null || path.length() == 0)
            return "/";

        if(path.charAt(0) == '/')
            return path;

        return "/" + path;
    }

    private String joinPaths(String a, String b) {
        char lastCharOfA = a.charAt(a.length() - 1);
        char firstCharOfB = b.charAt(0);

        if(lastCharOfA == '/' && firstCharOfB == '/')
            return a + b.substring(1);

        if(lastCharOfA != '/' && firstCharOfB != '/')
            return a + "/" + b;

        return a + b;
    }

}
