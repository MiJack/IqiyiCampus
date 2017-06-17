package cn.mijack.meme.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.mijack.meme.R;
import cn.mijack.meme.utils.Utils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Mr.Yuan
 * @date 2017/6/17
 */
public class MemeView extends ConstraintLayout {
    private Bitmap bitmap;
    private Bitmap emojiBitmap;
    private float emojiLeft = 0;
    private float emojiTop = 0;
    private float mPreviousX;
    private float mPreviousY;
    private int mActivePointerId;
    TextView title;
    ImageView captureView;
    ImageView emojiView;

    public MemeView(Context context) {
        this(context,null);
    }

    public MemeView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MemeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.base_meme_view, this);
//        addView(view);
        title = (TextView) findViewById(R.id.title);
        captureView = (ImageView) findViewById(R.id.captureView);
        emojiView = (ImageView) findViewById(R.id.emojiView);
    }

    public void setImageBitmap(Bitmap bitmap) {
        captureView.setImageBitmap(bitmap);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (bitmap != null) {
//            canvas.drawBitmap(bitmap, 0, 0, null);
//        }
//        if (emojiBitmap != null) {
//            canvas.drawBitmap(emojiBitmap, emojiLeft, emojiTop, null);
//        }
//    }

    public void setEmojiBitmapUrl(String url) {
        Glide.with(getContext())
                .load(Utils.imageUrl(url, 100, 100))
                .into(emojiView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPreviousX = e.getX(0);
                mPreviousY = e.getY(0);
                mActivePointerId = e.getPointerId(0);
                //判断是否在特定区域内

                break;
            case MotionEvent.ACTION_MOVE:
//                final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
//                if (activePointerIndex == INVALID_POINTER) {
//                    Log.e(TAG, "Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
//                    break;
//                }
//
//                final int x = (int) MotionEventCompat.getX(ev, 0);
//                int deltaX = (int) (mPreviousX - x);
//                if (!mIsBeingDragged && Math.abs(deltaX) > mTouchSlop) {
//                    mIsBeingDragged = true;
//                    if (deltaX > 0) {
//                        deltaX -= mTouchSlop;
//                    } else {
//                        deltaX += mTouchSlop;
//                    }
//                }
//                if (mIsBeingDragged) {
//                    // Scroll to follow the motion event
//                    mPreviousX = x;
//
//                    final int oldX = getScrollX();
//                    final int range = mScrollRange;
//
//                    if (overScrollBy(deltaX, 0, oldX, 0, range, 0, mOverscrollDistance, 0, true)) {
//                        // Break our velocity if we hit a scroll barrier.
//                        mVelocityTracker.clear();
//                    }
//
//                    if (mEdgeEffectLeft != null) {
//                        final int pulledToX = oldX + deltaX;
//                        if (pulledToX < 0) {
//                            mEdgeEffectLeft.onPull((float) deltaX / getWidth());
//                            if (!mEdgeEffectRight.isFinished()) {
//                                mEdgeEffectRight.onRelease();
//                            }
//                        } else if (pulledToX > range) {
//                            mEdgeEffectRight.onPull((float) deltaX / getWidth());
//                            if (!mEdgeEffectLeft.isFinished()) {
//                                mEdgeEffectLeft.onRelease();
//                            }
//                        }
//                        if (!mEdgeEffectLeft.isFinished() || !mEdgeEffectRight.isFinished()) {
//                            postInvalidateOnAnimation();
//                        }
//
//                    }
//
//                }
                break;

            case MotionEvent.ACTION_UP:
//                if (mIsBeingDragged) {
//                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
//                    int initialVelocity = (int) mVelocityTracker.getXVelocity(mActivePointerId);
//
//                    if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
//                        fling(-initialVelocity);
//                    } else {
//                        if (mScroller.springBack(getScrollX(), 0, 0, mScrollRange, 0, 0)) {
//                            postInvalidateOnAnimation();
//                        }
//                    }
//
//                    mActivePointerId = INVALID_POINTER;
//                    mIsBeingDragged = false;
//                    mVelocityTracker.recycle();
//                    mVelocityTracker = null;
//
//                    if (mEdgeEffectLeft != null) {
//                        mEdgeEffectLeft.onRelease();
//                        mEdgeEffectRight.onRelease();
//                    }
//                } else {
//                    // Was not being dragged, was this a press on an icon?
//                    final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
//                    if (activePointerIndex == INVALID_POINTER) {
//                        return false;
//                    }
//                    final int x = (int) ev.getX(activePointerIndex) + getScrollX();
//                    final int y = (int) ev.getY(activePointerIndex);
//                    int i = 0;
//                    for (Rect rect : mIconPositions) {
//                        if (rect.contains(x, y)) {
//                            final int position = i + mSkippedIconCount;
//                            Toast.makeText(getContext(), "Pressed icon " + position + "; rect count: " + mIconPositions.size(), Toast.LENGTH_SHORT).show();
//                            break;
//                        }
//                        i++;
//                    }
//                }
                break;

            case MotionEvent.ACTION_CANCEL:
//                if (mIsBeingDragged) {
//                    if (mScroller.springBack(getScrollX(), 0, 0, mScrollRange, 0, 0)) {
//                        postInvalidateOnAnimation();
//                    }
//                    mActivePointerId = INVALID_POINTER;
//                    mIsBeingDragged = false;
//                    if (mVelocityTracker != null) {
//                        mVelocityTracker.recycle();
//                        mVelocityTracker = null;
//                    }
//
//                    if (mEdgeEffectLeft != null) {
//                        mEdgeEffectLeft.onRelease();
//                        mEdgeEffectRight.onRelease();
//                    }
//                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
//                onSecondaryPointerUp(ev);
                break;

        }
        return true;
    }
}
