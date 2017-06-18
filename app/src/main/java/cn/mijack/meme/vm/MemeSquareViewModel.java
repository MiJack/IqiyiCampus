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

public class MemeSquareViewModel extends BaseViewModel {
    private LiveData<ApiResponse<Result<List<MemeEntity>>>> apiResponseLiveData;

    public LiveData<ApiResponse<Result<List<MemeEntity>>>> load() {
        if (apiResponseLiveData == null) {
            apiResponseLiveData = getApiService().listMemes();
        }
        return apiResponseLiveData;
    }

    public LiveData<ApiResponse<Result<List<MemeEntity>>>> reload() {
        apiResponseLiveData = getApiService().listMemes();
        return apiResponseLiveData;
    }
}
