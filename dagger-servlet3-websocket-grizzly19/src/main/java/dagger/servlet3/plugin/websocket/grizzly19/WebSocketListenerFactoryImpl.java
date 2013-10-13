package dagger.servlet3.plugin.websocket.grizzly19;

import com.sun.grizzly.websockets.WebSocketListener;
import dagger.Module;
import dagger.lang.NotImplementedYet;

import javax.servlet.http.HttpServletRequest;

public class WebSocketListenerFactoryImpl implements WebSocketListenerFactory {

    @Override
    public WebSocketListener create(Module module, HttpServletRequest request) {
        throw new NotImplementedYet();
    }

}
