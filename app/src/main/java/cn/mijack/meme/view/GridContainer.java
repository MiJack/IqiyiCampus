package cn.mijack.meme.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import cn.mijack.meme.R;

/**
 * @author admin
 * @date 2017/6/16
 */

public class GridContainer extends ViewGroup {
    private int resourceId;
    private int column;
    private int row;

    public GridContainer(Context context) {
        super(context);
    }

    public GridContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GridContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);


        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.GridContainer, defStyleAttr, defStyleRes);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.GridContainer_layoutId:
                    resourceId = a.getResourceId(R.styleable.GridContainer_layoutId, -1);
                    break;
                case R.styleable.GridContainer_column:
                    column = a.getInt(R.styleable.GridContainer_column, 4);
                    break;
                case R.styleable.GridContainer_row:
                    row = a.getInt(R.styleable.GridContainer_row, 4);
                    break;
            }
        }

        if (resourceId == -1) {
            return;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
