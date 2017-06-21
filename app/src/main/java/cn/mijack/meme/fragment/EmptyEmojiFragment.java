package cn.mijack.meme.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.mijack.meme.R;
import cn.mijack.meme.vm.MemeViewModel;

/**
 * @author Mr.Yuan
 * @date 2017/6/21
 */
public class EmptyEmojiFragment extends Fragment implements View.OnClickListener {
    private Button btn;
    private TextView tip;
    private ProgressBar progressBar;
    private MemeViewModel memeViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty_emoji, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btn = (Button) view.findViewById(R.id.btn);
        tip = (TextView) view.findViewById(R.id.tip);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        btn.setOnClickListener(this);
        memeViewModel = ViewModelProviders.of(getActivity()).get(MemeViewModel.class);
        progressBar.setVisibility(View.GONE);
        btn.setVisibility(View.VISIBLE);
        tip.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        btn.setVisibility(View.GONE);
        tip.setVisibility(View.GONE);
        progressBar.setProgress(View.VISIBLE);
        memeViewModel.loadEmoji();
    }
}
