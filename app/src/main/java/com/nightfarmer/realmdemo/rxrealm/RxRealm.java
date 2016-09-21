package com.nightfarmer.realmdemo.rxrealm;

import android.util.Log;

import io.realm.RealmModel;
import rx.Observable;
import rx.functions.Action0;

/**
 * Created by zhangfan on 16-9-20.
 */
public class RxRealm {

//    public static <T extends RealmModel> RealmObjOnSubscribe<T> find(RealmFindObj<T> realmFindObj) {
//        return new RealmObjOnSubscribe<>(realmFindObj);
//    }
//
//
//    public static <T extends RealmModel> RealmListOnSubscribe<T> list(RealmFindList<T> realmFindList) {
//        return new RealmListOnSubscribe<>(realmFindList);
//    }


    public static <T extends RealmModel> Observable<RxRealmResult<T>> findAndListen(RealmFindObj<T> realmFindObj) {
        return createFindObservable(realmFindObj, true);

    }

    public static <T extends RealmModel> Observable<RxRealmResult<T>> find(RealmFindObj<T> realmFindObj) {
        return createFindObservable(realmFindObj, false);
    }

    private static <T extends RealmModel> Observable<RxRealmResult<T>> createFindObservable(RealmFindObj<T> realmFindObj, boolean listen) {
        final RealmObjOnSubscribe<T> realmObjOnSubscribe = new RealmObjOnSubscribe<>(realmFindObj, listen);
        return Observable.create(realmObjOnSubscribe)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        realmObjOnSubscribe.unSubscribe();
                    }
                });
    }


    private static <T extends RealmModel> Observable<RxRealmResultList<T>> createListObservable(RealmFindList<T> realmFindList, boolean listen) {
        final RealmListOnSubscribe<T> realmListOnSubscribe = new RealmListOnSubscribe<>(realmFindList, listen);
        return Observable.create(realmListOnSubscribe)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.i("heheh","取消监听 "+Thread.currentThread().getName());
                        realmListOnSubscribe.unSubscribe();
                    }
                });
    }

    public static <T extends RealmModel> Observable<RxRealmResultList<T>> list(RealmFindList<T> realmFindList) {
        return createListObservable(realmFindList, false);
    }

    public static <T extends RealmModel> Observable<RxRealmResultList<T>> listAndListen(RealmFindList<T> realmFindList) {
        return createListObservable(realmFindList, true);
    }

}
