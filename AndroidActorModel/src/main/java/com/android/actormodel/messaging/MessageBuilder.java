package com.android.actormodel.messaging;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * a class that builds a {@link Message}, that can be parsed later through {@link MessageReader}
 * <p>
 * Created by Ahmed Adel Ismail on 8/8/2017.
 */
public class MessageBuilder extends MessageInitializer {

    private MessageBuilder(int what) {
        super(what);
    }

    private MessageBuilder(Message message, Bundle data) {
        super(message, data);
    }

    /**
     * prepare a {@link Message} with the passed {@code what}
     *
     * @param what the {@link Message#what}
     * @return a {@link MessageBuilder}
     */
    public static MessageBuilder prepareMessage(int what) {
        return new MessageBuilder(what);
    }

    @Override
    public MessageBuilder serializable(Serializable object) {
        MessageBuilder builder = new MessageBuilder(message, data);
        builder.data.putSerializable(KEY_SERIALIZABLE, object);
        return builder;
    }

    @Override
    public MessageBuilder parcelable(Parcelable object) {
        MessageBuilder builder = new MessageBuilder(message, data);
        builder.message.obj = object;
        return builder;
    }

    @Override
    public MessageBuilder replyTo(Messenger replyTo) {
        MessageBuilder builder = new MessageBuilder(message, data);
        builder.message.replyTo = replyTo;
        return builder;
    }

    /**
     * create the {@link Message}
     *
     * @return the {@link Message} to be used
     */
    public Message build() {
        return new MessageBuilder(message, data).message;
    }

}
