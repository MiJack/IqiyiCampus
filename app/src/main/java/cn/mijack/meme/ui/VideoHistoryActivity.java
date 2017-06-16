package cn.mijack.meme.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.adapter.HistoryAdapter;
import cn.mijack.meme.base.BaseActivity;
import cn.mijack.meme.model.HistoryEntity;
import cn.mijack.meme.vm.VideoHistoryViewModel;

/**
 * @author Mr.Yuan
 * @date 2017/6/8
 */
public class VideoHistoryActivity extends BaseActivity {
    VideoHistoryViewModel videoHistoryViewModel;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private LinearLayoutManager linearLayoutManager;
    Observer<List<HistoryEntity>> observer = historyEntities -> {
        historyAdapter.setData(historyEntities);
    };
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_history);
        videoHistoryViewModel = ViewModelProviders.of(this).get(VideoHistoryViewModel.class);
        videoHistoryViewModel.loadVideos().observe(this, observer);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        historyAdapter = new HistoryAdapter();
        recyclerView.setAdapter(historyAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //todo 添加点击播放

    }
}
