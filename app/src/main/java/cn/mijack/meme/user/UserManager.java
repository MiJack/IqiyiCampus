package cn.mijack.meme.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

/**
 * @author Mr.Yuan
 * @date 2017/6/8
 */
public class UserManager {
    public static final String USER_AVATAR_URL = "USER_AVATAR_URL";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_NICKNAME = "USER_NICKNAME";
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
        if (!sharedPreferences.contains(USER_ID)){
            return null;
        }
        User user=new User();
        user.setAvatarUrl(sharedPreferences.getString(USER_AVATAR_URL,null));
        user.setEmail(sharedPreferences.getString(USER_EMAIL,null));
        user.setNickName(sharedPreferences.getString(USER_NICKNAME,null));
        user.setUid(sharedPreferences.getInt(USER_ID,0));
        return user;
    }

    public void logout() {
        sharedPreferences.edit().clear().commit();
    }

    public void save(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString(USER_AVATAR_URL, user.getAvatarUrl());
        editor.putString(USER_EMAIL, user.getEmail());
        editor.putString(USER_NICKNAME, user.getNickName());
        editor.putInt(USER_ID, user.getUid());
        editor.commit();
    }
}
