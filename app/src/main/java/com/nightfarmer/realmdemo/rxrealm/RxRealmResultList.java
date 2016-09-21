package com.nightfarmer.realmdemo.rxrealm;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by zhangfan on 16-9-21.
 */
public class RxRealmResultList<T extends RealmModel> {

    public Realm realm;
    public RealmResults<T> list;
}
