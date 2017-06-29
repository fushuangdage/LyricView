package com.example.fushuang.lyricview;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by admin on 2017/5/30.
 */

public interface QQMusicService {
    String Host="http://route.showapi.com/213-1/";
    String LyricHost="http://route.showapi.com/213-2/";


    @GET(Host)
    Call<MusicSearchResult> getSearchResult(@Query("showapi_appid") int appid, @Query("keyword")
            String songName, @Query("page") int page, @Query("showapi_sign") String sign);


    @GET(LyricHost)
    Call<LyricResult> getLyricResult(@Query("musicid") int musicid,@Query("showapi_appid") int appid,@Query("showapi_sign") String sign);


    @Streaming
    @GET()
    Call<ResponseBody> downLoadMusic(@Url String url);

}

