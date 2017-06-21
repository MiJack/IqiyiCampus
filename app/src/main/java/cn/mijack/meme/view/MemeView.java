package cn.mijack.meme.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.mijack.meme.R;
import cn.mijack.meme.utils.LogUtils;
import cn.mijack.meme.utils.TextHelper;
import cn.mijack.meme.utils.Utils;

/**
 * @author Mr.Yuan
 * @date 2017/6/17
 */
public class MemeView extends ViewGroup {
    private static final String TAG = "MemeView";
    private float mPreviousX;
    private float mPreviousY;
    TextView title;
    ImageView captureView;
    ImageView emojiView;
    private int selectModel = SELECT_NONE;
    public static final int SELECT_NONE = 0;
    public static final int SELECT_EMOJI = 1;
    public static final int SELECT_TEXT = 2;
    private int mOldCenterX;
    private int mOldCenterY;

    public MemeView(Context context) {
        this(context, null);
    }

    public MemeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MemeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.base_meme_view, this);
        title = (TextView) findViewById(R.id.title);
        captureView = (ImageView) findViewById(R.id.captureView);
        emojiView = (ImageView) findViewById(R.id.emojiView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v.getVisibility() != View.GONE) {
                measureChild(v, widthMeasureSpec, heightMeasureSpec);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        Log.d(TAG, String.format("onLayout: l:%s,t:%s,r:%s,b:%s", l, t, r, b));
        captureView.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        layoutChild(title);
        layoutChild(emojiView);
        LayoutParams lp = (LayoutParams) emojiView.getLayoutParams();
//        Log.d(TAG, String.format("emojiView: l:%s,t:%s,r:%s,b:%s",
//                lp.centerX - emojiView.getMeasuredWidth() / 2, lp.centerY - emojiView.getMeasuredHeight() / 2,
//                lp.centerX + emojiView.getMeasuredWidth() / 2, lp.centerY + emojiView.getMeasuredHeight() / 2));

    }

    private void layoutChild(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int centerX = layoutParams.centerX;
        int centerY = layoutParams.centerY;
        if (centerX - view.getMeasuredWidth() / 2 < 0) {
            layoutParams.centerX = view.getMeasuredWidth() / 2;
            centerX = layoutParams.centerX;
        }
        if (centerX + view.getMeasuredWidth() / 2 > getMeasuredWidth()) {
            layoutParams.centerX = getMeasuredWidth() - view.getMeasuredWidth() / 2;
            centerX = layoutParams.centerX;
        }
        if (centerY - view.getMeasuredHeight() / 2 < 0) {
            layoutParams.centerY = view.getMeasuredHeight() / 2;
            centerY = layoutParams.centerY;
        }
        if (centerY + view.getMeasuredHeight() / 2 > getMeasuredHeight()) {
            layoutParams.centerY = getMeasuredHeight() - view.getMeasuredHeight() / 2;
            centerY = layoutParams.centerY;
        }
        view.layout(centerX - view.getMeasuredWidth() / 2,
                centerY - view.getMeasuredHeight() / 2,
                centerX + view.getMeasuredWidth() / 2,
                centerY + view.getMeasuredHeight() / 2);
    }

    public void setImageBitmap(Bitmap bitmap) {
        captureView.setImageBitmap(bitmap);
    }

    public void setEmojiBitmapUrl(String url) {
        Glide.with(getContext())
                    .load(Utils.imageUrl(url, 640, 640))
                .into(emojiView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        String TAG = "onTouchEvent";
        final int action = MotionEventCompat.getActionMasked(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPreviousX = e.getX(0);
                mPreviousY = e.getY(0);
                //判断是否在特定区域内
                Log.d("onTouchEvent", "mPreviousX:" + mPreviousX + "\tmPreviousY:" + mPreviousY);
                LogUtils.logView("onTouchEvent[emojiView]", emojiView);
                LogUtils.logView("onTouchEvent[title]", title);
                if (isInTheView(mPreviousX, mPreviousY, emojiView)) {
                    selectModel = SELECT_EMOJI;
                    mOldCenterX = ((LayoutParams) emojiView.getLayoutParams()).centerX;
                    mOldCenterY = ((LayoutParams) emojiView.getLayoutParams()).centerY;
                } else if (isInTheView(mPreviousX, mPreviousY, title)) {
                    selectModel = SELECT_TEXT;
                    mOldCenterX = ((LayoutParams) title.getLayoutParams()).centerX;
                    mOldCenterY = ((LayoutParams) title.getLayoutParams()).centerY;
                } else {
                    selectModel = SELECT_NONE;
                }
                Log.d(TAG, "onTouchEvent: selectModel:" + selectModel);
                break;
            case MotionEvent.ACTION_MOVE:
                View view = null;
                if (selectModel == SELECT_EMOJI) {
                    view = emojiView;
                    Log.d(TAG, "onTouchEvent[MOVE]: emojiView");
                }
                if (selectModel == SELECT_TEXT) {
                    view = title;
                    Log.d(TAG, "onTouchEvent[MOVE]: title");
                }
                if (view != null) {
                    float currentX = e.getX(0);
                    float currentY = e.getY(0);
                    LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    Log.d(TAG, "onTouchEvent[MOVE]: centerX:" + layoutParams.centerX + "\tcenterY:" + layoutParams.centerY);
                    Log.d(TAG, "onTouchEvent[MOVE]: mOldCenterX:" + mOldCenterX + "\tcurrentX:" + currentX + "\tmPreviousX:" + mPreviousX);
                    Log.d(TAG, "onTouchEvent[MOVE]: mOldCenterY:" + mOldCenterY + "\tcurrentY:" + currentY + "\tmPreviousY:" + mPreviousY);
                    layoutParams.centerX = (int) (mOldCenterX + currentX - mPreviousX);
                    layoutParams.centerY = (int) (mOldCenterY + currentY - mPreviousY);
                    Log.d(TAG, "onTouchEvent[MOVE]: centerX:" + layoutParams.centerX + "\tcenterY:" + layoutParams.centerY);
                    view.setLayoutParams(layoutParams);
                }
                break;
            case MotionEvent.ACTION_UP:
                selectModel = SELECT_NONE;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;

        }
        return true;
    }

    private boolean isInTheView(float x, float y, View v) {
        int l = v.getLeft();
        int r = v.getRight();
        int t = v.getTop();
        int b = v.getBottom();
        return x >= l && x <= r && y >= t && y <= b;
    }

    public void setText(String text) {
        title.setText(text);
    }

    public void setTextColor(int rgb) {
        title.setTextColor(rgb);
    }

    public String getText() {
        return TextHelper.getText(title);
    }

    public void makeFontSizeUp() {
        int autoSizeMaxTextSize = getResources().getDimensionPixelSize(R.dimen.maxFontSize);
        int size = (int) (title.getTextSize() * 1.2);
        if (size > autoSizeMaxTextSize) {
            size = autoSizeMaxTextSize;
        }
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void makeFontSizeDown() {
        int autoSizeMinTextSize = getResources().getDimensionPixelSize(R.dimen.minFontSize);
        int size = (int) (title.getTextSize() / 1.2);
        if (size < autoSizeMinTextSize) {
            size = autoSizeMinTextSize;
        }
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public int getTextColor() {
        return title.getCurrentTextColor();
    }

    public void makeEmojiSizeDown() {
        int pixels =  Utils.dipToPixels(getContext(), 48);
        int width = (int) (emojiView.getWidth() / 1.2);
        if (width < pixels) {
            width = pixels;
        }
        LayoutParams lp = (LayoutParams) emojiView.getLayoutParams();
        lp.width = width;
        lp.height = width;
        emojiView.setLayoutParams(lp);
    }

    public void makeEmojiSizeUp() {
        int pixels = (int) (getHeight() * 0.8);
        int width = (int) (emojiView.getWidth() * 1.2);
        if (width > pixels) {
            width = pixels;
        }
        LayoutParams lp = (LayoutParams) emojiView.getLayoutParams();
        lp.width = width;
        lp.height = width;
        emojiView.setLayoutParams(lp);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public int centerX = -1;
        public int centerY = -1;
    }
}
