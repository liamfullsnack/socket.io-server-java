/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.liamfullsnack.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;

/**
 *
 * @author liam
 */
public abstract class FConnectEventListener implements ConnectListener {

    private final FSocketIOServer server;

    public FConnectEventListener(FSocketIOServer fsios) {
        this.server = fsios;
    }

    @Override
    public abstract void onConnect(SocketIOClient sioc);

    public FSocketIOServer getServer() {
        return server;
    }

}
