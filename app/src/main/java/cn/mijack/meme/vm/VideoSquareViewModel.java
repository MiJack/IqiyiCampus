package cn.mijack.meme.vm;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.UiThread;

import cn.mijack.meme.model.RecommendEntity;
import cn.mijack.meme.remote.ApiParamsGen;
import cn.mijack.meme.remote.ApiResponse;

/**
 * @author Mr.Yuan
 * @date 2017/6/3
 */
public class VideoSquareViewModel extends BaseViewModel {

    private LiveData<ApiResponse<RecommendEntity>> recommendDetailData;

    @UiThread
    public LiveData<ApiResponse<RecommendEntity>> getRecommendDetail(Context context) {
        if (recommendDetailData == null) {
            recommendDetailData = iqiyiApiService.recommendDetail(ApiParamsGen.genRecommendDetailParams(context, 0, DEFAULT_PAGE_SIZE));
        }
        return recommendDetailData;
    }

    @UiThread
    public LiveData<ApiResponse<RecommendEntity>> reloadRecommendDetail(Context context) {
        recommendDetailData = iqiyiApiService.recommendDetail(ApiParamsGen.genRecommendDetailParams(context, 0, DEFAULT_PAGE_SIZE));
        return recommendDetailData;
    }
}
