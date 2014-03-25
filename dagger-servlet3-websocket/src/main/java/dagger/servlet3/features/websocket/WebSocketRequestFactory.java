package dagger.servlet3.features.websocket;

import dagger.http.Request;

import javax.websocket.Session;

public interface WebSocketRequestFactory {

    Request create(String httpMethod, String requestBody, Session session);

}
