package com.android.actormodel;

import android.annotation.SuppressLint;
import android.app.Application;

/**
 * a wrapper class for {@link Application} class
 * <p>
 * Created by Ahmed Adel Ismail on 8/26/2017.
 */
public class ApplicationWrapper {

    @SuppressLint("StaticFieldLeak")
    private static Application application;

    public static Application getApplication() {
        return application;
    }

    public static void setApplication(Application application) {
        ApplicationWrapper.application = application;
    }
}
