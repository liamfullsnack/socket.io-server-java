package io.github.liamfullsnack.socketio.example;

import com.corundumstudio.socketio.SocketIOClient;
import io.github.liamfullsnack.socketio.FAEvent;
import io.github.liamfullsnack.socketio.FConnectEventListener;
import io.github.liamfullsnack.socketio.FSocketIOServer;
import java.util.logging.Logger;

@FAEvent(servers = {"foo-server", "bar-server"})
public class OnConnectListener extends FConnectEventListener {

    static final Logger LOGGER = Logger.getLogger(OnConnectListener.class.getName());

    public OnConnectListener(FSocketIOServer fsios) {
        super(fsios);
    }

    @Override
    public void onConnect(SocketIOClient client) {
        LOGGER.info(client.getSessionId().toString() + " connected to server " + getServer().getName());
    }

}
