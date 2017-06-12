package cn.mijack.meme.user;

import com.google.gson.annotations.SerializedName;

/**
 * @author Mr.Yuan
 * @date 2017/6/8
 */
public class User {
    @SerializedName("userEmail")
    private String email;
    @SerializedName("username")
    private String nickName;
    @SerializedName("id")
    private int uid;
    @SerializedName("userAvatar")
    private String avatarUrl;

    public User() {
    }

    public User(String email, String nickName, int uid, String avatarUrl) {
        this.email = email;
        this.nickName = nickName;
        this.uid = uid;
        this.avatarUrl = avatarUrl;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
