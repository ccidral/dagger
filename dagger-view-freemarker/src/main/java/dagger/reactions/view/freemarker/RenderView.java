package dagger.reactions.view.freemarker;

import dagger.Reaction;
import dagger.http.HttpHeader;
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
        this.configuration = createConfiguration();
    }

    private static Configuration createConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setTemplateLoader(new ClasspathTemplateLoader());
        configuration.setObjectWrapper(new DefaultObjectWrapper());
        return configuration;
    }

    @Override
    public void execute(Request request, Response response) throws Exception {
        response.setHeader(HttpHeader.CONTENT_TYPE, contentType);
        renderView(request, response);
    }

    private void renderView(Request request, Response response) throws TemplateException, IOException {
        Template template = loadTemplate();
        Map<String, Object> rootModel = createRootModel(request);
        Writer writer = new OutputStreamWriter(response.getOutputStream());
        template.process(rootModel, writer);
        writer.flush();
    }

    private Template loadTemplate() throws IOException {
        return configuration.getTemplate(templateName + ".ftl");
    }

    private Map<String, Object> createRootModel(Request request) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("model", model);
        map.put("request", createRequestModel(request));
        map.put("uri", new UriMethod(request.getContextPath()));
        return map;
    }

    private Map<String, Object> createRequestModel(Request request) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("contextPath", request.getContextPath());
        map.put("uri", request.getURI());
        return map;
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
