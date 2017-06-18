package cn.mijack.meme.ui;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iqiyi.player.nativemediaplayer.MediaPlayerState;
import com.qiyi.video.playcore.ErrorCode;
import com.qiyi.video.playcore.IQYPlayerHandlerCallBack;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.adapter.MemeAdapter;
import cn.mijack.meme.base.BaseActivity;
import cn.mijack.meme.dao.HistoryDatabase;
import cn.mijack.meme.model.HistoryEntity;
import cn.mijack.meme.model.MemeEntity;
import cn.mijack.meme.model.VideoInfo;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.remote.Result;
import cn.mijack.meme.utils.LogUtils;
import cn.mijack.meme.utils.Utils;
import cn.mijack.meme.view.MemeVideoView;
import cn.mijack.meme.vm.PlayerViewModel;

/**
 * Created by zhouxiaming on 2017/5/9.
 * Changed by MiJack
 */

public class PlayerActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 7171;
    private static final String TAG = PlayerActivity.class.getSimpleName();

    private static final int HANDLER_MSG_UPDATE_PROGRESS = 1;
    private static final int HANDLER_DEPLAY_UPDATE_PROGRESS = 1000; // 1s
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_MEDIA_PROJECTION = 2;
    private static final int REQUEST_CODE_CHANGE_PERMISSION = 3;
    private static final int REQUEST_CODE_UPLOAD_MEME = 4;

    private MemeVideoView mVideoView;
    private SeekBar mSeekBar;
    private TextView mCurrentTime;
    private TextView mTotalTime;
    PlayerViewModel playerViewModel;
    IQYPlayerHandlerCallBack mCallBack = new IQYPlayerHandlerCallBack() {
        /**
         * SeekTo 成功，可以通过该回调获取当前准确时间点。
         */
        @Override
        public void OnSeekSuccess(long l) {
            LogUtils.i(TAG, "OnSeekSuccess: " + l);
            //更新时间
            if (Utils.isUiThread()) {
                renderTime();
            } else {
                mMainHandler.sendEmptyMessage(HANDLER_MSG_UPDATE_PROGRESS);
            }
        }

        /**
         * 是否因数据加载而暂停播放
         */
        @Override
        public void OnWaiting(boolean b) {
            LogUtils.i(TAG, "OnWaiting: " + b);
        }

        /**
         * 播放内核发生错误
         */
        @Override
        public void OnError(ErrorCode errorCode) {
            LogUtils.i(TAG, "OnError: " + errorCode);
            mMainHandler.removeMessages(HANDLER_MSG_UPDATE_PROGRESS);
        }

        /**
         * 播放器状态码 {@link com.iqiyi.player.nativemediaplayer.MediaPlayerState}
         * 0	空闲状态
         * 1	已经初始化
         * 2	调用PrepareMovie，但还没有进入播放
         * 4    可以获取视频信息（比如时长等）
         * 8    广告播放中
         * 16   正片播放中
         * 32	一个影片播放结束
         * 64	错误
         * 128	播放结束（没有连播）
         */
        @Override
        public void OnPlayerStateChanged(int i) {
            if (i == MediaPlayerState.MPS_Prepared) {
                renderTime();
            }
            if (i == MediaPlayerState.MPS_MoviePlaying && currentPosition > 0) {
                if (currentPosition < mVideoView.getDuration()) {
                    mVideoView.seekTo(currentPosition);
                }
                currentPosition = -1;
            }
            LogUtils.i(TAG, "OnPlayerStateChanged: " + i);
        }
    };
    private String aid;
    private String tid;
    private int currentPosition = -1;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mediaProjection;
    private ImageReader mImageReader;
    private ImageView mPlayPauseView;
    private ImageView mMemeButton;
    private LinearLayout controlBack;
    private ImageView mFullScreenView;
    private ConstraintLayout mConstraintLayout;
    private boolean mIsLandscape;
    private Handler mMainHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            LogUtils.d(TAG, "handleMessage, msg.what = " + msg.what);
            switch (msg.what) {
                case HANDLER_MSG_UPDATE_PROGRESS:
                    renderTime();
                    mMainHandler.sendEmptyMessageDelayed(HANDLER_MSG_UPDATE_PROGRESS, HANDLER_DEPLAY_UPDATE_PROGRESS);
                    break;
                default:
                    break;
            }
        }
    };
    private ContentObserver rotationObserver = new ContentObserver(mMainHandler) {
        @Override
        public void onChange(boolean selfChange) {
            if (selfChange) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }
        }
    };
    private VideoInfo videoInfo;
    private RecyclerView recyclerView;
    HistoryDatabase db;
    private MemeAdapter memeAdapter;

    Observer<ApiResponse<Result<List<MemeEntity>>>> observer =
            (resultApiResponse) -> {
                if (resultApiResponse != null && resultApiResponse.body != null) {
                    memeAdapter.setData(resultApiResponse.body.getData());
                } else {
                    Toast.makeText(PlayerActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                }
            };
    Observer<Integer> progressObserver =
            progress -> {
                if (mVideoView != null && progress > 0 && progress < mVideoView.getDuration()) {
                    mVideoView.seekTo(progress);
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null || intent.hasExtra("video") == false) {
            finish();
            return;
        }
        db = Room.databaseBuilder(getApplicationContext(),
                HistoryDatabase.class, "database-name").allowMainThreadQueries().build();
        videoInfo = (VideoInfo) intent.getSerializableExtra("video");
        if (videoInfo == null) {
            finish();
            return;
        }
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION),
                true, rotationObserver);
        currentPosition = intent.getIntExtra("time", -1);
        setContentView(R.layout.activity_player_portrait);
        mVideoView = (MemeVideoView) findViewById(R.id.id_videoview);
        mMemeButton = (ImageView) findViewById(R.id.idMeme);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        memeAdapter = new MemeAdapter(MemeAdapter.TYPE_VIDEO);
        recyclerView.setAdapter(memeAdapter);
        playerViewModel = ViewModelProviders.of(this).get(PlayerViewModel.class);
        playerViewModel.getMemeList(videoInfo.tId)
                .observe(this, observer);
        playerViewModel.getProgressData().observe(this, progressObserver);
        //mVideoView.setPlayData("667737400");
        mVideoView.setPlayData(videoInfo.tId);
        //设置回调，监听播放器状态
        setPlayerCallback();
        mCurrentTime = (TextView) findViewById(R.id.id_current_time);
        mTotalTime = (TextView) findViewById(R.id.id_total_time);
        controlBack = (LinearLayout) findViewById(R.id.controlBack);
        mPlayPauseView = (ImageView) findViewById(R.id.idPlayPause);
        mFullScreenView = (ImageView) findViewById(R.id.id_full_screen);
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
//        setUpView();
        mFullScreenView.setOnClickListener(v -> {
            Log.d(TAG, "click:  mFullScreenView");
            if (mIsLandscape == false) {
                //竖屏
                mFullScreenView.setImageResource(R.drawable.ic_fullscreen_exit);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                //横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mFullScreenView.setImageResource(R.drawable.ic_fullscreen);
            }
        });
        mPlayPauseView.setOnClickListener(v -> {
            if (mVideoView.isPlaying()) {
                pauseVideo();
            } else {
                startVideo();
            }
        });
        mVideoView.setOnClickListener(v -> {
            // todo 判断是否在获取截图，是则无反应
            if (controlBack.getVisibility() != View.VISIBLE) {
                controlBack.setVisibility(View.VISIBLE);
            } else {
                controlBack.setVisibility(View.GONE);
            }
        });

        //请求
        mMemeButton.setOnClickListener(v -> {
            pauseVideo();
            AutoTransition sDefaultTransition = new AutoTransition();

            sDefaultTransition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(@NonNull Transition transition) {
                    Log.d(TAG, "onTransitionStart: ");
                }

                @Override
                public void onTransitionEnd(@NonNull Transition transition) {
                    mMainHandler.postDelayed(() -> capture(), 300);
                    Log.d(TAG, "onTransitionEnd: ");
                }

                @Override
                public void onTransitionCancel(@NonNull Transition transition) {
                    Log.d(TAG, "onTransitionCancel: ");
                }

                @Override
                public void onTransitionPause(@NonNull Transition transition) {
                    Log.d(TAG, "onTransitionPause: ");
                }

                @Override
                public void onTransitionResume(@NonNull Transition transition) {
                    Log.d(TAG, "onTransitionResume: ");
                }
            });
            TransitionManager.beginDelayedTransition(mConstraintLayout, sDefaultTransition);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(this, R.layout.activity_player_meme);
            constraintSet.applyTo(mConstraintLayout);
