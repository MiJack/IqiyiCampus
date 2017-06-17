package cn.mijack.meme.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.base.BaseFragment;
import cn.mijack.meme.model.Emoji;
import cn.mijack.meme.view.NoScrollGridLayoutManager;

/**
 * @author admin
 * @date 2017/6/16
 */

public class EmojiPageAdapter extends FragmentStatePagerAdapter {
    public static final String TAG = "EmojiPageAdapter";
    List<Emoji> emojis = new ArrayList<>();
    private int SIZE_PER_PAGE = 4 * 6;
    SparseArray<ArrayList<Emoji>> dataArray = new SparseArray<>();
    SparseArray<Fragment> sparseArray = new SparseArray<>();

    public EmojiPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = sparseArray.get(i);
        if (fragment == null) {
            fragment = new EmojiFragment();
            Bundle args = new Bundle();
            args.putParcelableArrayList("data", dataArray.get(i));
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        if (emojis == null) {
            return 0;
        }
        int i = emojis.size() % SIZE_PER_PAGE;
        int count = emojis.size() / SIZE_PER_PAGE;
        return i == 0 ? count : (count + 1);
    }

    public void setEmojis(List<Emoji> emojis) {
        this.emojis.clear();
        this.emojis.addAll(emojis);
        int pageCount = getCount();
        dataArray.clear();
        int emojiSize = emojis.size();
        Log.d(TAG, "setEmojis: " + pageCount);
        for (int index = 0; index < pageCount; index++) {
            ArrayList<Emoji> data = new ArrayList<>();
            int start = index * SIZE_PER_PAGE;
            for (int i = 0; i < SIZE_PER_PAGE; i++) {
                if (start + i < emojiSize) {
                    data.add(emojis.get(start + i));
                }
            }
            dataArray.put(index, data);
        }
        this.notifyDataSetChanged();
    }

    public static class EmojiFragment extends BaseFragment {
        RecyclerView recyclerView;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_emoji, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            EmojiAdapter emojiAdapter = new EmojiAdapter(getActivity());
            emojiAdapter.setData(getArguments().getParcelableArrayList("data"));
            recyclerView.setAdapter(emojiAdapter);
            recyclerView.setLayoutManager(new NoScrollGridLayoutManager(recyclerView));
        }
    }
}
