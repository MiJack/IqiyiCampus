package cn.mijack.meme.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

/**
 * @author Mr.Yuan
 * @date 2017/6/8
 */
public class UserManager {
    private static final String USER_ID = "USER_ID";
    private Context context;
    private SharedPreferences sharedPreferences;

    public UserManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_APPEND | Context.MODE_PRIVATE);
    }

    public static UserManager get(Context context) {
        return new UserManager(context);
    }

    public boolean isLogin() {
        return sharedPreferences.contains(USER_ID);
    }

    public User getUser() {
        return null;
    }

    public void logout() {
        sharedPreferences.edit().clear().commit();
    }
}
