package cn.mijack.meme.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author admin
 * @date 2017/6/18
 */

public class MarginItemDecoration extends RecyclerView.ItemDecoration {
    private Paint paint;
    private int color;
    private int dividerSize = 20;

    public MarginItemDecoration(int dividerSize) {
        this.dividerSize = dividerSize;
        paint = new Paint();
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public int getDividerSize() {
        return dividerSize;
    }

    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        paint.setColor(color);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + dividerSize;
            c.drawRect(left, top, right, bottom, paint);
            c.drawRect(left, child.getTop() - params.topMargin - dividerSize, right, child.getTop() - params.topMargin, paint);
            c.drawRect(left, child.getTop() - params.topMargin, left + dividerSize, child.getBottom() + params.bottomMargin, paint);
            c.drawRect(right - dividerSize, child.getTop() - params.topMargin, right, child.getBottom() + params.bottomMargin, paint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        RecyclerView.Adapter adapter = parent.getAdapter();
        int count = adapter.getItemCount();
        outRect.set(dividerSize, position != 0 ? 0 : dividerSize, dividerSize, dividerSize);
    }
}