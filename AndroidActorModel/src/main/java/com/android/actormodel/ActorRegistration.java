package com.android.actormodel;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

import com.android.actormodel.exceptions.ActorRegistrationException;
import com.android.actormodel.integration.ActorsIntegrationService;

/**
 * a class responsible for registering actors
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
public class ActorRegistration {

    private final Messenger actorServiceMessenger;


    ActorRegistration(Messenger actorServiceMessenger) {
        this.actorServiceMessenger = actorServiceMessenger;
    }

    /**
     * register a new {@link ActorService} to the {@link ActorSystem}, this will start the passed
     * {@link ActorService} if it is not started yet
     *
     * @param serviceClass the {@link Class} of the service
     * @throws ActorRegistrationException if the operation failed
     */
    public void actor(Class<? extends ActorService> serviceClass)
            throws ActorRegistrationException {
        Message message = Message.obtain();
        message.what = ActorsIntegrationService.REGISTER_ACTOR_SERVICE;
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
     * register a new Actor to the {@link ActorSystem}, if this Actor is a service, you can use
     * {@link #actor(Class)} instead
     *
     * @param mailbox the {@link Mailbox} of the Actor
     * @throws ActorRegistrationException if the operation failed
     */
    public void actor(Mailbox mailbox)
            throws ActorRegistrationException {
        Message message = Message.obtain();
        message.what = ActorsIntegrationService.REGISTER_ACTOR;
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(ActorsIntegrationService.KEY_ADDRESS, mailbox.getAddress());
        message.setData(bundle);
        message.replyTo = mailbox.getMessenger();
        try {
            actorServiceMessenger.send(message);
        } catch (Throwable e) {
            throw new ActorRegistrationException(e);
        }
    }


}
