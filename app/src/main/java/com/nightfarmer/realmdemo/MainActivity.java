package com.nightfarmer.realmdemo;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nightfarmer.realmdemo.bean.Dog;
import com.nightfarmer.realmdemo.bean.Person;
import com.nightfarmer.realmdemo.rxrealm.RealmFindList;
import com.nightfarmer.realmdemo.rxrealm.RxRealm;
import com.nightfarmer.realmdemo.rxrealm.RxRealmResultList;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    TextView console;

    Realm realm;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        console = (TextView) findViewById(R.id.console);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();

        Number id = realm.where(Person.class).max("id");
        if (id != null) {
            count = id.intValue() / 1000 + 1;
        }

        createObjects();
//        initListener();
    }

    private void initListener() {
        realm.where(Person.class).findAllAsync().asObservable()
                .filter(new Func1<RealmResults<Person>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Person> persons) {
                        return persons.isLoaded();
                    }
                })
                .map(new Func1<RealmResults<Person>, Integer>() {
                    @Override
                    public Integer call(RealmResults<Person> persons) {
                        return persons.size();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        console.append("Person条目:" + integer + "\n");
                    }
                });
    }

    private void createObjects() {
        users = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Person e = new Person();
            e.setId(i + count * 1000);
            e.setName("person-" + (i + count * 1000));
            users.add(e);
        }
        count++;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void deleteAll(View view) {
        count = 0;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
    }

    Subscription subscription = null;

    /**
     * 后台查询, 并将分离Realm后的数据传回前台, 并持续监听直至unsubscribe
     *
     * @param view
     */
    public void findAllPersonAndListen(View view) {
        if (subscription1 != null && !subscription1.isUnsubscribed()) {
            print("监听未停止,取上次结果即可");
            return;
        }
        subscription1 = RxRealm
                .listAndListen(new RealmFindList<Person>() {
                    @Override
                    public RealmResults<Person> call(Realm realm) {
//                        return realm.where(Person.class).findAllAsync();
                        return realm.where(Person.class).findAll();
                    }
                })
                .map(new Func1<RxRealmResultList<Person>, List<Person>>() {
                    @Override
                    public List<Person> call(RxRealmResultList<Person> personRxRealmResultList) {
                        return personRxRealmResultList.realm.copyFromRealm(personRxRealmResultList.list);
                    }
                })
                .subscribeOn(RxRealm.ioScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Person>>() {
                    @Override
                    public void call(List<Person> personRxRealmResultList) {
                        print("Person数量" + personRxRealmResultList.size() + " thread:" + Thread.currentThread().getName());
                    }
                });
    }

    Subscription subscription1;//findAllPersonAndListen的监听

    /**
     * 取消findAllPersonAndListen方法的监听
     *
     * @param view
     */
    public void unSubscribe1(View view) {
        if (subscription1 != null && !subscription1.isUnsubscribed()) {
            subscription1.unsubscribe();
        }
    }

    /**
     * 后台查询, 并将分离Realm后的数据传回前台, 查询后关闭realm
     *
     * @param view
     */
    public void findAllPerson(View view) {

        subscription1 = RxRealm
                .list(new RealmFindList<Person>() {
                    @Override
                    public RealmResults<Person> call(Realm realm) {
//                        return realm.where(Person.class).findAllAsync();
                        return realm.where(Person.class).findAll();
                    }
                })
                .map(new Func1<RxRealmResultList<Person>, List<Person>>() {
                    @Override
                    public List<Person> call(RxRealmResultList<Person> personRxRealmResultList) {
                        return personRxRealmResultList.realm.copyFromRealm(personRxRealmResultList.list);
                    }
                })
                .subscribeOn(RxRealm.ioScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Person>>() {
                    @Override
                    public void call(List<Person> personRxRealmResultList) {
                        print("Person数量" + personRxRealmResultList.size() + " thread:" + Thread.currentThread().getName());
                    }
                });
    }

    public void a(View v) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Person first = realm.where(Person.class).findFirst();
                first.setName("aaa");

            }
        });
    }

    public void b(View v) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Person first = realm.where(Person.class).findFirst();
                first.setName("bbb");
            }
        });
    }

    public void c(View v) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public void findAllPerson3(View view) {

//        Observable
//                .create(new RealmListOnSubscribe<>(new RealmFindList<Person>() {
//                    @Override
//                    public RealmResults<Person> call(Realm realm) {
//                        Log.i("myLogRealm执行", "调度线程:" + Thread.currentThread().getName());
//                        return realm.where(Person.class).findAll();
//                    }
//                }))
//                .map(new Func1<RxRealmResultList<Person>, List<Person>>() {
//                    @Override
//                    public List<Person> call(RxRealmResultList<Person> personRxRealmResultList) {
//                        return personRxRealmResultList.realm.copyFromRealm(personRxRealmResultList.list);
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<List<Person>>() {
//                    @Override
//                    public void call(List<Person> persons) {
//                        Log.i("myLog请求列表", "结果线程:" + Thread.currentThread().getName() + "__" + "结果: " + persons.size());
//                    }
//                });

//        Observable
//                .create(RxRealm.find(new RealmFindObj<Person>() {
//                    @Override
//                    public Person call(Realm realm) {
//                        return realm.where(Person.class).findFirst();
//                    }
//                }))
//                .map(new Func1<RxRealmResult<Person>, Person>() {
//                    @Override
//                    public Person call(RxRealmResult<Person> personRxRealmResult) {
//                        return personRxRealmResult.realm.copyFromRealm(personRxRealmResult.obj);
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Person>() {
//                    @Override
//                    public void call(Person person) {
//                        Log.i("myLog请求对象", "结果线程:" + Thread.currentThread().getName() + "  " + person.getName());
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        throwable.printStackTrace();
//                    }
//                });

    }

    public void findAllPerson2(View view) {
        new Thread(new Runnable() {
            private Realm realm;

            @Override
            public void run() {
                Looper.prepare();
                try {
                    realm = Realm.getDefaultInstance();
                    //... Setup the handlers using the Realm instance ...
                    final RealmResults<Person> all = realm.where(Person.class).findAll();
                    all.asObservable()
//                            .filter(new Func1<RealmResults<Person>, Boolean>() {
//                                @Override
//                                public Boolean call(RealmResults<Person> persons) {
//                                    return persons.isLoaded();
//                                }
//                            })
                            .map(new Func1<RealmResults<Person>, String>() {
                                @Override
                                public String call(RealmResults<Person> persons) {
                                    String result = "";
                                    for (Person p :
                                            persons) {
                                        result += p.getName() + "\n";
                                    }
                                    return all.size() + "条 - 数据处理结果" + result.length() + "\n";
                                }
                            })
                            .doOnNext(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    all.removeChangeListeners();
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    console.append("" + s);
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

    public void print(Object... datas) {
        if (datas == null) return;
        String str = "";
        for (Object data : datas) {
            str += data + "\n";
        }
        str += "----------------\n";
        console.append(str);
    }

    public void clear(View view) {
        console.setText("");
    }

    ArrayList<Person> users = new ArrayList<>();

    public void insertMany(final View v) {
        Log.i("myLog", realm.getPath());
        createObjects();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(users);
            }
        });

//        realm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realm.copyToRealmOrUpdate(users);
//            }
//        }, new Realm.Transaction.OnSuccess() {
//            @Override
//            public void onSuccess() {
//                console.append("成功插入1000条Person.\n");
//            }
//        }, new Realm.Transaction.OnError() {
//            @Override
//            public void onError(Throwable error) {
//                error.printStackTrace();
//                console.append("数据插入失败.\n");
//            }
//        });
    }


    private void test() {
        // Create a RealmConfiguration that saves the Realm file in the app's "files" directory.
//        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
//                .schemaVersion(1)
//                .build();
//        Realm.setDefaultConfiguration(realmConfig);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
//                Person user = realm.createObject(Person.class);
                Person user = new Person();
                user.setId(1);
                user.setAge(22);
                user.setName("张三");
                user.setSessionId(11111);
                realm.insertOrUpdate(user);
            }
        });

        RealmResults<Person> age = realm.where(Person.class).equalTo("age", 22).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Person age1 = realm.where(Person.class).equalTo("age", 22).findFirst();
                age1.setName("李四");
            }
        });

        final Person user1 = new Person();
        user1.setId(1);
        user1.setAge(23);
        user1.setName("王五");
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(user1);
                Dog dog = new Dog();
                dog.name = "小白";
                dog.woner = user1;
                realm.insertOrUpdate(dog);
            }
        });

        for (int i = 0; i < age.size(); i++) {
            Person user = age.get(i);
            Log.i("myLog", user.getName() + "__" + user.getId());
        }

        Dog first = realm.where(Dog.class).equalTo("name", "小白").findFirst();
        Log.i("myLog", first.woner.getName());

        Dog first1 = realm.where(Dog.class).equalTo("woner.age", 23).findFirst();
        Log.i("myLog", first1.name);

        Realm defaultInstance = realm;
        defaultInstance.beginTransaction();
        Person object = new Person();
        object.setId(2);
        object.setName("呵呵打");
        object.setAge(24);
//        defaultInstance.insertOrUpdate(object);
        defaultInstance.copyToRealmOrUpdate(object);
        defaultInstance.commitTransaction();
    }
}
