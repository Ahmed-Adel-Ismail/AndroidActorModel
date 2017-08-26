package com.android.actormodel.exceptions;

import android.os.Message;

import com.android.actormodel.ActorSystem;

/**
 * a {@link RuntimeException} that is thrown if the {@link ActorSystem} failed to send a
 * {@link Message}
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
public class ActorMessagingException extends RuntimeException {

    public ActorMessagingException(String message) {
        super(message);
    }

    public ActorMessagingException(Throwable cause) {
        super(cause);
    }
}
