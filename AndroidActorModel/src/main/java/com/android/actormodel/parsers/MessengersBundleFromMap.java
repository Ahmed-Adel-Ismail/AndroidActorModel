package com.android.actormodel.parsers;

import android.os.Bundle;
import android.os.Messenger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * a function that inserts the {@link Map} of {@link Messenger} objects into a bundle
 * <p>
 * Created by Ahmed Adel Ismail on 8/26/2017.
 */
public class MessengersBundleFromMap {

    public Bundle apply(Map<Class<?>, Messenger> messengers) {
        Bundle bundle = new Bundle(messengers.size());
        if (!messengers.isEmpty()) {
            appendMessengersToBundle(messengers, bundle);
        }
        return bundle;
    }

    private void appendMessengersToBundle(Map<Class<?>, Messenger> messengers, Bundle bundle) {
        for (Entry<String, Messenger> entry : toStringMessengerMap(messengers).entrySet()) {
            bundle.putParcelable(entry.getKey(), entry.getValue());
        }
    }


    private Map<String, Messenger> toStringMessengerMap(Map<Class<?>, Messenger> messengers) {
        Map<String, Messenger> result = new LinkedHashMap<>(messengers.size());
        for (Entry<Class<?>, Messenger> entry : messengers.entrySet()) {
            result.put(entry.getKey().getName(), entry.getValue());
        }
        return result;
    }

}
