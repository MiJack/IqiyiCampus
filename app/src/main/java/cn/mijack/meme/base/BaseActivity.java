package cn.mijack.meme.base;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * @author Mr.Yuan
 * @date 2017/5/24
 */
public class BaseActivity extends AppCompatActivity implements LifecycleRegistryOwner {
    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    public LifecycleRegistry getLifecycle() {
        return this.mRegistry;
    }

}
