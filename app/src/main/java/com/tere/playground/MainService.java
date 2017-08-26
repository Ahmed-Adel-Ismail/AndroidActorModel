package com.tere.playground;

import android.os.Message;
import android.util.Log;

import com.android.actormodel.ActorService;
import com.android.actormodel.commands.Command;
import com.android.actormodel.messaging.MessageBuilder;
import com.android.actormodel.messaging.MessageReader;

public class MainService extends ActorService {

    public static final int MSG_PING = 1;

    public MainService() {
        onMessageReceived(MSG_PING, replyPing());
    }


    private Command<Message> replyPing() {
        return message -> {
            Log.e("MainService","replyPing()");
            MessageReader messageReader = MessageReader.with(message);
            System.out.println(messageReader.getSerializable().toString());
            Message newMessage = MessageBuilder
                    .prepareMessage(MainActivity.MSG_SHOW_TOAST)
                    .serializable("Service Pinged")
                    .build();
            messageReader.getReplyTo().send(newMessage);
        };
    }
}
