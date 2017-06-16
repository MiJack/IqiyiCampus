package cn.mijack.meme.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author admin
 * @date 2017/6/16
 */

public class Emoji implements Parcelable {
    private long id;
    private String name;
    private String url;

    public Emoji() {
    }

    protected Emoji(Parcel in) {
        id = in.readLong();
        name = in.readString();
        url = in.readString();
    }

    public static final Creator<Emoji> CREATOR = new Creator<Emoji>() {
        @Override
        public Emoji createFromParcel(Parcel in) {
            return new Emoji(in);
        }

        @Override
        public Emoji[] newArray(int size) {
            return new Emoji[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(url);
    }
}
