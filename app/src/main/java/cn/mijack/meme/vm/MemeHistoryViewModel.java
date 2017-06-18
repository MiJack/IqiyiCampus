package cn.mijack.meme.vm;

import android.arch.lifecycle.LiveData;

import java.util.List;

import cn.mijack.meme.model.MemeEntity;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.remote.Result;

/**
 * @author admin
 * @date 2017/6/18
 */

public class MemeHistoryViewModel extends BaseViewModel {
    private LiveData<ApiResponse<Result<List<MemeEntity>>>> memeLiveData;

    public LiveData<ApiResponse<Result<List<MemeEntity>>>> loadMyMemes(int uid) {
        if (memeLiveData == null) {
            memeLiveData = getApiService().listMyMemes(String.valueOf(uid));
        }
        return memeLiveData;
    }
}
