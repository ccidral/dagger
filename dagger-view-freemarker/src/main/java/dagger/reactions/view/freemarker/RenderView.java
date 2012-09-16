package dagger.reactions.view.freemarker;

import dagger.Reaction;
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
    private final Object model;
    private final Configuration configuration;

    public RenderView(String templateName, Object model) {
        this.templateName = templateName;
        this.model = model;
        configuration = new Configuration();

        configuration.setTemplateLoader(new ClasspathTemplateLoader());
        configuration.setObjectWrapper(new DefaultObjectWrapper());
    }

    @Override
    public void execute(Response response) {
        Template template = getTemplate();
        Map<String, Object> modelMap = getModelMap();
        Writer writer = new OutputStreamWriter(response.getOutputStream());

        try {
            template.process(modelMap, writer);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> getModelMap() {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        modelMap.put("model", model);
        return modelMap;
    }

    private Template getTemplate() {
        Template template;
        try {
            template = configuration.getTemplate(templateName + ".ftl");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return template;
    }

    private static class ClasspathTemplateLoader extends URLTemplateLoader {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        protected URL getURL(String templateName) {
            logger.info("Template name: {}", templateName);
            return getClass().getResource("/view/templates/"+templateName);
        }
    }
}
