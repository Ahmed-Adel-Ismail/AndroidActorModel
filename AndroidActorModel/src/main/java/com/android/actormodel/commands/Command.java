package com.android.actormodel.commands;

/**
 * a command function that can be executed later
 * <p>
 * Created by Ahmed Adel Ismail on 8/6/2017.
 */
public interface Command<T> {

    /**
     * execute this {@link Command}
     *
     * @param object the parameter to be used
     * @throws Exception if the execution process threw an {@link Exception}
     */
    void execute(T object) throws Exception;

}
