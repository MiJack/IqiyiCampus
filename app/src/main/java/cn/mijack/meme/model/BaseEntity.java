package cn.mijack.meme.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import cn.mijack.meme.remote.HasErrorReason;

/**
 * Created by zhouxiaming on 2017/5/5.
 */

public class BaseEntity implements Serializable ,HasErrorReason{
    /**
     * 服务器返回码，100000=成功
     */
    @SerializedName("code")
    public int code;

    @SerializedName("errorReason")
    public String errorReason;

    @Override
    public String errorReason() {
        return errorReason;
    }
}
