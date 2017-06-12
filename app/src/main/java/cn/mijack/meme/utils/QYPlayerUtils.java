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
        intent.putExtra("aid", videoInfo.aId);
        intent.putExtra("tid", videoInfo.tId);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 跳转到播放器播放
     *
     * @param context
     * @param aid
     * @param tid
     */
    @Deprecated
    public static void jumpToPlayerActivity(Context context, String aid, String tid) {
        LogUtils.i(TAG, "jumpToPlayerActivity aid: " + aid + " tid: " + tid);
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("aid", aid);
        intent.putExtra("tid", tid);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
