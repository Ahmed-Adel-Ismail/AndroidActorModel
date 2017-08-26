package com.android.actormodel.integration;

import android.app.Application;
import android.os.Messenger;

import com.android.actormodel.ActorService;
import com.android.actormodel.ApplicationWrapper;

/**
 * a class that holds addressees to all the available {@link Messenger} classes across the
 * application, and handles communication between them
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
public class ActorsIntegrationService extends ActorService {

    public static final int REGISTER_ACTOR_SERVICE = 401;
    public static final int UNREGISTER_ACTOR_SERVICE = 402;
    public static final int REGISTER_ACTOR = 403;
    public static final int UNREGISTER_ACTOR = 404;
    public static final int SEND_MESSAGE = 405;
    public static final String KEY_ADDRESS = "KEY_ADDRESS";
    public static final String KEY_REPLY_TO_ADDRESS = "KEY_REPLY_TO_ADDRESS";

    private final Cache cache;

    public ActorsIntegrationService() {
        this.cache = new Cache();
        Application application = ApplicationWrapper.getApplication();
        onMessageReceived(REGISTER_ACTOR_SERVICE, new ServiceRegistration(application, cache));
        onMessageReceived(UNREGISTER_ACTOR_SERVICE, new ServiceUnRegistration(application, cache.connections));
        onMessageReceived(REGISTER_ACTOR, new MailboxRegistration(cache.messengers));
        onMessageReceived(UNREGISTER_ACTOR, new MailboxUnRegistration(cache.messengers));
        onMessageReceived(SEND_MESSAGE, new ServiceMessageSender(cache.messengers, cache.pendingMessages));
    }

    @Override
    public void onDestroyService() {
        cache.clear();
    }
}
