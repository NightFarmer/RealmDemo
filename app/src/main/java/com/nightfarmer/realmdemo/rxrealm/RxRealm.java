package com.nightfarmer.realmdemo.rxrealm;

import android.os.Looper;

import com.nightfarmer.realmdemo.bean.Person;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by zhangfan on 16-9-20.
 */
public class RxRealm {

    public static Observable create() {
        return Observable.create(new Observable.OnSubscribe<Realm>() {
            @Override
            public void call(Subscriber<? super Realm> subscriber) {

            }
        });
    }

    public static <T> Observable<T> find(XX<T> xx) {
        return Observable.create(new RealmOnSubscribe(xx));
    }

    public interface XX<T> {
        T invoke(Realm realm);
    }
}
