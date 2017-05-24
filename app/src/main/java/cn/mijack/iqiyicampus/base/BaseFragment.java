package cn.mijack.iqiyicampus.base;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.mijack.iqiyicampus.R;

/**
 * @author Mr.Yuan
 * @date 2017/5/25
 */
public class BaseFragment extends LifecycleFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_long, container, false);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(getClass().getSimpleName());
        return view;
    }
}
