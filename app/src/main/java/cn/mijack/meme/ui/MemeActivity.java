package cn.mijack.meme.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.adapter.EmojiAdapter;
import cn.mijack.meme.adapter.EmojiPageAdapter;
import cn.mijack.meme.base.BaseActivity;
import cn.mijack.meme.model.Emoji;
import cn.mijack.meme.model.VideoInfo;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.remote.Result;
import cn.mijack.meme.vm.MemeViewModel;

public class MemeActivity extends BaseActivity {
    private static final int SPAN_COUNT = 8;
    VideoInfo videoInfo;
    private long progess;
//    RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private MemeViewModel memeViewModel;
//    private EmojiAdapter emojiAdapter;
    private EmojiPageAdapter emojiPageAdapter;

    private Observer<ApiResponse<Result<List<Emoji>>>> observer = emojiResult -> {
        if (emojiResult ==null){
            Toast.makeText(this, "请求异常", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Emoji> data = emojiResult.body.getData();
//        emojiAdapter.setData(data);
        emojiPageAdapter.setEmojis(data);
    };
    private ViewPager viewPager;


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
        progess = intent.getLongExtra("progress", -1);
        layoutManager = new GridLayoutManager(this, SPAN_COUNT, GridLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        emojiAdapter = new EmojiAdapter();
//        recyclerView.setAdapter(emojiAdapter);
        emojiPageAdapter = new EmojiPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(emojiPageAdapter);

        final ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
        layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewPager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;

//        final ViewPagerIndicator viewPagerIndicator = new ViewPagerIndicator(context);
//        viewPager.addView(viewPagerIndicator, layoutParams);
        memeViewModel.loadEmoji().observe(this,observer);
        String bitmap = intent.getStringExtra("image");
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    FileInputStream fileInputStream = openFileInput(strings[0]);
                    return BitmapFactory.decodeStream(fileInputStream);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                ImageView view = (ImageView) findViewById(R.id.memeView);
                view.setImageBitmap(bitmap);
            }
        }.execute(bitmap);

    }

}
