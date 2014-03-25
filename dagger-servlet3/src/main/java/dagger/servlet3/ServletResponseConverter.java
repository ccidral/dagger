package dagger.servlet3;

import dagger.http.Response;
import dagger.lang.Converter;

import javax.servlet.http.HttpServletResponse;

public class ServletResponseConverter implements Converter<HttpServletResponse, Response> {

    @Override
    public Response convert(HttpServletResponse httpServletResponse) {
        return new DaggerServletResponse(httpServletResponse);
    }

}
