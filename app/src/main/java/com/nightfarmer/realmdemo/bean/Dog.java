package com.nightfarmer.realmdemo.bean;

import io.realm.RealmObject;

/**
 * Created by zhangfan on 16-9-20.
 */
public class Dog extends RealmObject {

    public String name;
    public Person woner;
}
