package cn.mijack.meme.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * @author Mr.Yuan
 * @date 2017/5/26
 */
public class UiUtils {
    /**
     * 获取屏幕宽高
     * @param context
     * @return
     */
    public static int[] getScreenWidthAndHeight(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int[] screen = new int[2];
        screen[0] = metrics.widthPixels;
        screen[1] = metrics.heightPixels;
        return screen;
    }


}