package cn.mijack.meme.remote;

import retrofit2.http.GET;

/**
 * @author Mr.Yuan
 * @date 2017/6/4
 */
public interface ApiService {
    @GET("/login")
    void login();
    @GET("/register")
    void register();
}