//            DisplayMetrics metric = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(metric);
//            int width = metric.widthPixels - getResources().getDimensionPixelOffset(R.dimen.videoViewMargin) * 2;
//            int height = (int) (width * 9.0 / 16);
//            mVideoView.setVideoViewSize(width, height);
            controlBack.setVisibility(View.GONE);
        });

        mSeekBar = (SeekBar) findViewById(R.id.id_progress);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int mProgress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LogUtils.d(TAG, "onProgressChanged, progress = " + progress + ", fromUser = " + fromUser);
                if (fromUser) {
                    mProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSeekBar.setProgress(mProgress);
                mVideoView.seekTo(mProgress);
            }
        });
    }

    private void startVideo() {
        setUpView();
        mVideoView.start();
        mPlayPauseView.setImageResource(R.drawable.ic_pause);
        mMainHandler.sendEmptyMessageDelayed(HANDLER_MSG_UPDATE_PROGRESS, HANDLER_DEPLAY_UPDATE_PROGRESS);
    }

    private void setPlayerCallback() {
        mVideoView.setPlayerCallBack(mCallBack);
    }

    private void pauseVideo() {
        mVideoView.pause();
        mPlayPauseView.setImageResource(R.drawable.ic_play_arrow);
        mMainHandler.removeMessages(HANDLER_MSG_UPDATE_PROGRESS);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("progress", currentPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPosition = savedInstanceState.getInt("progress", -1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != mVideoView) {
            mVideoView.start();
        }
        mMainHandler.sendEmptyMessageDelayed(HANDLER_MSG_UPDATE_PROGRESS, HANDLER_DEPLAY_UPDATE_PROGRESS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mVideoView) {
            mVideoView.pause();
        }
        mMainHandler.removeMessages(HANDLER_MSG_UPDATE_PROGRESS);
    }

    @Override
    public void onBackPressed() {
        if (mIsLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mFullScreenView.setImageResource(R.drawable.ic_fullscreen);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaProjection != null) {
            mediaProjection.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainHandler.removeCallbacksAndMessages(null);
        if (mVideoView != null) {
            mVideoView.release();
            mVideoView = null;
        }
        getContentResolver().unregisterContentObserver(rotationObserver);
    }

    /**
     * Query and update the play progress every 1 second.
     */


    private void renderTime() {
        int duration = mVideoView.getDuration();
        int progress = mVideoView.getCurrentPosition();
        LogUtils.d(TAG, "HANDLER_MSG_UPDATE_PROGRESS, duration = " + duration + ", currentPosition = " + progress);
        if (duration > 0) {
            HistoryEntity entity = new HistoryEntity(videoInfo.id, videoInfo.title, videoInfo.img, videoInfo.aId, videoInfo.tId,
                    System.currentTimeMillis(), progress, duration, new Gson().toJson(videoInfo));
            db.historyDao().insertHistory(entity);
        }
        if (duration > 0) {
            mSeekBar.setMax(duration);
            mSeekBar.setProgress(progress);

            mTotalTime.setText(Utils.formatTime(duration));
            mCurrentTime.setText(Utils.formatTime(progress));
        }
        if (mVideoView.isPlaying()) {
            mPlayPauseView.setImageResource(R.drawable.ic_pause);
        } else {
            mPlayPauseView.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    public void capture() {
        captureImage();
    }

    private void captureImage() {
        if (mImageReader == null) {
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        } else {
//            mImageReader.close();
            handlerImage(mImageReader.acquireLatestImage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //todo 添加了返回的调用
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                return;
            }
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels;
            int height = metric.heightPixels;
            int dpi = metric.densityDpi;
            mediaProjection =
                    mMediaProjectionManager.getMediaProjection(resultCode, data);
            mImageReader = ImageReader.newInstance(width, height, 0x1, 20);
            mediaProjection.createVirtualDisplay("ScreenShout",
                    width, height, dpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);
//            mImageReader.setOnImageAvailableListener(this, null);
            mMainHandler.postDelayed(() -> handlerImage(mImageReader.acquireLatestImage()), 50);
        }
        if (requestCode == REQUEST_CODE_UPLOAD_MEME) {
            startVideo();
        }
    }

    private void handlerImage(Image image) {
        if (image == null) {
            mMainHandler.postDelayed(() -> handlerImage(mImageReader.acquireLatestImage()), 50);
            return;
        }
        new Thread(() -> {
            try {
                DisplayMetrics metric = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metric);
                int width = metric.widthPixels;
                int height = metric.heightPixels;
                final Image.Plane[] planes = image.getPlanes();
                final ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;
                Bitmap mBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                mBitmap.copyPixelsFromBuffer(buffer);
                Bitmap bitmap = Bitmap.createBitmap(mVideoView.getWidth(), mVideoView.getHeight(), Bitmap.Config.ARGB_8888);
                int[] location = new int[2];
                mVideoView.getLocationOnScreen(location);
                int left = location[0];
                int top = location[1];
                System.out.println("left:" + location[0] + " top:" + location[1]);

                for (int i = 0; i < mVideoView.getWidth(); i++) {
                    for (int j = 0; j < mVideoView.getHeight(); j++) {
                        bitmap.setPixel(i, j, mBitmap.getPixel(left + i, top + j));
                    }
                }

                System.out.println("ok");
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String png = "myscreen_" + dateFormat.format(new Date(System.currentTimeMillis())) + ".png";
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, this.openFileOutput(png, Context.MODE_PRIVATE));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(this, MemeActivity.class);
                intent.putExtra("image", png);
                intent.putExtra("videoInfo", videoInfo);
                intent.putExtra("progress", mVideoView.getCurrentPosition());
                startActivityForResult(intent, REQUEST_CODE_UPLOAD_MEME);
                image.close();
                currentPosition=mVideoView.getCurrentPosition();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "截图失败！", Toast.LENGTH_SHORT).show();
                    startVideo();
                });
            }
        }).start();
    }

    private void setUpView() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            //设置全屏即隐藏状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mIsLandscape = true;
            mVideoView.setVideoViewSize(screenWidth, (int) (screenWidth * 9.0 / 16));
            recyclerView.setVisibility(View.GONE);
            ConstraintSet mConstraintSet = new ConstraintSet(); // create a Constraint Set
            mConstraintSet.clone(getBaseContext(), R.layout.activity_player_landscape); //
            mConstraintSet.constrainWidth(R.id.id_videoview, 0);
            mConstraintSet.constrainHeight(R.id.id_videoview, 0);
            mConstraintSet.connect(R.id.id_videoview, ConstraintSet.LEFT, R.id.activity_main, ConstraintSet.LEFT);
            mConstraintSet.connect(R.id.id_videoview, ConstraintSet.TOP, R.id.activity_main, ConstraintSet.TOP);
            mConstraintSet.connect(R.id.id_videoview, ConstraintSet.RIGHT, R.id.activity_main, ConstraintSet.RIGHT);
            mConstraintSet.connect(R.id.id_videoview, ConstraintSet.BOTTOM, R.id.activity_main, ConstraintSet.BOTTOM);
            mConstraintLayout.setConstraintSet(mConstraintSet);
            if (mVideoView != null) {
                mVideoView.setVideoViewSize(screenWidth, screenHeight, true);
//            mVideoView.release();
            }
            mMemeButton.setVisibility(View.VISIBLE);
            //横屏 视频充满全屏
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mFleader.getLayoutParams();
//        layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
//        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
//        mFleader.setLayoutParams(layoutParams);
//        mWebView.setVisibility(View.GONE);
        } else if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //恢复状态栏
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            try {
                mVideoView.setVideoViewSize(screenWidth, (int) (screenWidth * 9.0 / 16));
            } catch (Exception e) {
            }
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            recyclerView.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mIsLandscape = false;
            mMemeButton.setVisibility(View.GONE);
            ConstraintSet mConstraintSet = new ConstraintSet(); // create a Constraint Set
            mConstraintSet.clone(getBaseContext(), R.layout.activity_player_portrait); // get constraints from layout
            mConstraintLayout.setConstraintSet(mConstraintSet);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setUpView();
    }
}