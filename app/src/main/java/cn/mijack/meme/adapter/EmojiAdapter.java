package cn.mijack.meme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.model.Emoji;

/**
 * @author admin
 * @date 2017/6/16
 */

public class EmojiAdapter extends RecyclerView.Adapter {
    List<Emoji> emojis = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new RecyclerView.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_emoji, viewGroup, false)) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        View itemView = viewHolder.itemView;
        Context context = itemView.getContext();
        Emoji emoji = emojis.get(position);
        String url = emoji.getUrl() + "?imageView2/1/w/64/h/64";
        ImageView emojiView = (ImageView) itemView.findViewById(R.id.emojiView);
        Glide.with(context).load(url).into(emojiView);
    }

    @Override
    public int getItemCount() {
        return emojis != null ? emojis.size() : 0;
    }

    public void setData(List<Emoji> data) {
        if (data == null) {
            return;
        }
        emojis.clear();
        emojis.addAll(data);
        this.notifyDataSetChanged();
    }
}
