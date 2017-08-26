package com.android.actormodel.integration;

import android.os.Message;
import android.os.Messenger;

import com.android.actormodel.commands.Command;

import java.util.Map;

import static com.android.actormodel.integration.ActorsIntegrationService.*;


/**
 * a function that un-registers the {@link Messenger} stored in {@link Cache#messengers}
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
class MailboxUnRegistration implements Command<Message> {

    private final Map<Class<?>, Messenger> messengers;

    MailboxUnRegistration(Map<Class<?>, Messenger> messengers) {
        this.messengers = messengers;
    }

    @Override
    public void execute(Message message) throws Exception {
        Class<?> addressClass = (Class<?>) message.getData().getSerializable(KEY_ADDRESS);
        if (messengers.containsKey(addressClass)) {
            messengers.remove(addressClass);
        }
    }
}
