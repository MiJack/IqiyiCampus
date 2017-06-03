package cn.mijack.meme.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.qiyi.video.playcore.QiyiVideoView;

/**
 * @author Mr.Yuan
 * @date 2017/6/3
 */
public class MemeVideoView extends QiyiVideoView {
    public MemeVideoView(Context var1) {
        super(var1);
    }

    public MemeVideoView(Context var1, AttributeSet var2) {
        super(var1, var2);
    }

    public MemeVideoView(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
    }

    public void OnVideoRenderAreaChanged(int var1, int var2, int var3, int var4) {
        System.out.println("vars:" + var1 + "-" + var2 + "-" + var3 + "-" + var4);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
