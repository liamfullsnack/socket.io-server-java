/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.liamfullsnack.socketio;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reflections.Reflections;

/**
 *
 * @author liam
 */
public class FSocketIOServer {

    static final Logger LOGGER = Logger.getLogger(FSocketIOServer.class.getName());

    private SocketIOServer socketIOServer;

    private String name;
    private int port;
    private String context;
    private boolean ssl;
    private String sslFilePath;
    private String sslKeyPass;
    private String packageToScanListeners;

    private FSocketIOServer() {
    }

    public FSocketIOServer(String name, int port, String context, String packageToScanListeners) {
        this.name = name;
        this.port = port;
        this.context = context;
        if (packageToScanListeners == null || packageToScanListeners.isEmpty()) {
            throw new RuntimeException("You must provide package name for scanning event listeners !");
        }
        this.packageToScanListeners = packageToScanListeners;
    }

    public void setSSL(String sslFilePath, String sslKeyPass) {
        this.ssl = true;
        this.sslFilePath = sslFilePath;
        this.sslKeyPass = sslKeyPass;
    }

    public void start() {

        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        socketConfig.setTcpKeepAlive(true);

        Configuration configuration = new Configuration();
        configuration.setHostname("0.0.0.0");
        configuration.setPort(port);
        configuration.setContext(context);
        configuration.setTransports(Transport.WEBSOCKET);

        configuration.setSocketConfig(socketConfig);
        configuration.setPingTimeout(30000);
        configuration.setPingInterval(10000);

        if (ssl) {
            try {
                configuration.setKeyStore(new FileInputStream(sslFilePath));
                configuration.setKeyStorePassword(sslKeyPass);
            } catch (FileNotFoundException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        socketIOServer = new SocketIOServer(configuration);
        registerEventListeners();
        socketIOServer.start();

        registerShutdownHook();

        LOGGER.log(Level.INFO, "Server {0} started at 0.0.0.0:{1}{2}", new Object[]{name, String.valueOf(port), context});
    }

    private void registerEventListeners() {
        Reflections reflections = new Reflections(packageToScanListeners);
        registerConnectAndDisconnectListeners(reflections);
        registerEventListeners(reflections);
    }

    private void registerConnectAndDisconnectListeners(Reflections reflections) {
        Set<Class<? extends FConnectEventListener>> onConnectListenerClasses = reflections.getSubTypesOf(FConnectEventListener.class);
        onConnectListenerClasses.forEach((Class<? extends FConnectEventListener> cls) -> {
            FAEvent fAEvent = (FAEvent) cls.getAnnotation(FAEvent.class);
            List<String> serverNames = (fAEvent != null && fAEvent.servers() != null && fAEvent.servers().length > 0)
                    ? Arrays.asList(fAEvent.servers()) : new ArrayList<>();
            if (serverNames.contains(this.name)) {
                try {
                    socketIOServer.addConnectListener((FConnectEventListener) cls.getDeclaredConstructor(FSocketIOServer.class).newInstance(this));
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        });

        Set<Class<? extends FDisconnectEventListener>> onDisconnectListenerClasses = reflections.getSubTypesOf(FDisconnectEventListener.class);
        onDisconnectListenerClasses.forEach((Class<? extends FDisconnectEventListener> cls) -> {
            FAEvent fAEvent = (FAEvent) cls.getAnnotation(FAEvent.class);
            List<String> serverNames = (fAEvent != null && fAEvent.servers() != null && fAEvent.servers().length > 0)
                    ? Arrays.asList(fAEvent.servers()) : new ArrayList<>();
            if (serverNames.contains(this.name)) {
                try {
                    socketIOServer.addDisconnectListener((FDisconnectEventListener) cls.getDeclaredConstructor(FSocketIOServer.class).newInstance(this));
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void registerEventListeners(Reflections reflections) {

        Set<Class<? extends FEventListener>> listenerClasses = reflections.getSubTypesOf(FEventListener.class);

        listenerClasses.forEach((Class<? extends FEventListener> cls) -> {
            FAEvent fAEvent = (FAEvent) cls.getAnnotation(FAEvent.class);
            List<String> serverNames = (fAEvent != null && fAEvent.servers() != null && fAEvent.servers().length > 0)
                    ? Arrays.asList(fAEvent.servers()) : new ArrayList<>();
            if (serverNames.contains(this.name)) {
                Type genericSuperClass = cls.getGenericSuperclass();
                if (genericSuperClass instanceof ParameterizedType) {
                    try {
                        Type[] genericTypes = ((ParameterizedType) genericSuperClass).getActualTypeArguments();
                        Class csMessageClass = (Class) genericTypes[0];
                        FEventMessage eventMessage = (FEventMessage) csMessageClass.newInstance();
                        String eventName = ((FEventMessage) csMessageClass.newInstance()).getEvent();
                        socketIOServer.addEventListener(eventName,
                                eventMessage.getClass(),
                                (FEventListener) cls.getDeclaredConstructor(FSocketIOServer.class).newInstance(this));
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                socketIOServer.stop();
            }
        });
    }

    public void stop() {
        if (socketIOServer != null) {
            socketIOServer.stop();
        }
    }

    public String getName() {
        return name;
    }

    public SocketIOServer getServer() {
        return socketIOServer;
    }

}
