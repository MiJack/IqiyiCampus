package cn.mijack.meme.remote;

/**
 * @author Mr.Yuan
 * @date 2017/5/26
 */

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Map;

import cn.mijack.meme.model.ChannelDetailEntity;
import cn.mijack.meme.model.ChannelEntity;
import cn.mijack.meme.model.RecommendEntity;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by zhouxiaming on 2017/4/11.
 *
 * @change by Yuan Yujie
 */
public interface IqiyiApiService {
//    /**
//     * 获取频道列表接口
//     *
//     * @param params
//     * @return
//     */
//    @GET("channel")
//    Observable<ChannelEntity> qiyiChannelList(@QueryMap Map<String, String> params);
//
//    /**
//     * 获取频道详情接口
//     *
//     * @param params
//     * @return
//     */
//    @GET("channel")
//    Observable<ChannelDetailEntity> qiyiChannelDetail(@QueryMap Map<String, String> params);
//
//    /**
//     * 获取推荐页数据接口
//     *
//     * @param params
//     * @return
//     */
//    @GET("recommend")
//    Observable<RecommendEntity> qiyiRecommendDetail(@QueryMap Map<String, String> params);

    /**
     * 获取推荐页数据接口
     *
     * @param params
     * @return
     */
    @GET("recommend")
    LiveData<ApiResponse<RecommendEntity>> recommendDetail(@QueryMap Map<String, String> params);

    /**
     * 获取频道列表接口
     *
     * @param params
     * @return
     */
    @GET("channel")
    LiveData<ApiResponse<ChannelEntity>> channelList(@QueryMap Map<String, String> params);


    /**
     * 获取频道详情接口
     *
     * @param params
     * @return
     */
    @GET("channel")
    MutableLiveData<ApiResponse<ChannelDetailEntity>> channelDetail(@QueryMap Map<String, String> params);

}
