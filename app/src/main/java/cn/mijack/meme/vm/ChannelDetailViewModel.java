package cn.mijack.meme.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import java.util.Map;

import cn.mijack.meme.model.ChannelDetailEntity;
import cn.mijack.meme.remote.ApiParamsGen;
import cn.mijack.meme.remote.ApiResponse;

/**
 * @author Mr.Yuan
 * @date 2017/6/3
 */
public class ChannelDetailViewModel extends MemeViewModel {
    MutableLiveData<ApiResponse<ChannelDetailEntity>> liveData;

    public MutableLiveData<ApiResponse<ChannelDetailEntity>> loadChannel(Context context, String channelId, String channelName) {
        if (liveData == null) {
            Map<String, String> channelDetailParams =
                    ApiParamsGen.genChannelDetailParams(
                            context, channelId, channelName, 0, DEFAULT_PAGE_SIZE);
            liveData = apiService.channelDetail(channelDetailParams);
        }
        return liveData;
    }

    public LiveData<ApiResponse<ChannelDetailEntity>> loadMore(Context context, String channelId, String channelName,
                                                               int pageIndex) {
        if (liveData == null) {
            liveData = loadChannel(context, channelId, channelName);
            return liveData;
        }
        Map<String, String> channelDetailParams =
                ApiParamsGen.genChannelDetailParams(
                        context, channelId, channelName, pageIndex, DEFAULT_PAGE_SIZE);
        return apiService.channelDetail(channelDetailParams);
    }
}
