package com.android.actormodel.integration;

import android.os.Message;
import android.os.Messenger;

import com.android.actormodel.queues.PendingMessages;
import com.android.actormodel.commands.Command;

import java.util.Map;

import static com.android.actormodel.integration.ActorsIntegrationService.KEY_ADDRESS;
import static com.android.actormodel.integration.ActorsIntegrationService.KEY_REPLY_TO_ADDRESS;

/**
 * a class that is responsible to send {@link Message} objects between {@link Messenger Messengers}
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
public class ServiceMessageSender implements Command<Message> {

    private final Map<Class<?>, Messenger> messengers;
    private final PendingMessages pendingMessages;

    public ServiceMessageSender(Map<Class<?>, Messenger> messengers, PendingMessages pendingMessages) {
        this.messengers = messengers;
        this.pendingMessages = pendingMessages;
    }

    @Override
    public void execute(Message message) throws Exception {
        Class<?> address = (Class<?>) message.getData().getSerializable(KEY_ADDRESS);
        if (messengers.containsKey(address)) {
            messengers.get(address).send(messageToDeliver(message));
        } else {
            pendingMessages.queue(address, Message.obtain(message));
        }
    }

    private Message messageToDeliver(Message message) {
        Message messageToDeliver = (Message) message.obj;
        if (message.replyTo == null) {
            messageToDeliver = updateMessageReplyToByAddress(message, messageToDeliver);
        }
        return messageToDeliver;
    }

    private Message updateMessageReplyToByAddress(Message message, Message messageToDeliver) {
        Class<?> replyToAddress = retrieveReplyToMessenger(message);
        if (replyToAddress != null && messengers.containsKey(replyToAddress)) {
            messageToDeliver.replyTo = messengers.get(replyToAddress);
        }
        return messageToDeliver;
    }

    private Class<?> retrieveReplyToMessenger(Message message) {
        if (message.getData() != null && message.getData().containsKey(KEY_REPLY_TO_ADDRESS)) {
            return (Class<?>) message.getData().getSerializable(KEY_REPLY_TO_ADDRESS);
        }
        return null;
    }
}
