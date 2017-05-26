package cn.mijack.meme;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.qiyi.video.playcore.QiyiVideoView;

/**
 * @author Mr.Yuan
 * @date 2017/5/24
 */
public class MemeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QiyiVideoView.init(this);
        Stetho.initializeWithDefaults(this);
    }
}
