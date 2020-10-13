/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.liamfullsnack.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DisconnectListener;

/**
 *
 * @author liam
 */
public abstract class FDisconnectEventListener implements DisconnectListener {

    private final FSocketIOServer server;

    public FDisconnectEventListener(FSocketIOServer fsios) {
        this.server = fsios;
    }

    @Override
    public abstract void onDisconnect(SocketIOClient sioc);

    public FSocketIOServer getServer() {
        return server;
    }

}
