package cn.mijack.meme.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import cn.mijack.meme.model.Emoji;
import cn.mijack.meme.model.TokenEntity;
import cn.mijack.meme.model.VideoInfo;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.remote.Result;
import io.reactivex.Observable;

/**
 * @author admin
 * @date 2017/6/16
 */

public class MemeViewModel extends BaseViewModel {
    LiveData<ApiResponse<Result<List<Emoji>>>> emojiLiveData;
    private MutableLiveData<String> emojiUrlLiveData;

    public MemeViewModel() {
        emojiUrlLiveData = new MutableLiveData<>();
    }

    public LiveData<ApiResponse<Result<List<Emoji>>>> loadEmoji() {
        if (emojiLiveData == null) {
            emojiLiveData = getApiService().listEmoji();
        }
        return emojiLiveData;
    }

    public void setEmojiUrl(String emojiUrl) {
        emojiUrlLiveData.setValue(emojiUrl);
    }

    public LiveData<String> getEmojiUrlLiveData() {
        return emojiUrlLiveData;
    }

    public Observable<Result<TokenEntity>> requestToken(int uid, String vId, long progess, String title, String shortTitle,VideoInfo videoInfo) {
        return getApiService().requestToken(uid,vId,progess,title,shortTitle,videoInfo);
    }
}
