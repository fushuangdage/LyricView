package com.example.fushuang.lyricview;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by admin on 2017/5/30.
 */

public class RetrofitHelper {
    private static RetrofitHelper retrofitHelper;
    private final Retrofit retrofit;
    private Map<Class, Object> apis = new HashMap<>();

    private RetrofitHelper() {
        Retrofit.Builder builder = new Retrofit.Builder();
        retrofit = builder.baseUrl(QQMusicService.Host)

                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
    }

    public synchronized static  RetrofitHelper getInstance(){
        if (retrofitHelper==null){
            retrofitHelper=new RetrofitHelper();
        }

        return retrofitHelper ;

    }
    public <T>T createApi(Class<T> tClass) {
        if (!apis.containsKey(tClass)) {
            T t = retrofit.create(tClass);
            apis.put(tClass,t);
        }

        return  (T) apis.get(tClass);
    }

}
