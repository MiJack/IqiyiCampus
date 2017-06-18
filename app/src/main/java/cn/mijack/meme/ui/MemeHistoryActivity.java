package cn.mijack.meme.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.adapter.MemeAdapter;
import cn.mijack.meme.base.BaseActivity;
import cn.mijack.meme.model.MemeEntity;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.remote.Result;
import cn.mijack.meme.user.UserManager;
import cn.mijack.meme.utils.Utils;
import cn.mijack.meme.view.MarginItemDecoration;
import cn.mijack.meme.vm.MemeHistoryViewModel;

/**
 * @author Mr.Yuan
 * @date 2017/6/8
 */
public class MemeHistoryActivity extends BaseActivity {
    MemeHistoryViewModel memeHistoryViewModel;
    private RecyclerView recyclerView;
    private MemeAdapter memeAdapter;
    private LinearLayoutManager linearLayoutManager;
    Observer<ApiResponse<Result<List<MemeEntity>>>> observer = (historyEntities) -> {
        if (historyEntities != null && historyEntities.body != null) {
            memeAdapter.setData(historyEntities.body.getData());
        } else {
            Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
        }
    };
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_history);
        memeHistoryViewModel = ViewModelProviders.of(this).get(MemeHistoryViewModel.class);
        memeHistoryViewModel.loadMyMemes(UserManager.get(this).getUser().getUid()).observe(this, observer);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        MarginItemDecoration marginItemDecoration = new MarginItemDecoration(Utils.dipToPixels(this, 8));
        marginItemDecoration.setColor(ContextCompat.getColor(this, R.color.colorDivider));
        recyclerView.addItemDecoration(marginItemDecoration);
        memeAdapter = new MemeAdapter(MemeAdapter.TYPE_HISTORY);
        recyclerView.setAdapter(memeAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
