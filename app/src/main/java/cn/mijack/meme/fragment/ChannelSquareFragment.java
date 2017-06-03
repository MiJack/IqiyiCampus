package cn.mijack.meme.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cn.mijack.meme.R;
import cn.mijack.meme.adapter.ChannelFragmentAdapter;
import cn.mijack.meme.base.BaseFragment;
import cn.mijack.meme.model.ChannelEntity;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.vm.ChannelSquareViewModel;

/**
 * @author Mr.Yuan
 * @date 2017/6/3
 */
public class ChannelSquareFragment extends BaseFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ChannelFragmentAdapter adapter;
    ChannelSquareViewModel channelSquareViewModel;
    Observer<ApiResponse<ChannelEntity>> observer = channelEntityApiResponse -> {
        adapter.setChannels(channelEntityApiResponse.body);
        if (!TextUtils.isEmpty(channelEntityApiResponse.errorReason)) {
            Toast.makeText(getActivity(), channelEntityApiResponse.errorReason, Toast.LENGTH_SHORT).show();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_channel_square, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabLayout);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        adapter = new ChannelFragmentAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        channelSquareViewModel = ViewModelProviders.of(this).get(ChannelSquareViewModel.class);
        channelSquareViewModel.loadChannels(getActivity()).observe(this, observer);
    }
}
