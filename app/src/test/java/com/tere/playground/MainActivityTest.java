package com.tere.playground;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ahmed Adel Ismail on 8/1/2017.
 */
public class MainActivityTest {

    private List<Integer> integers = Arrays.asList(1, 2, 3, 4);


    @Test
    public void imperativeStyle() throws Exception {

        final List<Integer> evenNumbers = new ArrayList<>();

        Thread threadOne = new Thread(new Runnable() {
            @Override
            public void run() {
                for (Integer i : integers) {
                    if (i % 2 == 0) {
                        evenNumbers.add(i);
                    }
                }

            }
        });


        Thread threadTwo = new Thread(new Runnable() {
            @Override
            public void run() {
                for (Integer evenNumber : evenNumbers) {
                    System.out.println(evenNumber);
                }
            }
        });

        threadOne.start();
        threadOne.join();
        threadTwo.start();

        // create stream
        Observable.fromIterable(integers)
                // subscribe to the stream
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer i) {
                        // receive the emitted items
                        System.out.print(i);
                    }
                });

    }

    @Test
    public void rxJavaStyle() throws Exception {
        System.out.println("test : " + Thread.currentThread().getId());
        Observable.fromIterable(integers)
                .filter(byEvenNumbers())
                .subscribeOn(Schedulers.newThread())
                .blockingSubscribe(printInteger());
    }

    Predicate<Integer> byEvenNumbers() {
        return new Predicate<Integer>() {
            @Override
            public boolean test(Integer i) {
                System.out.println("bg : " + Thread.currentThread().getId());
                return i % 2 == 0;
            }
        };
    }

    Consumer<Integer> printInteger() {
        return new Consumer<Integer>() {
            @Override
            public void accept(Integer i) {
                System.out.println("fg : " + Thread.currentThread().getId());
                System.out.println(i);
            }
        };
    }

}