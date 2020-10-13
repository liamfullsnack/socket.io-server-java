/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.liamfullsnack.socketio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

/**
 *
 * @author liam
 * @param <T>
 */
public abstract class FEventListener<T extends FEventMessage> implements DataListener<T> {

    private final FSocketIOServer server;

    public FEventListener(FSocketIOServer fsios) {
        this.server = fsios;
    }

    @Override
    public abstract void onData(SocketIOClient sioc, T t, AckRequest ar) throws Exception;

    public SocketIOServer getServer() {
        return server.getServer();
    }

}
