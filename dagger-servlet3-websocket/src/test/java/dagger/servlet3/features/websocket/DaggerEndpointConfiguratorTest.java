package dagger.servlet3.features.websocket;

import org.junit.Test;

import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DaggerEndpointConfiguratorTest {

    @Test
    public void test_store_request_headers_in_the_user_properties_map_during_handshake() {
        ServerEndpointConfig.Configurator configurator = new DaggerEndpointConfigurator(null);
        HandshakeRequest handshakeRequest = createHandshakeRequest();
        ServerEndpointConfig endpointConfig = createServerEndpointConfig();

        configurator.modifyHandshake(endpointConfig, handshakeRequest, null);

        assertSame(handshakeRequest.getHeaders(), endpointConfig.getUserProperties().get(DaggerEndpointConfigurator.REQUEST_HEADERS_KEY));
    }

    private ServerEndpointConfig createServerEndpointConfig() {
        ServerEndpointConfig endpointConfig = mock(ServerEndpointConfig.class);
        Map<String, Object> userProperties = new HashMap<>();
        when(endpointConfig.getUserProperties()).thenReturn(userProperties);
        return endpointConfig;
    }

    private HandshakeRequest createHandshakeRequest() {
        HandshakeRequest handshakeRequest = mock(HandshakeRequest.class);
        Map<String, List<String>> requestHeaders = new HashMap<>();
        when(handshakeRequest.getHeaders()).thenReturn(requestHeaders);
        return handshakeRequest;
    }

}
