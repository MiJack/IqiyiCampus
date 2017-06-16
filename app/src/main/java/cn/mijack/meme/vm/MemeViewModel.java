package cn.mijack.meme.vm;

import android.arch.lifecycle.LiveData;

import java.util.List;

import cn.mijack.meme.model.Emoji;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.remote.Result;

/**
 * @author admin
 * @date 2017/6/16
 */

public class MemeViewModel extends BaseViewModel {
    LiveData<ApiResponse<Result<List<Emoji>>>> emojiLiveData;

    public LiveData<ApiResponse<Result<List<Emoji>>>> loadEmoji() {
        if (emojiLiveData == null) {
            emojiLiveData = getApiService().listEmoji();
        }
        return emojiLiveData;
    }
}
