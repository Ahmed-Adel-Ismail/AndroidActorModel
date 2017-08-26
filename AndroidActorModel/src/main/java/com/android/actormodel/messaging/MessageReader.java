package com.android.actormodel.messaging;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * a class that reads a {@link Message} that was constructed through
 * {@link MessageInitializer} sub-classes, like {@link MessageBuilder} or
 * {@link MessageSender}
 * <p>
 * Created by Ahmed Adel Ismail on 8/8/2017.
 */
public class MessageReader {

    private final int what;
    private final Serializable data;
    private final Parcelable object;
    private final Messenger replyTo;

    private MessageReader(Message message) {
        what = message.what;
        object = (Parcelable) message.obj;
        replyTo = message.replyTo;
        Bundle bundle = message.getData();
        if (bundle != null && bundle.containsKey(MessageInitializer.KEY_SERIALIZABLE)) {
            data = bundle.getSerializable(MessageInitializer.KEY_SERIALIZABLE);
        } else {
            data = null;
        }
    }

    /**
     * create a {@link MessageReader} for the passed {@link Message}
     * that was prepared through a {@link MessageSender} or
     * {@link MessageBuilder}
     *
     * @param message the {@link Message} to be read
     * @return a {@link MessageReader} instance hoding the read message
     */
    public static MessageReader with(Message message) {
        return new MessageReader(message);
    }

    /**
     * create a {@link MessageReader} with the passed {@link Message}, and invoke it's
     * {@link #getSerializable()} method
     *
     * @param message the {@link Message} to check for
     * @param <T>     the expected return type which is an implementer of {@link Serializable}
     * @return the value casted to the expected return type
     * @throws ClassCastException if the expected type mismatches with the actual type
     */
    public static <T extends Serializable> T serializable(Message message)
            throws ClassCastException {
        return new MessageReader(message).getSerializable();
    }

    /**
     * create a {@link MessageReader} with the passed {@link Message}, and invoke it's
     * {@link #getParcelable()} method
     *
     * @param message the {@link Message} to check for
     * @param <T>     the expected return type which is an implementer of {@link Parcelable}
     * @return the value casted to the expected return type
     * @throws ClassCastException if the expected type mismatches with the actual type
     */
    public static <T extends Parcelable> T parcelable(Message message)
            throws ClassCastException {
        return new MessageReader(message).getParcelable();
    }

    /**
     * create a {@link MessageReader} with the passed {@link Message}, and invoke it's
     * {@link #getReplyTo()} method
     *
     * @param message the {@link Message} to check for
     * @return the {@link Messenger} to reply to (if exists), or {@code null} if non available
     */
    public static Messenger replyTo(Message message) {
        return new MessageReader(message).getReplyTo();
    }

    public int getWhat() {
        return what;
    }

    /**
     * check if the {@link #getWhat()} value is the same as the passed value
     *
     * @param otherWhat the {@link Message#what} of the other {@link Message}
     * @return {@code true} if the value is the same, else {@code false}
     */
    public boolean isWhat(int otherWhat) {
        return what == otherWhat;
    }

    /**
     * get the {@link Serializable} value that was set using
     * {@link MessageBuilder#serializable(Serializable)} or
     * {@link MessageSender#serializable(Serializable)}
     *
     * @param <T> the expected return type which is an implementer of {@link Serializable}
     * @return the value casted to the expected return type
     * @throws ClassCastException if the expected type mismatches with the actual type
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getSerializable() throws ClassCastException {
        return (T) data;
    }

    /**
     * get the {@link Parcelable} value that was set using
     * {@link MessageBuilder#parcelable(Parcelable)} or
     * {@link MessageSender#parcelable(Parcelable)}
     *
     * @param <T> the expected return type which is an implementer of {@link Parcelable}
     * @return the value casted to the expected return type
     * @throws ClassCastException if the expected type mismatches with the actual type
     */
    @SuppressWarnings("unchecked")
    public <T extends Parcelable> T getParcelable() throws ClassCastException {
        return (T) object;
    }

    /**
     * get the {@link Messenger} that was set through {@link MessageBuilder#replyTo(Messenger)}
     * or {@link MessageSender#replyTo(Messenger)}, or the {@link Messenger} in the address
     * passed to {@link MessageSender#replyTo(Class)}
     *
     * @return a {@link Messenger} if available, or {@code null}
     */
    public Messenger getReplyTo() {
        return replyTo;
    }

    @Override
    public String toString() {
        return "MessageReader{" +
                "what=" + what +
                ", data=" + data +
                ", object=" + object +
                ", replyTo=" + replyTo +
                '}';
    }
}
