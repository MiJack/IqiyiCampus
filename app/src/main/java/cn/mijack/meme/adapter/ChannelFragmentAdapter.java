package cn.mijack.meme.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.mijack.meme.fragment.ChannelDetailFragment;
import cn.mijack.meme.model.ChannelEntity;

/**
 * @author Mr.Yuan
 * @date 2017/6/3
 */
public class ChannelFragmentAdapter extends FragmentStatePagerAdapter {
    private List<ChannelEntity.Channel> channels = new ArrayList<>();
    private SparseArray<ChannelDetailFragment> channelDetailFragmentArray = new SparseArray<>();

    public ChannelFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return channels.get(position).name;
    }

    @Override
    public Fragment getItem(int index) {
        ChannelDetailFragment fragment = channelDetailFragmentArray.get(index);
        if (fragment == null) {
            fragment = ChannelDetailFragment.newInstantiate(channels.get(index));
            channelDetailFragmentArray.put(index, fragment);
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        channelDetailFragmentArray.remove(position);
    }

    @Override
    public int getCount() {
        return channels.size();
    }

    public void setChannels(ChannelEntity entity) {
        this.channels.clear();
        if (entity != null && entity.channelList != null) {
            this.channels.addAll(entity.channelList);
        }
        this.notifyDataSetChanged();
    }
}
