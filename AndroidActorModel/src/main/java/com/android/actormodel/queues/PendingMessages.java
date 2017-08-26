package com.android.actormodel.queues;

import android.os.Message;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * a class that queues pending Messages for an address
 * <p>
 * Created by Ahmed Adel Ismail on 8/12/2017.
 */
public class PendingMessages implements Serializable {

    public final Map<Class<?>, List<Message>> messagesQueue;

    public PendingMessages(Map<Class<?>, List<Message>> messagesQueue) {
        this.messagesQueue = messagesQueue;
    }

    public synchronized final void queue(Class<?> address, Message message) {
        List<Message> queue = getNonNullQueue(address);
        queue.add(message);
        messagesQueue.put(address, queue);
    }

    synchronized final List<Message> consume(Class<?> address) {
        List<Message> pendingMessages = getNonNullQueue(address);
        messagesQueue.remove(address);
        return pendingMessages;
    }

    private List<Message> getNonNullQueue(Class<?> address) {
        List<Message> queue = messagesQueue.get(address);
        if (queue == null) {
            queue = new LinkedList<>();
        }
        return queue;
    }

    public final void clear() {
        messagesQueue.clear();
    }

    @Override
    public String toString() {
        return "PendingMessages{" +
                "map=" + messagesQueue +
                '}';
    }
}
