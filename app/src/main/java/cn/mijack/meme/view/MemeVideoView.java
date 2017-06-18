package cn.mijack.meme.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.qiyi.video.playcore.QiyiVideoView;

/**
 * @author Mr.Yuan
 * @date 2017/6/3
 */
public class MemeVideoView extends QiyiVideoView implements SurfaceHolder.Callback {
    public MemeVideoView(Context var1) {
        this(var1, null);
    }

    public MemeVideoView(Context var1, AttributeSet var2) {
        this(var1, var2, 0);
    }

    public MemeVideoView(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        getHolder().addCallback(this);
    }

    public void OnVideoRenderAreaChanged(int var1, int var2, int var3, int var4) {
        System.out.println("vars:" + var1 + "-" + var2 + "-" + var3 + "-" + var4);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void setVideoViewSize(int width, int height) {
        try {
            super.setVideoViewSize(width, height);
        } catch (Exception e) {
        }
    }

    @Override
    public void setVideoViewSize(int width, int height, boolean ifFullScreen) {
        try {
            super.setVideoViewSize(width, height, ifFullScreen);
        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        setVideoViewSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
