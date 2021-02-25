package com.dron.githubusersgraber.model;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class User extends RealmObject implements Serializable {

    @PrimaryKey
    private Integer id;

    private UserDetail mUserDetail;
    private Integer mCountChange;

    public User() {
    }

    public User(Integer id, UserDetail userDetail) {
        this.id = id;
        mUserDetail = userDetail;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserDetail getUserDetail() {
        return mUserDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        mUserDetail = userDetail;
    }

    public Integer getCountChange() {
        return mCountChange;
    }

    public void setCountChange(Integer countChange) {
        mCountChange = countChange;
    }
}
