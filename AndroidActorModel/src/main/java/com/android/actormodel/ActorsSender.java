package com.android.actormodel;

import android.os.Message;
import android.os.Messenger;

import com.android.actormodel.messaging.MessageSender;

/**
 * a class responsible for sending {@link Message} to Actprs
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
public class ActorsSender {

    private final Class<?> address;
    private final Messenger actorServiceMessenger;

    ActorsSender(Messenger actorServiceMessenger, Class<?> address) {
        this.actorServiceMessenger = actorServiceMessenger;
        this.address = address;
    }

    public MessageSender prepareMessage(int what) {
        return MessageSender.prepareMessage(what, actorServiceMessenger, address, null);
    }


}
