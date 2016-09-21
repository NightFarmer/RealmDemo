package com.nightfarmer.realmdemo.rxrealm;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;

/**
 * Created by zhangfan on 16-9-21.
 */
public interface RealmFindObj<T extends RealmModel> {
    T call(Realm realm);
}
