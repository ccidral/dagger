package dagger.servlet3;

import dagger.http.Request;
import dagger.lang.Converter;

import javax.servlet.http.HttpServletRequest;

public class ServletRequestConverter implements Converter<HttpServletRequest, Request> {

    @Override
    public Request convert(HttpServletRequest httpServletRequest) {
        return new DaggerServletRequest(httpServletRequest);
    }

}
