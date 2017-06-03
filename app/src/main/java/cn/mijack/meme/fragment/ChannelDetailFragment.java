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
import cn.mijack.meme.adapter.ChannelDetailAdapter;
import cn.mijack.meme.adapter.RecommendAdapter;
import cn.mijack.meme.base.BaseFragment;
import cn.mijack.meme.model.ChannelDetailEntity;
import cn.mijack.meme.model.ChannelEntity;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.vm.ChannelDetailViewModel;

/**
 * @author Mr.Yuan
 * @date 2017/6/3
 */
public class ChannelDetailFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String CHANNEL_ID = "channel_id";
    public static final String CHANNEL_NAME = "channel_name";
    public static final String CHANNEL_DESCRIBE = "channel_describe";
    public String channelId;
    public String channelName;
    public String channelDescribe;
    ChannelDetailViewModel channelDetailViewModel;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ChannelDetailAdapter channelDetailAdapter;
    private Observer<ApiResponse<ChannelDetailEntity>> observer = channelDetailEntityApiResponse -> {
        refreshLayout.setRefreshing(false);
        if (channelDetailEntityApiResponse != null && channelDetailEntityApiResponse.body != null) {
            channelDetailAdapter.setData(channelDetailEntityApiResponse.body.dataEntity);
        }
        if (!TextUtils.isEmpty(channelDetailEntityApiResponse.errorReason)) {
            Toast.makeText(getActivity(), channelDetailEntityApiResponse.errorReason, Toast.LENGTH_SHORT).show();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_channel_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        channelId = args.getString(CHANNEL_ID, null);
        channelName = args.getString(CHANNEL_NAME, null);
        channelDescribe = args.getString(CHANNEL_DESCRIBE, null);
        channelDetailViewModel = ViewModelProviders.of(this).get(ChannelDetailViewModel.class);
        channelDetailViewModel.loadChannel(getActivity(), channelId, channelName)
                .observe(this, observer);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), RecommendAdapter.ROW_NUM);
        // TODO: 2017/6/4  setSpanSizeLookup
        recyclerView.setLayoutManager(layoutManager);
        channelDetailAdapter = new ChannelDetailAdapter();
        recyclerView.setAdapter(channelDetailAdapter);
        refreshLayout.setOnRefreshListener(this);
    }

    public static ChannelDetailFragment newInstantiate(ChannelEntity.Channel channel) {
        ChannelDetailFragment channelDetailFragment = new ChannelDetailFragment();
        Bundle args = new Bundle();
        args.putString(CHANNEL_ID, channel.id);
        args.putString(CHANNEL_NAME, channel.name);
        args.putString(CHANNEL_DESCRIBE, channel.describe);
        channelDetailFragment.setArguments(args);
        return channelDetailFragment;
    }

    @Override
    public void onRefresh() {

    }
}
