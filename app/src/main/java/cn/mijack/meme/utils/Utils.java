package cn.mijack.meme.utils;

import android.os.Looper;

/**
 * @author Mr.Yuan
 * @date 2017/6/3
 */
public class Utils {
    public static boolean isUiThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}
