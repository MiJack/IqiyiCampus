package cn.mijack.meme.remote;

import android.arch.lifecycle.LiveData;

import java.util.List;
import java.util.Map;

import cn.mijack.meme.model.Emoji;
import cn.mijack.meme.model.TokenEntity;
import cn.mijack.meme.user.User;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author Mr.Yuan
 * @date 2017/6/4
 */
public interface ApiService {
    String HOST = "develop.mijack.cn";
    String SERVER = "http://" + HOST + ":8080";
//    public static final String SERVER = "http://192.168.1.104:8080";

    @POST(SERVER + "/login")
    @FormUrlEncoded
    Observable<Result<User>> login(
            @Field("userEmail") String userEmail,
            @Field("password") String password);

    @POST(SERVER + "/register")
    @FormUrlEncoded
    Observable<Result<User>> register(@Field("userName") String username,
                                      @Field("userEmail") String userEmail,
                                      @Field("password") String password);

    @GET(SERVER + "/listEmoji")
    LiveData<ApiResponse<Result<List<Emoji>>>> listEmoji();

    @GET(SERVER + "/requestQiniuToken")
//    http://develop.mijack.cn:8080/requestQiniuToken?uid=12&videoId=12&startTime=1212
    Observable<Result<TokenEntity>> requestToken(
            @Query("uid") int uid, @Query("videoId") String vId,
            @Query("startTime") long progess,
            @Query("title") String title,
            @Query("shortTitle") String shortTitle);
}

