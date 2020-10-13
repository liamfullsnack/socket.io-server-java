package io.github.liamfullsnack.socketio.example;

import io.github.liamfullsnack.socketio.FSocketIOServer;

public class MainApplication {

    static final String PACKAGE_TO_SCAN = "io.github";

    public static void main(String[] arg) throws Exception {
        FSocketIOServer fooServer = new FSocketIOServer("foo-server", 11111, "/foo", PACKAGE_TO_SCAN);
        fooServer.start();

        FSocketIOServer barServer = new FSocketIOServer("bar-server", 11112, "/bar", PACKAGE_TO_SCAN);
        barServer.start();
        
    }
}
