package com.android.actormodel.integration;

import android.app.Service;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Message;
import android.os.Messenger;

import com.android.actormodel.queues.PendingMessages;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * a serializable class that holds the memory-cache maps
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
class Cache implements Serializable {

    final LinkedHashMap<Class<?>, Messenger> messengers;
    final LinkedHashMap<Class<? extends Service>, ServiceConnection> connections;
    final LinkedHashMap<ComponentName, Class<? extends Service>> names;
    final PendingMessages pendingMessages;

    Cache() {
        messengers = new LinkedHashMap<>();
        connections = new LinkedHashMap<>();
        names = new LinkedHashMap<>();
        pendingMessages = new PendingMessages(new LinkedHashMap<Class<?>, List<Message>>());
    }

    @Override
    public String toString() {
        return "Cache{" +
                "messengers=" + messengers +
                ", connections=" + connections +
                ", names=" + names +
                ", pendingMessages=" + pendingMessages +
                '}';
    }

    final void clear() {
        pendingMessages.clear();
        messengers.clear();
        connections.clear();
        names.clear();
    }
}
