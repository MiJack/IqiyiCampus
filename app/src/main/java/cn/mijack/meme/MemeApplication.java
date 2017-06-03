package cn.mijack.meme;

import android.app.Application;

import com.facebook.stetho.Stetho;

import cn.mijack.meme.view.MemeVideoView;

/**
 * @author Mr.Yuan
 * @date 2017/5/24
 */
public class MemeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MemeVideoView.init(this);
        Stetho.initializeWithDefaults(this);
    }
}
