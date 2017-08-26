package com.android.actormodel.integration;

import android.app.Application;
import android.app.Service;
import android.content.ServiceConnection;
import android.os.Message;

import com.android.actormodel.commands.Command;

import java.util.Map;

/**
 * a function that un-registers (or unbinds a) {@link Service}
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
class ServiceUnRegistration implements Command<Message> {

    private final Application application;
    private final Map<Class<? extends Service>, ServiceConnection> connections;

    ServiceUnRegistration(Application application,
                          Map<Class<? extends Service>, ServiceConnection> connections) {
        this.application = application;
        this.connections = connections;
    }

    @SuppressWarnings("unchecked")
    public void execute(Message message) throws Exception {
        Class<? extends Service> serviceClass;
        serviceClass = (Class<? extends Service>) message.getData().getSerializable(ActorsIntegrationService.KEY_ADDRESS);
        if (connections.containsKey(serviceClass)) {
            application.unbindService(connections.get(serviceClass));
        }
    }

}
