package cn.mijack.meme.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.adapter.MemeAdapter;
import cn.mijack.meme.base.BaseFragment;
import cn.mijack.meme.model.MemeEntity;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.remote.Result;
import cn.mijack.meme.utils.Utils;
import cn.mijack.meme.view.MarginItemDecoration;
import cn.mijack.meme.vm.MemeSquareViewModel;

/**
 * @author Mr.Yuan
 * @date 2017/5/25
 */
public class MemeSquareFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private MemeSquareViewModel memeSquareViewModel;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MemeAdapter memeAdapter;
    private Observer<ApiResponse<Result<List<MemeEntity>>>> observer = entityApiResponse -> {
        refreshLayout.setRefreshing(false);
        if (entityApiResponse != null && entityApiResponse.body != null) {
            memeAdapter.setData(entityApiResponse.body.getData());
        }
        if (!TextUtils.isEmpty(entityApiResponse.errorReason)) {
            Toast.makeText(getActivity(), entityApiResponse.errorReason, Toast.LENGTH_SHORT).show();
        }
    };
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meme_square, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        memeSquareViewModel = ViewModelProviders.of(this).get(MemeSquareViewModel.class);
        memeSquareViewModel.load()
                .observe(this, observer);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        MarginItemDecoration marginItemDecoration = new MarginItemDecoration(Utils.dipToPixels(getActivity(), 8));
        marginItemDecoration.setColor(ContextCompat.getColor(getActivity(),R.color.colorDivider));
        recyclerView.addItemDecoration(marginItemDecoration);
        memeAdapter = new MemeAdapter(MemeAdapter.TYPE_SQUARE);
        recyclerView.setAdapter(memeAdapter);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        memeSquareViewModel.reload()
                .observe(this, observer);
    }

}
