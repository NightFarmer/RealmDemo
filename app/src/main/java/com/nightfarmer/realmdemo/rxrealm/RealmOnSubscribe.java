package com.nightfarmer.realmdemo.rxrealm;

import android.os.Looper;

import io.realm.Realm;
import io.realm.RealmObject;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by zhangfan on 16-9-20.
 */
public class RealmOnSubscribe<T> implements Observable.OnSubscribe<T> {

    RxRealm.XX xx;

    public RealmOnSubscribe(RxRealm.XX xx) {
        this.xx = xx;
    }

    @Override
    public void call(final Subscriber<? super T> subscriber) {
        new Thread(new Runnable() {
            private Realm realm;

            @Override
            public void run() {
                Looper.prepare();
                try {
                    realm = Realm.getDefaultInstance();

                    RealmObject realmObject = (RealmObject) xx.invoke(realm);
                    realmObject.asObservable().subscribe(new Action1<RealmObject>() {
                        @Override
                        public void call(RealmObject realmObject) {
                            subscriber.onNext((T)realmObject);
                        }
                    });
                    Looper.loop();
                } finally {
                    if (realm != null) {
                        realm.close();
                    }
                }
            }
        }).start();
    }
}
