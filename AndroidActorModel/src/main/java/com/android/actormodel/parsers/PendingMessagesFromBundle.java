package com.android.actormodel.parsers;

import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;

import com.android.actormodel.queues.PendingMessages;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * a function that converts a {@link Bundle} to {@link PendingMessages}, this bundle should be
 * previously created through {@link PendingMessagesAsBundle}
 * <p>
 * Created by Ahmed Adel Ismail on 8/26/2017.
 */
public class PendingMessagesFromBundle {

    public PendingMessages accept(Bundle bundle) throws ClassNotFoundException {

        if (bundle == null || bundle.isEmpty()) {
            return new PendingMessages(new LinkedHashMap<Class<?>, List<Message>>());
        }

        return new PendingMessages(parseMessageQueue(bundle));
    }

    private Map<Class<?>, List<Message>> parseMessageQueue(Bundle bundle) throws ClassNotFoundException {
        Map<Class<?>, List<Message>> messagesQueue = new LinkedHashMap<>(bundle.size());
        for (String key : bundle.keySet()) {
            messagesQueue.put(Class.forName(key), messagesList(bundle, key));
        }
        return messagesQueue;
    }

    @SuppressWarnings("ConstantConditions")
    private List<Message> messagesList(Bundle bundle, String key) {
        List<Parcelable> parcelables = bundle.getParcelableArrayList(key);
        List<Message> messages = new ArrayList<>(parcelables.size());
        for (Parcelable parcelable : parcelables) {
            messages.add((Message) parcelable);
        }
        return messages;
    }
}
