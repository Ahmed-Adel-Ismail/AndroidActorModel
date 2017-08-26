package com.android.actormodel.messaging;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;

import com.android.actormodel.exceptions.ActorMessagingException;
import com.android.actormodel.integration.ActorsIntegrationService;

import java.io.Serializable;

/**
 * a class that creates the {@link Message} that is sent across the Actor System, and it is
 * read through {@link MessageReader}
 * <p>
 * Created by Ahmed Adel Ismail on 8/8/2017.
 */
public class MessageSender extends MessageInitializer {

    private final Messenger actorServiceMessenger;
    private final Class<?> address;
    private final Class<?> replyToAddress;


    private MessageSender(int what, Messenger actorServiceMessenger,
                          Class<?> address, Class<?> replyToAddress) {
        super(what);
        this.actorServiceMessenger = actorServiceMessenger;
        this.address = address;
        this.replyToAddress = replyToAddress;

    }

    private MessageSender(Message message, Bundle data, Messenger actorServiceMessenger,
                          Class<?> address, Class<?> replyToAddress) {
        super(message, data);
        this.actorServiceMessenger = actorServiceMessenger;
        this.address = address;
        this.replyToAddress = replyToAddress;
    }

    /**
     * prepare a {@link Message} to be sent via {@link MessageSender} ... this method is intended
     * to be used internally
     *
     * @param what                  the {@link Message#what}
     * @param actorServiceMessenger the {@link Messenger} of the {@link ActorsIntegrationService}
     * @param address               the {@link Class} address of the Actor that will receive the message
     * @param replyToAddress        the {@link Class} address of the current Actor to receive replies from
     *                              the receiver Actor
     * @return a {@link MessageSender} to help building and sending a {@link Message}
     */
    public static MessageSender prepareMessage(int what, Messenger actorServiceMessenger,
                                               Class<?> address, Class<?> replyToAddress) {
        return new MessageSender(what, actorServiceMessenger, address, replyToAddress);
    }

    @Override
    public MessageSender serializable(Serializable object) {
        MessageSender sender = new MessageSender(message, data, actorServiceMessenger
                , address, replyToAddress);
        sender.data.putSerializable(KEY_SERIALIZABLE, object);
        return sender;
    }

    @Override
    public MessageSender parcelable(Parcelable object) {
        MessageSender sender = new MessageSender(message, data, actorServiceMessenger
                , address, replyToAddress);
        sender.message.obj = object;
        return sender;
    }

    /**
     * let the receiver reply to the Actor in the passed address, if the {@link Message#replyTo}
     * already has a value, this method will do nothing
     *
     * @param replyToAddress the {@link Class} indicating the address of the Actor waiting for reply
     * @return a new instance with the new properties
     */
    public MessageSender replyTo(Class<?> replyToAddress) {
        return new MessageSender(message, data, actorServiceMessenger, address, replyToAddress);
    }

    /**
     * add the {@link Messenger} that will be used to reply-to the sent {@link Message} through
     * {@link Message#replyTo}, this value will override the {@link #replyTo(Class)} value
     *
     * @param replyTo the {@link Messenger} to receive the reply
     * @return a new instance with the new properties
     */
    @Override
    public MessageSender replyTo(Messenger replyTo) {
        MessageSender sender = new MessageSender(message, data, actorServiceMessenger
                , address, replyToAddress);
        sender.message.replyTo = replyTo;
        return sender;
    }

    /**
     * send the {@link Message} to the Actor that started this chain of calls
     *
     * @throws ActorMessagingException if the operation failed
     */
    public void send() throws ActorMessagingException {
        Message newMessage = Message.obtain();
        newMessage.what = ActorsIntegrationService.SEND_MESSAGE;
        newMessage.obj = message;
        newMessage.setData(bundle(address));
        try {
            actorServiceMessenger.send(newMessage);
        } catch (Throwable e) {
            throw new ActorMessagingException(e);
        }
    }

    private Bundle bundle(Class<?> address) {
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(ActorsIntegrationService.KEY_ADDRESS, address);
        if (replyToAddress != null) {
            bundle.putSerializable(ActorsIntegrationService.KEY_REPLY_TO_ADDRESS, replyToAddress);
        }
        return bundle;
    }
}
