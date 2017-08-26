package com.android.actormodel.integration;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.android.actormodel.commands.Command;
import com.android.actormodel.parsers.MessengersBundleFromMap;
import com.android.actormodel.parsers.PendingMessagesAsBundle;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.android.actormodel.ActorService.KEY_EXTRA_MESSENGERS;
import static com.android.actormodel.ActorService.KEY_EXTRA_PENDING_MESSAGES;
import static com.android.actormodel.ActorService.REGISTER_ACTOR_SERVICE_COMPLETE;

/**
 * a function that registers (or binds to a) {@link Service}
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
class ServiceRegistration implements Command<Message> {

    private final Application application;
    private final Cache cache;

    ServiceRegistration(Application application, Cache cache) {
        this.application = application;
        this.cache = cache;
    }

    @SuppressWarnings("unchecked")
    public void execute(Message message) throws Exception {
        Class<? extends Service> serviceClass;
        serviceClass = (Class<? extends Service>) message.getData().getSerializable(ActorsIntegrationService.KEY_ADDRESS);
        Intent intent = new Intent(application, serviceClass);
        application.bindService(intent, serviceConnection(serviceClass), BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection(final Class<? extends Service> serviceClass) {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Messenger messenger = new Messenger(service);
                cache.messengers.put(serviceClass, messenger);
                cache.names.put(name, serviceClass);
                cache.connections.put(serviceClass, this);
                Message message = Message.obtain();
                message.what = REGISTER_ACTOR_SERVICE_COMPLETE;
                message.setData(onRegisterBundle());
                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e("ServiceRegistration", "onServiceDisconnected() : " + name);
                Class<? extends Service> serviceClass = cache.names.get(name);
                cache.names.remove(name);
                cache.messengers.remove(serviceClass);
                cache.connections.remove(serviceClass);
            }
        };
    }

    private Bundle onRegisterBundle() {

        Bundle bundle = new Bundle(2);

        bundle.putBundle(KEY_EXTRA_MESSENGERS,
                new MessengersBundleFromMap().apply(cache.messengers));

        bundle.putBundle(KEY_EXTRA_PENDING_MESSAGES,
                new PendingMessagesAsBundle().apply(cache.pendingMessages.messagesQueue));

        return bundle;
    }

}
