package com.android.actormodel;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;

import com.android.actormodel.commands.Command;
import com.android.actormodel.commands.CommandsMap;

/**
 * the Mailbox {@link Handler} that handles the {@link Message Messages} received
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
public class Mailbox extends Handler {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final CommandsMap commandsMap = new CommandsMap();
    private final HandlerThread handlerThread;
    private final Class<?> address;
    private final Messenger messenger;

    /**
     * create a {@link Mailbox} that handles {@link Message Messages} on the
     * {@link Looper#getMainLooper() Main Looper}
     *
     * @param address the {@link Class} that identifies the address for this {@link Mailbox}
     */
    public Mailbox(Class<?> address) {
        super(Looper.getMainLooper());
        this.handlerThread = null;
        this.address = address;
        this.messenger = new Messenger(this);
    }

    /**
     * create a {@link Mailbox} that handles {@link Message Messages} on the
     * passed {@link Looper}
     *
     * @param address the {@link Class} that identifies the address for this {@link Mailbox}
     * @param looper  the {@link Looper} that will host handling {@link Message Messages}
     */
    public Mailbox(Class<?> address, Looper looper) {
        super(looper);
        this.handlerThread = null;
        this.address = address;
        this.messenger = new Messenger(this);
    }

    /**
     * create a {@link Mailbox} that handles {@link Message Messages} on the
     * {@link Looper} of the passed {@link HandlerThread}, notice that invoking
     * {@link #clear()} method will cause {@link HandlerThread#quitSafely()} to be invoked,
     * so make sure that this {@link HandlerThread} is used just for this {@link Mailbox},
     * to use multiple {@link Mailbox Mailboxes} on the same {@link Looper}, you can use
     * {@link Mailbox#Mailbox(Class, Looper)} instead
     *
     * @param address       the {@link Class} that identifies the address for this {@link Mailbox}
     * @param handlerThread the {@link HandlerThread} that will be used to get
     *                      the {@link Looper} for this {@link Mailbox} through
     *                      {@link HandlerThread#getLooper()}
     */
    public Mailbox(Class<?> address, HandlerThread handlerThread) {
        super(retrieveLooper(handlerThread));
        this.handlerThread = handlerThread;
        this.address = address;
        this.messenger = new Messenger(this);
    }

    private static Looper retrieveLooper(HandlerThread handlerThread) {
        handlerThread.start();
        return handlerThread.getLooper();
    }


    @Override
    public final void handleMessage(Message msg) {
        super.handleMessage(msg);
        try {
            onMessageReceived(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * handle incoming {@link Message}, the default implementation is to invoke
     * {@link CommandsMap#accept(Integer, Message)} ... to add a {@link CommandsMap},
     * you need to invoke {@link #addCommandsMap(CommandsMap)}
     *
     * @param message the {@link Message} received
     * @throws Exception if any error occurred
     */
    private void onMessageReceived(Message message) throws Exception {
        commandsMap.accept(message.what, message);
    }

    /**
     * add a {@link CommandsMap} that will be executed when
     * {@link #onMessageReceived(Message)} is executed
     *
     * @param commandsMap the {@link CommandsMap}, the keys of this map are the
     *                    {@link Message#what}, and the parameter that will be
     *                    passed will be the {@link Message} itself
     */
    public void addCommandsMap(CommandsMap commandsMap) {
        this.commandsMap.putAll(commandsMap);
    }

    /**
     * add a single {@link Command} to the {@link CommandsMap} that is invoked in
     * {@link #onMessageReceived(Message)}
     *
     * @param what    the {@link Message#what} that will be the key for the passed {@link Command}
     * @param command the {@link Command} that will be executed if the received {@link Message#what}
     *                matches the {@code what} parameter
     */
    public void addCommand(int what, Command<Message> command) {
        this.commandsMap.put(what, command);
    }

    /**
     * get the address of this {@link Mailbox}
     *
     * @return the {@link Class} indicating the address of this {@link Mailbox}
     */
    public Class<?> getAddress() {
        return address;
    }

    /**
     * get the {@link Messenger} that controls this {@link Mailbox}
     *
     * @return the {@link Messenger} for this {@link Mailbox}
     */
    public Messenger getMessenger() {
        return messenger;
    }

    /**
     * clear the current {@link Mailbox}, this will invoke
     * {@link #removeCallbacksAndMessages(Object)} and {@link HandlerThread#quitSafely()} if found
     */
    public final void clear() {
        removeCallbacksAndMessages(null);
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
        commandsMap.clear();
    }
}
