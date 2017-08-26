package com.tere.playground;

import android.app.Application;

import com.android.actormodel.ActorSystem;

import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;


/**
 * Created by Ahmed Adel Ismail on 5/12/2017.
 */

public class MainApplication extends Application {

    private static MainApplication instance;
    private final Subject<ActorSystem> actorSystem = ReplaySubject.create(1);

    @Override
    public void onCreate() {
        super.onCreate();
        ActorSystem.with(MainApplication.this, actorSystem::onNext);
        instance = this;

    }

    public static Observable<ActorSystem> getActorSystem() {
        return instance.actorSystem;
    }
}
