package cn.mijack.meme.view;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author admin
 * @date 2017/6/16
 */

public class NoScrollGridLayoutManager extends RecyclerView.LayoutManager {
    private static final String TAG = "LayoutManager";
    //    x x x x x x
//    x x x x x x
//    x x x x x x
//    x x x x x x
    private RecyclerView recyclerView;

    public NoScrollGridLayoutManager(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //在布局之前，将所有的子View先Detach掉，放入到Scrap缓存中
        detachAndScrapAttachedViews(recycler);
//        Log.d(TAG, "onLayoutChildren: recyclerView:width:" + recyclerView.getWidth() + "\theight:" + recyclerView.getHeight());
//        Log.d(TAG, "------------------------------------------------------------------------------------");
        //定义竖直方向的偏移量
        int cellWidth = recyclerView.getWidth() / 6;
        for (int i = 0; i < getItemCount(); i++) {
            //这里就是从缓存里面取出
            View view = recycler.getViewForPosition(i);
            int x = i % 6;
            int y = (i - x) / 6;
//            Log.d(TAG, "onLayoutChildren: x:" + x + "\ty:" + y);
            //将View加入到RecyclerView中
            addView(view);
            //对子View进行测量
            measureChildWithMargins(view, (int) (recyclerView.getWidth() - cellWidth * 0.8), (int) (recyclerView.getHeight() - cellWidth * 0.8));
            //把宽高拿到，宽高都是包含ItemDecorate的尺寸
            int width = getDecoratedMeasuredWidth(view);
            int height = getDecoratedMeasuredHeight(view);
            //最后，将View布局
            int left = x * cellWidth + cellWidth / 10;
            int top = y * cellWidth + cellWidth / 10;
//            Log.d(TAG, "onLayoutChildren: i(" + i + ")left:" + left + ",top:" + top + ",width:" + width + ",height:" + height);
            layoutDecorated(view, left, top, left + width, top + height);
        }
        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        layoutParams.height = cellWidth * 4;
        layoutParams.width = cellWidth * 6;
        recyclerView.setLayoutParams(layoutParams);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
