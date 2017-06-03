package cn.mijack.meme;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.UiThread;

import java.util.Map;

import cn.mijack.meme.model.RecommendEntity;
import cn.mijack.meme.remote.ApiParamsGen;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.remote.ApiService;
import cn.mijack.meme.remote.RetrofitClient;

/**
 * @author Mr.Yuan
 * @date 2017/6/3
 */
public class VideoSquareFragmentViewModel extends ViewModel {
    private static final int DEFAULT_PAGE_SIZE = 30;
    private ApiService apiService;
    private LiveData<ApiResponse<RecommendEntity>> recommendDetailData;

    public VideoSquareFragmentViewModel() {
        RetrofitClient client = RetrofitClient.getInstance();
        apiService = client.createApi(ApiService.class);
    }

    @UiThread
    public LiveData<ApiResponse<RecommendEntity>> getRecommendDetail(Context context) {
        if (recommendDetailData == null) {
            recommendDetailData = apiService.recommendDetail(ApiParamsGen.genRecommendDetailParams(context, 0, DEFAULT_PAGE_SIZE));
        }
        return recommendDetailData;
    }

    @UiThread
    public LiveData<ApiResponse<RecommendEntity>> reloadRecommendDetail(Context context) {
        recommendDetailData = apiService.recommendDetail(ApiParamsGen.genRecommendDetailParams(context, 0, DEFAULT_PAGE_SIZE));
        return recommendDetailData;
    }
}
