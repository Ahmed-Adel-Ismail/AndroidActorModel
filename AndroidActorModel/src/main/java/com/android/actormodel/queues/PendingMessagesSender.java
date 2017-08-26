package com.android.actormodel.queues;

import android.os.Message;
import android.os.Messenger;

import com.android.actormodel.commands.Command;
import com.android.actormodel.integration.ServiceMessageSender;

import java.util.Map;

/**
 * a function responsible for sending pending messages
 * <p>
 * Created by Ahmed Adel Ismail on 8/12/2017.
 */
public class PendingMessagesSender implements Command<Class<?>> {

    private final Map<Class<?>, Messenger> messengers;
    private final PendingMessages pendingMessages;

    public PendingMessagesSender(Map<Class<?>, Messenger> messengers, PendingMessages pendingMessages) {
        this.messengers = messengers;
        this.pendingMessages = pendingMessages;
    }

    @Override
    public void execute(Class<?> address) {
        for (Message pendingMessage : pendingMessages.consume(address)) {
            try {
                new ServiceMessageSender(messengers, pendingMessages).execute(pendingMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
