package com.android.actormodel.commands;

import android.os.Message;

import java.util.LinkedHashMap;


/**
 * a map of {@link Message} {@link Command} instances, the key oof every instance is the
 * {@link Message#what}, and the value is a {@link Command} that takes the received {@link Message}
 * as it's parameter
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
public class CommandsMap extends LinkedHashMap<Integer, Command<Message>> {


    /**
     * invoke the {@link Command} that is mapped to the passed key (if available)
     *
     * @param what    the key of the {@link Command}
     * @param message the {@link Message} to be passed to the found {@link Command}
     * @throws Exception if {@link Command#execute(Object)} threw an {@link Exception}
     */
    public final void accept(Integer what, Message message) throws Exception {
        Command<Message> command = get(what);
        if (command != null) {
            command.execute(message);
        }
    }
}
