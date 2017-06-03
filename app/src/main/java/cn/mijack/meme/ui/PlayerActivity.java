package cn.mijack.meme.ui;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qiyi.video.playcore.ErrorCode;
import com.qiyi.video.playcore.IQYPlayerHandlerCallBack;
import com.qiyi.video.playcore.QiyiVideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cn.mijack.meme.BuildConfig;
import cn.mijack.meme.R;
import cn.mijack.meme.base.BaseActivity;
import cn.mijack.meme.utils.LogUtils;
import cn.mijack.meme.utils.StringUtils;
import cn.mijack.meme.utils.Utils;

/**
 * Created by zhouxiaming on 2017/5/9.
 * Changed by MiJack
 */

public class PlayerActivity extends BaseActivity /*implements ImageReader.OnImageAvailableListener*/ {
    private static final int PERMISSION_REQUEST_CODE = 7171;
    private static final String TAG = PlayerActivity.class.getSimpleName();

    private static final int HANDLER_MSG_UPDATE_PROGRESS = 1;
    private static final int HANDLER_DEPLAY_UPDATE_PROGRESS = 1000; // 1s
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_MEDIA_PROJECTION = 2;
    private static final int REQUEST_CODE_CHANGE_PERMISSION = 3;

    private QiyiVideoView mVideoView;
    private SeekBar mSeekBar;
    private TextView mCurrentTime;
    private TextView mTotalTime;

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
            if (currentPosition != -1) {
                mVideoView.seekTo(currentPosition);
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
    private String mImageName;
    private Bitmap mBitmap;
    private ImageView mPlayPauseView;
    private LinearLayout controlBack;
    private ImageView mFullScreenView;
    private ConstraintLayout mConstraintLayout;
    private boolean mIsLandscape;
    //注册 Settings.System.ACCELEROMETER_ROTATION

    private ContentObserver rotationObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (selfChange) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        aid = getIntent().getStringExtra("aid");
        tid = getIntent().getStringExtra("tid");
        if (StringUtils.isEmpty(tid)) {
            finish();
            return;
        }
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION),
                true, rotationObserver);

        setContentView(R.layout.activity_player);
        mVideoView = (QiyiVideoView) findViewById(R.id.id_videoview);
        //mVideoView.setPlayData("667737400");
        mVideoView.setPlayData(tid);
        //设置回调，监听播放器状态
        setPlayerCallback();
