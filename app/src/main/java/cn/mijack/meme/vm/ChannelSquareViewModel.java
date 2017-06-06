package cn.mijack.meme.vm;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import cn.mijack.meme.model.ChannelEntity;
import cn.mijack.meme.remote.ApiParamsGen;
import cn.mijack.meme.remote.ApiResponse;

/**
 * @author Mr.Yuan
 * @date 2017/6/3
 */
public class ChannelSquareViewModel extends MemeViewModel {
    private LiveData<ApiResponse<ChannelEntity>> channelData;


    public LiveData<ApiResponse<ChannelEntity>> loadChannels(Context context) {
        if (channelData == null) {
            channelData = apiService.channelList(ApiParamsGen.genChannelParams(context));
        }
        return channelData;
    }
}
