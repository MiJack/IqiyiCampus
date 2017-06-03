package cn.mijack.meme.vm;

import android.arch.lifecycle.ViewModel;

import cn.mijack.meme.remote.ApiService;
import cn.mijack.meme.remote.RetrofitClient;

/**
 * @author Mr.Yuan
 * @date 2017/6/4
 */
public class MemeViewModel extends ViewModel {
    public static final int DEFAULT_PAGE_SIZE = 30;
    protected ApiService apiService;

    public MemeViewModel() {
        RetrofitClient client = RetrofitClient.getInstance();
        apiService = client.createApi(ApiService.class);
    }
}
