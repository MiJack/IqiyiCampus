package cn.mijack.meme.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import cn.mijack.meme.R;
import cn.mijack.meme.base.BaseActivity;
import cn.mijack.meme.fragment.AccountFragment;
import cn.mijack.meme.fragment.ChannelSquareFragment;
import cn.mijack.meme.fragment.MemeSquareFragment;
import cn.mijack.meme.fragment.VideoSquareFragment;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemReselectedListener {

    private AppBarLayout appbar;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private VideoSquareFragment videoSquareFragment;
    private ChannelSquareFragment channelSquareFragment;
    private MemeSquareFragment memeSquareFragment;
    private AccountFragment accountFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        setSupportActionBar(toolbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);
        selectFragment(R.id.m1);
    }

    private void selectFragment(int fragmentId) {
        FragmentTransaction transaction;
        switch (fragmentId) {
            case R.id.m1:
                transaction = getSupportFragmentManager().beginTransaction();
                if (videoSquareFragment == null) {
                    videoSquareFragment = createFragment(transaction, VideoSquareFragment.class);
                }
                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }
                transaction.show(videoSquareFragment);
                currentFragment = videoSquareFragment;
                transaction.commit();
                break;
            case R.id.m2:
                transaction = getSupportFragmentManager().beginTransaction();
                if (channelSquareFragment == null) {
                    channelSquareFragment = createFragment(transaction, ChannelSquareFragment.class);
                }
                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }
                transaction.show(channelSquareFragment);
                currentFragment = channelSquareFragment;
                transaction.commit();
                break;
            case R.id.m3:
                transaction = getSupportFragmentManager().beginTransaction();
                if (memeSquareFragment == null) {
                    memeSquareFragment = createFragment(transaction, MemeSquareFragment.class);
                }
                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }
                transaction.show(memeSquareFragment);
                currentFragment = memeSquareFragment;
                transaction.commit();
                break;
            case R.id.m4:
                transaction = getSupportFragmentManager().beginTransaction();
                if (accountFragment == null) {
                    accountFragment = createFragment(transaction, AccountFragment.class);
                }
                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }
                transaction.show(accountFragment);
                currentFragment = accountFragment;
                transaction.commit();
                break;
        }
    }

    private <T extends Fragment> T createFragment(FragmentTransaction transaction, Class<T> fragmentClass) {
        T fragment = (T) getSupportFragmentManager().findFragmentByTag(fragmentClass.getName());
        if (fragment == null) {
            try {
                fragment = fragmentClass.newInstance();
                transaction.add(R.id.container, fragment, fragmentClass.getName());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fragment;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        selectFragment(menuItem.getItemId());
        return true;
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
        if (currentFragment instanceof IReload) {
            ((IReload) currentFragment).reload();
        }
    }
}
