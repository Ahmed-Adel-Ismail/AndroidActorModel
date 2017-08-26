package com.android.actormodel;

import android.app.Service;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

import com.android.actormodel.exceptions.ActorRegistrationException;
import com.android.actormodel.integration.ActorsIntegrationService;

/**
 * a class responsible for un-registering actors
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
public class ActorUnRegistration {

    private final Messenger actorServiceMessenger;

    ActorUnRegistration(Messenger actorServiceMessenger) {
        this.actorServiceMessenger = actorServiceMessenger;
    }

    /**
     * unregister a {@link ActorService} from the {@link ActorSystem} ... if the passed {@link Service}
     * {@link Class} was not registered before, nothing will happen
     *
     * @param serviceClass the {@link Class} of the service
     * @throws ActorRegistrationException if the operation failed
     */
    public void actor(Class<? extends ActorService> serviceClass)
            throws ActorRegistrationException {
        Message message = Message.obtain();
        message.what = ActorsIntegrationService.UNREGISTER_ACTOR_SERVICE;
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(ActorsIntegrationService.KEY_ADDRESS, serviceClass);
        message.setData(bundle);
        try {
            actorServiceMessenger.send(message);
        } catch (Throwable e) {
            throw new ActorRegistrationException(e);
        }
    }

    /**
     * unregister an Actor to the {@link ActorSystem}, if this Actor is a service, you can use
     * {@link #actor(Class)} instead ... this method invokes {@link Mailbox#clear()} after
     * un-registering
     *
     * @param mailbox the {@link Mailbox} of the Actor
     * @throws ActorRegistrationException if the operation failed
     */
    public void actor(Mailbox mailbox)
            throws ActorRegistrationException {
        Message message = Message.obtain();
        message.what = ActorsIntegrationService.UNREGISTER_ACTOR;
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(ActorsIntegrationService.KEY_ADDRESS, mailbox.getAddress());
        message.setData(bundle);
        message.replyTo = mailbox.getMessenger();
        try {
            actorServiceMessenger.send(message);
        } catch (Throwable e) {
            throw new ActorRegistrationException(e);
        } finally {
            mailbox.clear();
        }
    }
}
