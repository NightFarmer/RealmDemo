package com.nightfarmer.realmdemo.rxrealm;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by zhangfan on 16-9-20.
 */
public class RealmListOnSubscribe<T extends RealmModel> implements Observable.OnSubscribe<RxRealmResultList<T>> {

    RealmFindList<T> realmFindList;
    Realm realm = null;
    boolean listen;
    RealmResults<T> realmObject;

    public RealmListOnSubscribe(RealmFindList<T> realmFindList, boolean listen) {
        this.realmFindList = realmFindList;
        this.listen = listen;
    }

    void unSubscribe() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                realm.removeAllChangeListeners();
                if (realmObject != null) {
                    realmObject.removeChangeListeners();
                }
                if (realm != null) {
                    realm.close();
                }
                Looper looper = Looper.myLooper();
                if (looper!=null){
                    looper.quit();
                }
            }
        });
    }

    Handler handler;

    @Override
    public void call(final Subscriber<? super RxRealmResultList<T>> subscriber) {
        Log.i("heheh", "call " + Thread.currentThread().getName());

        boolean hasLooper = false;
        Looper looper = Looper.myLooper();
        if (looper != null) hasLooper = true;
        if (!hasLooper) {
            Looper.prepare();
        }

        handler = new Handler(Looper.myLooper());
        realm = Realm.getDefaultInstance();
        realmObject = realmFindList.call(realm);
        realmObject.addChangeListener(new RealmChangeListener<RealmResults<T>>() {
            @Override
            public void onChange(RealmResults<T> element) {
                doNext(subscriber);
            }
        });
        doNext(subscriber);
        if (!listen) {
            subscriber.unsubscribe();
        }
        if (!hasLooper) {
            Looper.loop();
        }
    }

    private void doNext(Subscriber<? super RxRealmResultList<T>> subscriber) {
        RxRealmResultList<T> tRxRealmResult = new RxRealmResultList<>();
        tRxRealmResult.realm = realm;
        tRxRealmResult.list = realmObject;
        subscriber.onNext(tRxRealmResult);
    }


}
