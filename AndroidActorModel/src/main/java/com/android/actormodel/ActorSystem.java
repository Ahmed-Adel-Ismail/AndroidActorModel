package com.android.actormodel;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import com.android.actormodel.commands.Command;
import com.android.actormodel.exceptions.ActorMessagingException;
import com.android.actormodel.exceptions.ActorRegistrationException;
import com.android.actormodel.integration.ActorsIntegrationService;

import java.io.Serializable;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * a class that handles communication across classes across the whole {@link Application}, even
 * across multiple processes
 * <p>
 * the default implementation to use this class is to invoke {@link #with(Application, Command)}
 * in {@link Application#onCreate()}, and use the returned instance across the whole application
 * <p>
 * due to the reactive nature of the Actor-Model, you can use {@code RxJava2}'s {@code ReplaySubject}
 * to save the returned instance, and then subscribe to this {@code ReplaySubject} across the
 * application to emit this {@code Actor-System} in the {@code onNext()} when ever it is ready
 * <p>
 * <u>sample code for the Application class :</u>
 * <p>
 * private final Subject<ActorSystem> actorSystem = ReplaySubject.create(1);
 * <p>
 * public void onCreate() {<br>
 * super.onCreate();<br>
 * ActorSystem.with(this, actorSystem::onNext);<br>
 * }
 * <p>
 * public static Observable<ActorSystem> getActorSystem() {return instance.actorSystem;}
 * <p>
 * ============
 * <p>
 * <u>and from the Activities or Services :</u>
 * <p>
 * MainApplication.getActorSystem()
 * .subscribe(actorSystem -> actorSystem.register().actor(MainService.class));
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
public class ActorSystem implements Serializable {

    private final Messenger actorServiceMessenger;

    private ActorSystem(Messenger actorServiceMessenger) {
        this.actorServiceMessenger = actorServiceMessenger;
    }

    /**
     * create a new {@link ActorSystem} for the passed {@link Application}
     *
     * @param application the {@link Application} that will use the {@link ActorSystem}
     * @param onComplete  {@link Command} that will be executed when the {@link ActorSystem}
     *                    is created and it's Services are bound
     */
    public static void with(Application application, Command<ActorSystem> onComplete) {
        ApplicationWrapper.setApplication(application);
        Intent intent = new Intent(application, ActorsIntegrationService.class);
        application.bindService(intent, serviceConnection(onComplete), BIND_AUTO_CREATE);
    }

    private static ServiceConnection serviceConnection(final Command<ActorSystem> onComplete) {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    ActorSystem actorSystem = new ActorSystem(new Messenger(service));
                    onComplete.execute(actorSystem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                System.err.println(name + "disconnected");
            }
        };
    }


    /**
     * register an Actor
     *
     * @return a {@link ActorRegistration} instance to handle the operation
     * @throws ActorRegistrationException if the operation failed
     */
    public ActorRegistration register() throws ActorRegistrationException {
        if (actorServiceMessenger != null) {
            return new ActorRegistration(actorServiceMessenger);
        } else {
            throw new ActorRegistrationException(ActorsIntegrationService.class.getName() +
                    " not connected");
        }
    }

    /**
     * unregister an Actor
     *
     * @return an {@link ActorUnRegistration} instance to handle the operation
     * @throws ActorRegistrationException if the operation failed
     */
    public ActorUnRegistration unregister() throws ActorRegistrationException {
        if (actorServiceMessenger != null) {
            return new ActorUnRegistration(actorServiceMessenger);
        } else {
            throw new ActorRegistrationException(ActorsIntegrationService.class.getName() +
                    " not connected");
        }
    }

    /**
     * find an Actor to send a {@link Message} to it
     *
     * @param actor the Actor to send {@link Message} to
     * @return a {@link ActorsSender} to control the operation
     */
    public ActorsSender actorOf(Class<?> actor) throws ActorMessagingException {
        if (actorServiceMessenger != null) {
            return new ActorsSender(actorServiceMessenger, actor);
        } else {
            throw new ActorMessagingException(ActorsIntegrationService.class.getName() +
                    " not connected");
        }
    }


}
