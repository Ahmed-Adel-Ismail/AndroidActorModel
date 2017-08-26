package com.android.actormodel;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;

import com.android.actormodel.commands.Command;
import com.android.actormodel.commands.CommandsMap;
import com.android.actormodel.parsers.MessengersMapFromBundle;
import com.android.actormodel.parsers.PendingMessagesFromBundle;
import com.android.actormodel.queues.PendingMessages;
import com.android.actormodel.queues.PendingMessagesSender;

import java.util.Map;

/**
 * a {@link Service} that runs in on a background {@link Looper}
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
public abstract class ActorService extends Service {

    public static final int REGISTER_ACTOR_SERVICE_COMPLETE = 400;
    public static final String KEY_EXTRA_PENDING_MESSAGES = "KEY_EXTRA_PENDING_MESSAGES";
    public static final String KEY_EXTRA_MESSENGERS = "KEY_EXTRA_MESSENGERS";


    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final CommandsMap commandsMap;
    private Handler handler;
    private Messenger messenger;

    public ActorService() {
        this.commandsMap = new CommandsMap();
        this.commandsMap.put(REGISTER_ACTOR_SERVICE_COMPLETE, onRegistered(getClass()));
    }

    private Command<Message> onRegistered(final Class<? extends ActorService> serviceClass) {
        return new Command<Message>() {
            @Override
            public void execute(Message message) throws Exception {
                handlePendingMessages(message, serviceClass);
            }
        };
    }


    private void handlePendingMessages(Message message, Class<? extends ActorService> serviceClass)
            throws ClassNotFoundException {

        Bundle bundle = message.getData();
        Map<Class<?>, Messenger> messengers = parseMessengers(bundle);
        PendingMessages pendingMessages = parsePendingMessages(bundle);
        new PendingMessagesSender(messengers, pendingMessages).execute(serviceClass);
    }


    private Map<Class<?>, Messenger> parseMessengers(Bundle bundle) throws ClassNotFoundException {
        return new MessengersMapFromBundle().apply(bundle.getBundle(KEY_EXTRA_MESSENGERS));
    }

    private PendingMessages parsePendingMessages(Bundle bundle) throws ClassNotFoundException {
        return new PendingMessagesFromBundle().accept(bundle.getBundle(KEY_EXTRA_PENDING_MESSAGES));
    }

    @Override
    public final void onCreate() {
        super.onCreate();
        handler = createHandler(commandsMap);
        messenger = new Messenger(handler);
        onCreateActorService();
    }

    /**
     * override this method instead of {@link #onCreate()}, you can invoke
     * {@link #onMessageReceived(int, Command)} in the constructor safely
     */
    protected void onCreateActorService() {
        // template method
    }


    private static Handler createHandler(final CommandsMap commandsMap) {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                try {
                    commandsMap.accept(message.what, message);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                super.handleMessage(message);
            }
        };
    }

    /**
     * add a {@link Command} that will be executed when a {@link Message} is received,
     * notice that this {@link Command} will be executed on the Main thread, for background
     * operations, you should handle this manually ... if this {@link ActorService} is in a
     * separate process, running on the Main thread is fine
     *
     * @param what              the {@link Message#what} expected
     * @param onMessageReceived the {@link Command} to be executed
     */
    protected final void onMessageReceived(int what, Command<Message> onMessageReceived) {
        commandsMap.put(what, onMessageReceived);
    }


    @Override
    public final void onDestroy() {

        onDestroyService();
        commandsMap.clear();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        messenger = null;
        super.onDestroy();
    }

    /**
     * override this method instead of {@link #onDestroy()}
     */
    protected void onDestroyService() {
        // template method
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (messenger != null) {
            return messenger.getBinder();
        } else {
            return null;
        }
    }
}
