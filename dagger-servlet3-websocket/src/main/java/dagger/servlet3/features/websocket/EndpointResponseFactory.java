package dagger.servlet3.features.websocket;

import dagger.http.Response;

import javax.websocket.Session;

public interface EndpointResponseFactory {

    Response create(Session session);

}
