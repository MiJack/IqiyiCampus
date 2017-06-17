package cn.mijack.meme.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Mr.Yuan
 * @date 2017/6/18
 */
public class TokenEntity {
    @SerializedName("token")
    private String token;

    @SerializedName("key")
    private String key;

    public TokenEntity() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
