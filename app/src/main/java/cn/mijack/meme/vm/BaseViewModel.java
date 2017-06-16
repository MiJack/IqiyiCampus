package cn.mijack.meme.vm;

import android.arch.lifecycle.ViewModel;

import cn.mijack.meme.remote.ApiService;
import cn.mijack.meme.remote.IqiyiApiService;
import cn.mijack.meme.remote.RetrofitClient;

/**
 * @author Mr.Yuan
 * @date 2017/6/4
 */
public class BaseViewModel extends ViewModel {
    public static final int DEFAULT_PAGE_SIZE = 30;
    private IqiyiApiService iqiyiApiService;
    private ApiService apiService;

    public BaseViewModel() {
        RetrofitClient client = RetrofitClient.getInstance();
        iqiyiApiService = client.createApi(IqiyiApiService.class);
        apiService = client.createApi(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }

    public IqiyiApiService getIqiyiApiService() {
        return iqiyiApiService;
    }
}