//        mVideoView.OnSeekSuccess();

        mCurrentTime = (TextView) findViewById(R.id.id_current_time);
        mTotalTime = (TextView) findViewById(R.id.id_total_time);
        controlBack = (LinearLayout) findViewById(R.id.controlBack);
        mFullScreenView = (ImageView) findViewById(R.id.id_full_screen);
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        mFullScreenView.setOnClickListener(v -> {
            Log.d(TAG, "click:  mFullScreenView");
            if (mIsLandscape == false) {
                //竖屏
                mFullScreenView.setImageResource(R.drawable.ic_fullscreen_exit);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                //横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mFullScreenView.setImageResource(R.drawable.ic_fullscreen );
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
//            int requestedOrientation = getRequestedOrientation();
//            int orientation = getResources().getConfiguration().orientation;
//            if (requestedOrientation==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
//            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            } else /*if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)*/ {
//                //竖屏
//                mFullScreenView.setImageResource(R.drawable.ic_fullscreen_exit);
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }
        });
        mVideoView.setOnClickListener(v -> {
            if (controlBack.getVisibility() != View.VISIBLE) {
                controlBack.setVisibility(View.VISIBLE);
            } else {
                controlBack.setVisibility(View.GONE);
            }
        });
        mPlayPauseView = (ImageView) findViewById(R.id.idPlayPause);
        mPlayPauseView.setOnClickListener(v -> {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
                mPlayPauseView.setImageResource(R.drawable.ic_play_arrow);
                mMainHandler.removeMessages(HANDLER_MSG_UPDATE_PROGRESS);
            } else {
                mVideoView.start();
                mPlayPauseView.setImageResource(R.drawable.ic_pause);
                mMainHandler.sendEmptyMessageDelayed(HANDLER_MSG_UPDATE_PROGRESS, HANDLER_DEPLAY_UPDATE_PROGRESS);
            }
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

    private void setPlayerCallback() {
        mVideoView.setPlayerCallBack(mCallBack);
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
    protected void onDestroy() {
        super.onDestroy();
        mMainHandler.removeCallbacksAndMessages(null);
        mVideoView.release();
        mVideoView = null;
        getContentResolver().unregisterContentObserver(rotationObserver);
    }

    /**
     * Query and update the play progress every 1 second.
     */
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

    private void renderTime() {
        int duration = mVideoView.getDuration();
        int progress = mVideoView.getCurrentPosition();
        LogUtils.d(TAG, "HANDLER_MSG_UPDATE_PROGRESS, duration = " + duration + ", currentPosition = " + progress);
        if (duration > 0) {
            mSeekBar.setMax(duration);
            mSeekBar.setProgress(progress);

            mTotalTime.setText(ms2hms(duration));
            mCurrentTime.setText(ms2hms(progress));
        }
    }

    /**
     * Convert ms to hh:mm:ss
     *
     * @param millis
     * @return
     */
    private String ms2hms(int millis) {
        String result = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return result;
    }

    public void capture(View view) {
        //请求读取权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this).setTitle("请求权限")
                        .setTitle("截图保持在SD卡中，需要存储权限，请前往设置打开权限重试")
                        .setCancelable(false)
                        .setPositiveButton("确定", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_CODE_CHANGE_PERMISSION);
                            dialog.dismiss();
                        })
                        .setNegativeButton("取消", (dialog, which) -> Toast.makeText(PlayerActivity.this, "权限请求失败，无法截图", Toast.LENGTH_SHORT).show()).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            }
        } else {
            captureImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CHANGE_PERMISSION) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                new AlertDialog.Builder(this).setTitle("请求权限")
                        .setTitle("截图保持在SD卡中，需要存储权限，请前往设置打开权限重试")
                        .setCancelable(false)
                        .setPositiveButton("确定", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_CODE_CHANGE_PERMISSION);
                            dialog.dismiss();
                        })
                        .setNegativeButton("取消", (dialog, which) -> Toast.makeText(PlayerActivity.this, "权限请求失败，无法截图", Toast.LENGTH_SHORT).show()).create().show();
            }
        }
    }

    private void captureImage() {
        if (mImageReader == null) {
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        } else {
            handlerImage(mImageReader.acquireLatestImage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            mImageReader = ImageReader.newInstance(width, height, 0x1, 2);
            mediaProjection.createVirtualDisplay("ScreenShout",
                    width, height, dpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);
//            mImageReader.setOnImageAvailableListener(this, null);
            new Handler().postDelayed(() -> handlerImage(mImageReader.acquireLatestImage()), 50);
        }
    }

    private void handlerImage(Image image) {
        if (image == null) {
            new Handler().postDelayed(() -> handlerImage(mImageReader.acquireLatestImage()), 50);
        }
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        mBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
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
        saveBitmapFile(bitmap);
        image.close();
    }

    private void saveBitmapFile(Bitmap bitmap) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String filePath = getExternalFilesDir(null).getAbsolutePath() + "/screenshots/" + "myscreen_" + dateFormat.format(new Date(System.currentTimeMillis())) + ".png";
        // write bitmap to a file
        File file = new File(filePath);
        FileOutputStream fos = null;
        try {
            File parentFile = file.getParentFile();
            parentFile.mkdirs();
            file.createNewFile();
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            System.out.println("insertImage:" + filePath);
            Log.e(TAG, "captured image: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
    }

@Override
public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
        //设置全屏即隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mIsLandscape = true;

        ConstraintSet mConstraintSet = new ConstraintSet(); // create a Constraint Set
        mConstraintSet.clone(getBaseContext(), R.layout.activity_player); //
        mConstraintSet.constrainWidth(R.id.id_videoview,0);
        mConstraintSet.constrainHeight(R.id.id_videoview,0);
        mConstraintSet.connect(R.id.id_videoview,ConstraintSet.LEFT,R.id.activity_main,ConstraintSet.LEFT);
        mConstraintSet.connect(R.id.id_videoview,ConstraintSet.TOP,R.id.activity_main,ConstraintSet.TOP);
        mConstraintSet.connect(R.id.id_videoview,ConstraintSet.RIGHT,R.id.activity_main,ConstraintSet.RIGHT);
        mConstraintSet.connect(R.id.id_videoview,ConstraintSet.BOTTOM,R.id.activity_main,ConstraintSet.BOTTOM);
        mConstraintLayout.setConstraintSet(mConstraintSet);
        //横屏 视频充满全屏
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mFleader.getLayoutParams();
//        layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
//        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
//        mFleader.setLayoutParams(layoutParams);
//        mWebView.setVisibility(View.GONE);
    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        //恢复状态栏
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mIsLandscape = false;
        ConstraintSet mConstraintSet = new ConstraintSet(); // create a Constraint Set
        mConstraintSet.clone(getBaseContext(), R.layout.activity_player); // get constraints from layout
        mConstraintLayout.setConstraintSet(mConstraintSet);
        //竖屏 视频显示固定大小
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mFleader.getLayoutParams();
//        layoutParams.height = ViewUtils.dip2px(activity, 208);
//        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
//        mFleader.setLayoutParams(layoutParams);
//        //显示图文内容
//        mWebView.setVisibility(View.VISIBLE);
    }
}
}