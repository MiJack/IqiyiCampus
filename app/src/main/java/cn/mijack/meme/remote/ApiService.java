package cn.mijack.meme.remote;

import android.arch.lifecycle.LiveData;

import java.util.List;

import cn.mijack.meme.model.Emoji;
import cn.mijack.meme.user.User;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

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
}
