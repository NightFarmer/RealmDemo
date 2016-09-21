package com.nightfarmer.realmdemo.rxrealm;

import io.realm.Realm;
import io.realm.RealmModel;

/**
 * Created by zhangfan on 16-9-21.
 */
public class RxRealmResult<T extends RealmModel> {

    public Realm realm;
    public T obj;
}
