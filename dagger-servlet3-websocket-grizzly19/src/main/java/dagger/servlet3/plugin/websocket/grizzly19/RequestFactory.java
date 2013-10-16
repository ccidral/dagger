package dagger.servlet3.plugin.websocket.grizzly19;

import dagger.http.Request;

import javax.servlet.http.HttpServletRequest;

public interface RequestFactory {

    Request createFrom(com.sun.grizzly.tcp.Request grizzlyRequest, String httpMethod);

    Request createWebSocketOpenRequest(HttpServletRequest httpServletRequest);

    Request createWebSocketCloseRequest(HttpServletRequest httpServletRequest);

    Request createWebSocketMessageRequest(HttpServletRequest httpServletRequest, String message);

}
