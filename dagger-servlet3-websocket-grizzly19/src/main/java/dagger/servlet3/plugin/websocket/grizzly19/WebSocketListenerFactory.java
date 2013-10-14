package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.WebSocketListener;
import dagger.Module;

import javax.servlet.http.HttpServletRequest;

public interface WebSocketListenerFactory {

    WebSocketListener create(Module module, HttpServletRequest request);

}
