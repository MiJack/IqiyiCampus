package cn.mijack.meme.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import cn.mijack.meme.model.MemeEntity;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.remote.Result;

/**
 * @author admin
 * @date 2017/6/18
 */

public class PlayerViewModel extends BaseViewModel {
    private LiveData<ApiResponse<Result<List<MemeEntity>>>> memeLiveData;
    private MutableLiveData<Integer> progressData=new MutableLiveData<>();

    public LiveData<ApiResponse<Result<List<MemeEntity>>>> getMemeList(String videoId) {
        if (memeLiveData == null) {
            memeLiveData = getApiService().listMemes(videoId);
        }
        return memeLiveData;
    }

    public MutableLiveData<Integer> getProgressData() {
        return progressData;
    }
}
