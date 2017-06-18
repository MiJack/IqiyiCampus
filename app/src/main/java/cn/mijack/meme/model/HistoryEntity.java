package cn.mijack.meme.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author admin
 * @date 2017/6/16
 */
@Entity(tableName = "history")
public class HistoryEntity {
    @PrimaryKey
    //视频ID
    private String vId;
    //视频名称
    private String title;
    //视频封面
    private String img;

    //播放器播放的时候需要
    private String aId;

    //播放器播放的时候需要
    private String tId;

    private long updateTime;

    private int progress;
    private int duration;
    private String videoInfo;

    public HistoryEntity() {
    }

    public HistoryEntity(String videoId, String title, String img, String aId, String tId, long updateTime, int progress, int duration, String videoInfo) {
        this.vId = videoId;
        this.title = title;
        this.img = img;
        this.aId = aId;
        this.tId = tId;
        this.updateTime = updateTime;
        this.progress = progress;
        this.duration = duration;
        this.videoInfo = videoInfo;
    }


    public String getVId() {
        return vId;
    }

    public void setVId(String vId) {
        this.vId = vId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAId() {
        return aId;
    }

    public void setAId(String aId) {
        this.aId = aId;
    }

    public String getTId() {
        return tId;
    }

    public void setTId(String tId) {
        this.tId = tId;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(String videoInfo) {
        this.videoInfo = videoInfo;
    }
}
