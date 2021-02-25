package com.dron.githubusersgraber.retrofit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.dron.githubusersgraber.misc.Const.BASE_URL;
import static com.dron.githubusersgraber.misc.Const.CLIENT_ID;
import static com.dron.githubusersgraber.misc.Const.CLIENT_SECRET;

public class MyRetrofitClient {



    //Auth to github api for increasing rate limit
    private static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new AuthInterceptor(CLIENT_ID, CLIENT_SECRET))
            .build();

    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static Api api = retrofit.create(Api.class);

    public static Api getRequestApi() {
        return api;
    }
}
