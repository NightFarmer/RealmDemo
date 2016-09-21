package com.nightfarmer.realmdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zhangfan on 16-9-21.
 */
public class Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void findAllPerson(View view) {
        subscription1 = hehe2();
    }

    Subscription subscription1;//findAllPersonAndListen的监听


    public void unSubscribe1(View view) {
        if (subscription1 != null && !subscription1.isUnsubscribed()) {
            subscription1.unsubscribe();
        }
    }

    Handler handler;

    private Subscription hehe2() {
        return Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        boolean hasLooper = false;
                        Looper looper = Looper.myLooper();
                        if (looper != null) hasLooper = true;
                        if (!hasLooper) {
                            Looper.prepare();
                        }
                        handler = new Handler(Looper.myLooper());
                        subscriber.onNext("called.." + Thread.currentThread().getName());
                        if (!hasLooper) {
                            Looper.loop();
                        }
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("注销" + Thread.currentThread().getName());
                                Looper looper = Looper.myLooper();
                                if (looper != null) {
                                    looper.quit();
                                }
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println(s);
                    }
                });
    }
}
