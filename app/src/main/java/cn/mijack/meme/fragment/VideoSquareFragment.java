package cn.mijack.meme.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cn.mijack.meme.R;
import cn.mijack.meme.vm.VideoSquareViewModel;
import cn.mijack.meme.adapter.RecommendAdapter;
import cn.mijack.meme.base.BaseFragment;
import cn.mijack.meme.model.RecommendEntity;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.ui.IReload;
import cn.mijack.meme.utils.NetWorkTypeUtils;

/**
 * @author Mr.Yuan
 * @date 2017/5/25
 */
public class VideoSquareFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IReload {
    RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private RecommendAdapter mAdapter;
    VideoSquareViewModel videoSquareViewModel;
    private CoordinatorLayout coordinatorLayout;
    private Observer<ApiResponse<RecommendEntity>> observer =
            (ApiResponse<RecommendEntity> apiResponse) -> {
                RecommendEntity recommendEntity = apiResponse.body;
                mAdapter.setData(recommendEntity);
                refreshLayout.setRefreshing(false);
                if (recommendEntity != null && !TextUtils.isEmpty(recommendEntity.errorReason)) {
                    Toast.makeText(getActivity(), apiResponse.errorReason, Toast.LENGTH_SHORT).show();
//                    Snackbar.make(coordinatorLayout, apiResponse.errorReason, Snackbar.LENGTH_SHORT)
//                            .setAction(R.string.retry, v -> onRefresh()).show();
                }
            };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_square, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        videoSquareViewModel = ViewModelProviders.of(this).get(VideoSquareViewModel.class);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
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
        if (!NetWorkTypeUtils.isNetAvailable(getActivity())) {
            refreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
        videoSquareViewModel.getRecommendDetail(getActivity())
                .observe(this, observer);
    }

    @Override
    public void onRefresh() {
        if (!NetWorkTypeUtils.isNetAvailable(getActivity())) {
            refreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
        videoSquareViewModel.reloadRecommendDetail(getActivity())
                .observe(this, observer);
    }

    @Override
    public void reload() {
        refreshLayout.setRefreshing(true);
        onRefresh();
    }
}
