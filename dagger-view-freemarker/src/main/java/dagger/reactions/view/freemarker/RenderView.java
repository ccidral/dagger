package dagger.reactions.view.freemarker;

import dagger.Reaction;
import dagger.http.HttpHeaderNames;
import dagger.http.Request;
import dagger.http.Response;
import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RenderView implements Reaction {

    private final String templateName;
    private final String contentType;
    private final Object model;
    private final Configuration configuration;

    public RenderView(String templateName, String contentType, Object model) {
        this.templateName = templateName;
        this.contentType = contentType;
        this.model = model;

        configuration = new Configuration();
        configuration.setTemplateLoader(new ClasspathTemplateLoader());
        configuration.setObjectWrapper(new DefaultObjectWrapper());
    }

    @Override
    public void execute(Request request, Response response) throws Exception {
        response.setHeader(HttpHeaderNames.CONTENT_TYPE, contentType);
        renderView(response);
    }

    private void renderView(Response response) throws TemplateException, IOException {
        Template template = loadTemplate();
        Map<String, Object> modelMap = createModelMap();
        Writer writer = new OutputStreamWriter(response.getOutputStream());
        template.process(modelMap, writer);
        writer.flush();
    }

    private Template loadTemplate() throws IOException {
        return configuration.getTemplate(templateName + ".ftl");
    }

    private Map<String, Object> createModelMap() {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("model", model);
        return modelMap;
    }

    private static class ClasspathTemplateLoader extends URLTemplateLoader {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        protected URL getURL(String templateName) {
            logger.debug("Template name: {}", templateName);
            return getClass().getResource("/view/templates/"+templateName);
        }

    }

}
