package cn.mijack.meme.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cn.mijack.meme.R;
import cn.mijack.meme.adapter.EmojiPageAdapter;
import cn.mijack.meme.base.BaseActivity;
import cn.mijack.meme.model.Emoji;
import cn.mijack.meme.model.VideoInfo;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.remote.Result;
import cn.mijack.meme.utils.Utils;
import cn.mijack.meme.view.MemeView;
import cn.mijack.meme.vm.MemeViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.relex.circleindicator.CircleIndicator;

public class MemeActivity extends BaseActivity {
    private static final int SPAN_COUNT = 8;
    VideoInfo videoInfo;
    private long progess;
    //    RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private MemeViewModel memeViewModel;
    //    private EmojiAdapter emojiAdapter;
    private EmojiPageAdapter emojiPageAdapter;

    private Observer<ApiResponse<Result<List<Emoji>>>> dataObserver = emojiResult -> {
        if (emojiResult == null) {
            Toast.makeText(this, "请求异常", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Emoji> data = emojiResult.body.getData();
//        emojiAdapter.setData(data);
        emojiPageAdapter.setEmojis(data);
    };
    private ViewPager viewPager;
    private MemeView memeView;
    Observer<String> emojiObserver = url -> {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        memeView.setEmojiBitmapUrl(url);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        if (!intent.hasExtra("image")) {
            finish();
            return;
        }
        memeViewModel = ViewModelProviders.of(this).get(MemeViewModel.class);

        videoInfo = (VideoInfo) intent.getSerializableExtra("videoInfo");
        memeView = (MemeView) findViewById(R.id.memeView);
        progess = intent.getLongExtra("progress", -1);
        layoutManager = new GridLayoutManager(this, SPAN_COUNT, GridLayoutManager.HORIZONTAL, false);
        emojiPageAdapter = new EmojiPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(emojiPageAdapter);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
//        viewpager.setAdapter(mPageAdapter);
        indicator.setViewPager(viewPager);
        emojiPageAdapter.registerDataSetObserver(indicator.getDataSetObserver());
        final ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
        layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewPager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;
        memeViewModel.loadEmoji().observe(this, dataObserver);

        memeViewModel.getEmojiUrlLiveData().observe(this, emojiObserver);
        String bitmap = intent.getStringExtra("image");
        Observable.just(bitmap)
                .subscribeOn(Schedulers.io())
                .map(s -> {
                    FileInputStream fileInputStream = openFileInput(s);
                    return BitmapFactory.decodeStream(fileInputStream);
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap1 -> memeView.setImageBitmap(bitmap1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meme,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
