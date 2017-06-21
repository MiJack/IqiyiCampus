package cn.mijack.meme.fragment;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.adapter.EmojiPageAdapter;
import cn.mijack.meme.base.BaseFragment;
import cn.mijack.meme.model.Emoji;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.remote.Result;
import cn.mijack.meme.view.MemeView;
import cn.mijack.meme.vm.MemeViewModel;
import me.relex.circleindicator.CircleIndicator;

/**
 * @author Mr.Yuan
 * @date 2017/6/21
 */
public class EmojiContainerFragment extends BaseFragment {
    private EmojiPageAdapter emojiPageAdapter;
    private Observer<Result<List<Emoji>>> dataObserver = emojiResult -> {
        if (emojiResult == null) {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
            return;
        }
        List<Emoji> data = emojiResult.getData();
        emojiPageAdapter.setEmojis(data);
    };
    private ViewPager viewPager;
    private MemeViewModel memeViewModel;
    private CircleIndicator indicator;
    DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            int count = viewPager.getAdapter().getCount();
            indicator.setVisibility(count <= 1 ? View.INVISIBLE : View.VISIBLE);
        }

        @Override
        public void onInvalidated() {
            indicator.setVisibility(View.INVISIBLE);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_emoji_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        emojiPageAdapter = new EmojiPageAdapter(getFragmentManager());
        viewPager.setAdapter(emojiPageAdapter);
        indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        emojiPageAdapter.registerDataSetObserver(indicator.getDataSetObserver());
        emojiPageAdapter.registerDataSetObserver(dataSetObserver);
        memeViewModel = ViewModelProviders.of(this).get(MemeViewModel.class);
        memeViewModel.loadEmoji().observe((LifecycleOwner) getActivity(), dataObserver);
    }
}
