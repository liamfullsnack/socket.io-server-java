package io.github.liamfullsnack.socketio.example;

import com.corundumstudio.socketio.SocketIOClient;
import io.github.liamfullsnack.socketio.FAEvent;
import io.github.liamfullsnack.socketio.FDisconnectEventListener;
import io.github.liamfullsnack.socketio.FSocketIOServer;
import java.util.logging.Logger;

@FAEvent(servers = {"foo-server", "bar-server"})
public class OnDisconnectListener extends FDisconnectEventListener {

    static final Logger LOGGER = Logger.getLogger(OnDisconnectListener.class.getName());

    public OnDisconnectListener(FSocketIOServer fsios) {
        super(fsios);
    }

    @Override
    public void onDisconnect(SocketIOClient client) {
        LOGGER.info(client.getSessionId().toString() + " disconnected from server " + getServer().getName());
    }

}
