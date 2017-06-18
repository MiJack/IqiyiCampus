package cn.mijack.meme.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import cn.mijack.meme.model.VideoInfo;
import cn.mijack.meme.ui.PlayerActivity;

/**
 * @author Mr.Yuan
 * @date 2017/5/26
 */
public class QYPlayerUtils {
    private static String TAG = "QYPlayerUtils";

    /**
     * 跳转到播放器播放
     *
     * @param context
     * @param videoInfo
     */
    public static void jumpToPlayerActivity(Context context, VideoInfo videoInfo) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("video", videoInfo);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static void jumpToPlayerActivity(Context context, VideoInfo videoInfo, int time) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("video", videoInfo);
        intent.putExtra("time", time);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
