package com.nightfarmer.realmdemo;

import android.os.Handler;
import android.os.Looper;

import org.junit.Test;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void hehe() {
        Subscription subscription = hehe2();
        System.out.println(subscription.isUnsubscribed());
        subscription.unsubscribe();
        System.out.println(subscription.isUnsubscribed());
        hehe2();
    }

    private Subscription hehe2() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                boolean hasLooper = false;
                Looper looper = Looper.myLooper();
                if (looper != null) hasLooper = true;
                if (!hasLooper) {
                    Looper.prepare();
                }
                Handler handler = new Handler(Looper.myLooper());
                subscriber.onNext("xxxx");
                if (!hasLooper) {
                    Looper.loop();
                }
            }
        })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println(s);
                    }
                });
    }
}