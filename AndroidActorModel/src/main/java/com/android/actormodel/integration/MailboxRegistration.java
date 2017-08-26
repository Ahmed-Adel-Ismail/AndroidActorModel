package com.android.actormodel.integration;

import android.os.Message;
import android.os.Messenger;

import com.android.actormodel.commands.Command;

import java.util.Map;

import static com.android.actormodel.integration.ActorsIntegrationService.KEY_ADDRESS;


/**
 * a function that registers the {@link Messenger} available in {@link Message#replyTo}
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
class MailboxRegistration implements Command<Message> {

    private final Map<Class<?>, Messenger> messengers;

    MailboxRegistration(Map<Class<?>, Messenger> messengers) {
        this.messengers = messengers;
    }

    @Override
    public void execute(Message message) throws Exception {
        Class<?> addressClass = (Class<?>) message.getData().getSerializable(KEY_ADDRESS);
        messengers.put(addressClass, message.replyTo);
    }


}
