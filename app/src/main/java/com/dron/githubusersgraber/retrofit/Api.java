package com.dron.githubusersgraber.retrofit;

import com.dron.githubusersgraber.model.UserDetail;
import com.dron.githubusersgraber.model.UserRepo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Api {
    @GET("users")
    Observable<List<UserDetail>> getAllUsers();

    @GET("users/{login}/repos")
    Observable<List<UserRepo>> getRepo(@Path("login") String login);
}

