package com.nightfarmer.realmdemo.rxrealm;

import android.os.Looper;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObject;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by zhangfan on 16-9-20.
 */
public class RealmObjOnSubscribe<T extends RealmModel> implements Observable.OnSubscribe<RxRealmResult<T>> {

    Realm realm = null;
    RealmFindObj<T> realmFindObj;
    boolean listen;
    RealmModel realmObject;

    public RealmObjOnSubscribe(RealmFindObj<T> realmFindObj, boolean listen) {
        this.realmFindObj = realmFindObj;
        this.listen = listen;
    }

    void unSubscribe() {
        realm.removeAllChangeListeners();
        if (realmObject != null) {
            RealmObject.removeChangeListeners(realmObject);
        }
        if (realm != null) {
            realm.close();
        }
    }

    @Override
    public void call(final Subscriber<? super RxRealmResult<T>> subscriber) {
        boolean hasLooper = false;
        Looper looper = Looper.myLooper();
        if (looper != null) hasLooper = true;
        if (!hasLooper) {
            Looper.prepare();
        }
        realm = Realm.getDefaultInstance();
        realmObject = realmFindObj.call(realm);

        RealmObject.addChangeListener(realmObject, new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {
                doNext(subscriber);
            }
        });
        doNext(subscriber);
        if (!listen) {
            subscriber.onCompleted();
        }
        if (!hasLooper) {
            Looper.loop();
        }
    }

    private void doNext(Subscriber<? super RxRealmResult<T>> subscriber) {
        RxRealmResult<T> tRxRealmResult = new RxRealmResult<>();
        tRxRealmResult.realm = realm;
        tRxRealmResult.obj = (T) realmObject;
        subscriber.onNext(tRxRealmResult);
    }


}
