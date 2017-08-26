package com.android.actormodel.parsers;

import android.os.Bundle;
import android.os.Messenger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * a function that reverts back the {@link Messenger} instances stored in a bundle through a
 * {@link MessengersBundleFromMap}
 * <p>
 * Created by Ahmed Adel Ismail on 8/26/2017.
 */
public class MessengersMapFromBundle {

    public Map<Class<?>, Messenger> apply(Bundle bundle) throws ClassNotFoundException {

        if (bundle == null || bundle.isEmpty()) {
            return new LinkedHashMap<>();
        }

        return messengersMap(bundle);
    }

    private Map<Class<?>, Messenger> messengersMap(Bundle bundle) throws ClassNotFoundException {
        Map<Class<?>, Messenger> result = new LinkedHashMap<>(bundle.size());
        for (String key : bundle.keySet()) {
            result.put(Class.forName(key), (Messenger) bundle.getParcelable(key));
        }
        return result;
    }

}
