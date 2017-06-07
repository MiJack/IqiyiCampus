package cn.mijack.meme.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.mijack.meme.R;
import cn.mijack.meme.base.BaseFragment;

/**
 * @author Mr.Yuan
 * @date 2017/5/25
 */
public class AccountFragment extends BaseFragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account,container,false);
    }
}
