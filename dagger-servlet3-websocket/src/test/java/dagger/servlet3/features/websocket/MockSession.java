package dagger.servlet3.features.websocket;

import dagger.DaggerRuntimeException;
import dagger.lang.NotImplementedYet;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class MockSession implements Session {

    private final List<MessageHandler> messageHandlers = new ArrayList<>();
    private final Map<String, Object> userProperties = new HashMap<>();
    private boolean isClosed;

    public void fireOnMessageEvent(String message) {
        for(MessageHandler messageHandler : messageHandlers) {
            MessageHandler.Whole wholeMessageHandler = (MessageHandler.Whole) messageHandler;
            wholeMessageHandler.onMessage(message);
        }
    }

    public MockSession assertClosed() {
        assertEquals("Is session closed?", true, isClosed);
        return this;
    }

    public MockSession assertNoMessageHandlerIsAdded() {
        assertEquals("Number of message handlers", 0, messageHandlers.size());
        return this;
    }

    @Override
    public void addMessageHandler(MessageHandler messageHandler) throws IllegalStateException {
        if(!(messageHandler instanceof MessageHandler.Whole))
            throw new DaggerRuntimeException("Only "+MessageHandler.Whole.class.getName()+" is allowed");
        messageHandlers.add(messageHandler);
    }

    @Override
    public WebSocketContainer getContainer() {
        throw new NotImplementedYet();
    }

    @Override
    public Set<MessageHandler> getMessageHandlers() {
        return new HashSet<>(messageHandlers);
    }

    @Override
    public void removeMessageHandler(MessageHandler messageHandler) {
        messageHandlers.remove(messageHandler);
    }

    @Override
    public void close() throws IOException {
        close(null);
    }

    @Override
    public void close(CloseReason closeReason) throws IOException {
        isClosed = true;
    }

    @Override
    public String getProtocolVersion() {
        throw new NotImplementedYet();
    }

    @Override
    public String getNegotiatedSubprotocol() {
        throw new NotImplementedYet();
    }

    @Override
    public List<Extension> getNegotiatedExtensions() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isSecure() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isOpen() {
        throw new NotImplementedYet();
    }

    @Override
    public long getMaxIdleTimeout() {
        throw new NotImplementedYet();
    }

    @Override
    public void setMaxIdleTimeout(long l) {
        throw new NotImplementedYet();
    }

    @Override
    public void setMaxBinaryMessageBufferSize(int i) {
        throw new NotImplementedYet();
    }

    @Override
    public int getMaxBinaryMessageBufferSize() {
        throw new NotImplementedYet();
    }

    @Override
    public void setMaxTextMessageBufferSize(int i) {
        throw new NotImplementedYet();
    }

    @Override
    public int getMaxTextMessageBufferSize() {
        throw new NotImplementedYet();
    }

    @Override
    public RemoteEndpoint.Async getAsyncRemote() {
        throw new NotImplementedYet();
    }

    @Override
    public RemoteEndpoint.Basic getBasicRemote() {
        throw new NotImplementedYet();
    }

    @Override
    public String getId() {
        throw new NotImplementedYet();
    }

    @Override
    public URI getRequestURI() {
        throw new NotImplementedYet();
    }

    @Override
    public Map<String, List<String>> getRequestParameterMap() {
        throw new NotImplementedYet();
    }

    @Override
    public String getQueryString() {
        throw new NotImplementedYet();
    }

    @Override
    public Map<String, String> getPathParameters() {
        throw new NotImplementedYet();
    }

    @Override
    public Map<String, Object> getUserProperties() {
        return userProperties;
    }

    @Override
    public Principal getUserPrincipal() {
        throw new NotImplementedYet();
    }

    @Override
    public Set<Session> getOpenSessions() {
        throw new NotImplementedYet();
    }

}
