package com.android.actormodel.messaging;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * the parent class for {@link MessageBuilder} and {@link MessageSender}
 * <p>
 * Created by Ahmed Adel Ismail on 8/8/2017.
 */
class MessageInitializer {

    static final String KEY_SERIALIZABLE = "KEY_SERIALIZABLE";

    protected final Message message;
    protected final Bundle data;

    MessageInitializer(int what) {
        this.data = new Bundle(1);
        this.message = Message.obtain();
        this.message.what = what;
        this.message.setData(data);
    }

    MessageInitializer(Message message, Bundle data) {
        this.message = Message.obtain(message);
        this.data = new Bundle(data);
        this.message.setData(this.data);
    }

    /**
     * add {@link Serializable} serializable to the {@link Message#getData()}
     *
     * @param object the {@link Serializable} {@code Object}
     * @return a new instance with the new properties
     */
    public MessageInitializer serializable(Serializable object) {
        MessageInitializer initializer = new MessageInitializer(message, data);
        initializer.data.putSerializable(KEY_SERIALIZABLE, object);
        return initializer;
    }

    /**
     * add {@link Parcelable} parcelable to the {@link Message#obj}
     *
     * @param object the {@link Parcelable} parcelable
     * @return a new instance with the new properties
     */
    public MessageInitializer parcelable(Parcelable object) {
        MessageInitializer initializer = new MessageInitializer(message, data);
        initializer.message.obj = object;
        return initializer;
    }

    /**
     * add the {@link Messenger} that will be used to reply-to the sent {@link Message}
     *
     * @param replyTo the {@link Messenger} to receive the reply
     * @return a new instance with the new properties
     */
    public MessageInitializer replyTo(Messenger replyTo) {
        MessageInitializer initializer = new MessageInitializer(message, data);
        initializer.message.replyTo = replyTo;
        return initializer;
    }


}
