package dagger.reactions.view.freemarker;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

public class UriMethodTest {

    private Object executeUriMethod(String contextPath, String relativePath) throws TemplateModelException {
        TemplateMethodModel uriMethod = new UriMethod(contextPath);
        return uriMethod.exec(asList(relativePath));
    }

    @Test(expected = TemplateModelException.class)
    public void test_throw_exception_when_no_arguments_are_provided() throws Throwable {
        TemplateMethodModel uriMethod = new UriMethod("/context");
        uriMethod.exec(emptyList());
    }

    @Test(expected = TemplateModelException.class)
    public void test_only_one_argument_is_expected() throws Throwable {
        TemplateMethodModel uriMethod = new UriMethod("/context");
        uriMethod.exec(asList("/foo", "/bar"));
    }

    @Test
    public void test_join_context_path_to_relative_path() throws Throwable {
        Object uri = executeUriMethod("/context", "/relative/path");
        assertEquals("/context/relative/path", uri);
    }

    @Test
    public void test_prepend_slash_to_context_path() throws Throwable {
        Object uri = executeUriMethod("context", "/relative/path");
        assertEquals("/context/relative/path", uri);
    }

    @Test
    public void test_prepend_slash_to_relative_path() throws Throwable {
        Object uri = executeUriMethod("/context", "relative/path");
        assertEquals("/context/relative/path", uri);
    }

    @Test
    public void test_remove_duplicate_slashes_between_paths() throws Throwable {
        Object uri = executeUriMethod("/context/", "/relative/path/");
        assertEquals("/context/relative/path/", uri);
    }

    @Test
    public void test_context_path_with_trailing_slash() throws Throwable {
        Object uri = executeUriMethod("/context/", "relative/path");
        assertEquals("/context/relative/path", uri);
    }

    @Test
    public void test_both_paths_are_slashes() throws Throwable {
        Object uri = executeUriMethod("/", "/");
        assertEquals("/", uri);
    }

    @Test
    public void test_empty_context_path() throws Throwable {
        Object uri = executeUriMethod("", "/relative/path");
        assertEquals("/relative/path", uri);
    }

    @Test
    public void test_null_context_path() throws Throwable {
        Object uri = executeUriMethod(null, "/relative/path");
        assertEquals("/relative/path", uri);
    }

    @Test
    public void test_empty_relative_path() throws Throwable {
        Object uri = executeUriMethod("/context", "");
        assertEquals("/context/", uri);
    }

    @Test
    public void test_null_relative_path() throws Throwable {
        Object uri = executeUriMethod("/context", null);
        assertEquals("/context/", uri);
    }

}
