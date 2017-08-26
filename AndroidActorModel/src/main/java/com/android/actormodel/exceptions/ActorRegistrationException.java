package com.android.actormodel.exceptions;

import com.android.actormodel.ActorSystem;

/**
 * a {@link RuntimeException} that is thrown when the registration operation fails in
 * {@link ActorSystem}
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
public class ActorRegistrationException extends RuntimeException {

    public ActorRegistrationException(String message) {
        super(message);
    }

    public ActorRegistrationException(Throwable cause) {
        super(cause);
    }
}
