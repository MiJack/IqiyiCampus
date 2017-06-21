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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author admin
 * @date 2017/6/16
 */

public class MemeViewModel extends BaseViewModel {
    MutableLiveData<Result<List<Emoji>>> emojiLiveData = new MutableLiveData<>();
    private MutableLiveData<String> emojiUrlLiveData = new MutableLiveData<>();

    public MemeViewModel() {
    }

    public LiveData<Result<List<Emoji>>> loadEmoji() {
        getApiService().listEmoji()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResult -> emojiLiveData.setValue(listResult));
        return emojiLiveData;
    }

    public void setEmojiUrl(String emojiUrl) {
        emojiUrlLiveData.setValue(emojiUrl);
    }

    public LiveData<String> getEmojiUrlLiveData() {
        return emojiUrlLiveData;
    }

    public Observable<Result<TokenEntity>> requestToken(int uid, String vId, long progess, String title, String shortTitle, VideoInfo videoInfo) {
        return getApiService().requestToken(uid, vId, progess, title, shortTitle, videoInfo);
    }
}
