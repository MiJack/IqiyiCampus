package cn.mijack.meme.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import cn.mijack.meme.base.BaseFragment;
import cn.mijack.meme.fragment.AccountFragment;
import cn.mijack.meme.fragment.MemeSquareFragment;
import cn.mijack.meme.fragment.VideoSquareFragment;

/**
 * @author Mr.Yuan
 * @date 2017/5/25
 */
public class MainAdapter extends FragmentStatePagerAdapter {
    public static final Class<? extends BaseFragment>[] FRAGMENT_CLASSES = new Class[]{
            VideoSquareFragment.class,
            MemeSquareFragment.class,
            AccountFragment.class
    };
    BaseFragment[] baseFragments = new BaseFragment[FRAGMENT_CLASSES.length];

    public MainAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if (i >= 0 && i < 3) {
            if (baseFragments[i] != null) {
                return baseFragments[i];
            }
            try {
                BaseFragment baseFragment = FRAGMENT_CLASSES[i].newInstance();
                baseFragments[i] = baseFragment;
                return baseFragment;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalArgumentException("i should >=0 and <3");
    }

    @Override
    public int getCount() {
        return FRAGMENT_CLASSES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return FRAGMENT_CLASSES[position].getSimpleName();
    }
}
