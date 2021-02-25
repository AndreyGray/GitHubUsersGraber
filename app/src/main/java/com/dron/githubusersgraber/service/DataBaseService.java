package com.dron.githubusersgraber.service;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class DataBaseService {

    private Realm realm = Realm.getDefaultInstance();

    //Get all  from db
    public <T extends RealmObject> RealmResults<T> getAll(Class<T> tClass) {
        return realm.where(tClass).findAllAsync();
    }

    //Get by id from  db
    public <T extends RealmObject> T getById(Integer id, Class<T> tClass) {
        return realm.where(tClass).equalTo("id", id).findFirst();
    }
}
