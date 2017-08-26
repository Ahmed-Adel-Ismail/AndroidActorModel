package com.android.actormodel.parsers;

import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;

import com.android.actormodel.queues.PendingMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * a function that converts {@link PendingMessages} to a {@link Bundle}
 * <p>
 * Created by Ahmed Adel Ismail on 8/26/2017.
 */
public class PendingMessagesAsBundle {

    public Bundle apply(Map<Class<?>, List<Message>> pendingMessages) {
        Bundle bundle = new Bundle(pendingMessages.size());
        if (!pendingMessages.isEmpty()) {
            appendAllToBundle(pendingMessages, bundle);
        }
        return bundle;
    }

    private void appendAllToBundle(Map<Class<?>, List<Message>> pendingMessages, Bundle bundle) {
        for (Entry<Class<?>, List<Message>> entry : pendingMessages.entrySet()) {
            appendMessageListToBundle(bundle, entry);
        }
    }

    private void appendMessageListToBundle(Bundle bundle, Entry<Class<?>, List<Message>> entry) {
        String key = entry.getKey().getName();
        ArrayList<? extends Parcelable> messages = new ArrayList<>(entry.getValue());
        bundle.putParcelableArrayList(key, messages);
    }

}
