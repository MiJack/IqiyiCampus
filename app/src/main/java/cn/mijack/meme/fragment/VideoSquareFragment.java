package cn.mijack.meme.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cn.mijack.meme.R;
import cn.mijack.meme.adapter.RecommendAdapter;
import cn.mijack.meme.base.BaseFragment;
import cn.mijack.meme.model.RecommendEntity;
import cn.mijack.meme.remote.ApiParamsGen;
import cn.mijack.meme.remote.ApiService;
import cn.mijack.meme.remote.RetrofitClient;
import cn.mijack.meme.utils.NetWorkTypeUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Mr.Yuan
 * @date 2017/5/25
 */
public class VideoSquareFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final int DEFAULT_PAGE_SIZE = 30;
    RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private RecommendAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_square, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), RecommendAdapter.ROW_NUM);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecommendAdapter(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        loadData();
    }

    private void loadData() {
        if (!NetWorkTypeUtils.isNetAvailable(getActivity())) {
            refreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
        RetrofitClient client = RetrofitClient.getInstance();
        ApiService apiService  =client.createApi(ApiService.class);
        apiService.qiyiRecommendDetail(ApiParamsGen.genRecommendDetailParams(getActivity(), 0, DEFAULT_PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<RecommendEntity>() {

                    @Override
                    public void onNext(RecommendEntity recommendEntity) {
                        mAdapter.setData(recommendEntity);
                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void onRefresh() {
        loadData();
    }
}
