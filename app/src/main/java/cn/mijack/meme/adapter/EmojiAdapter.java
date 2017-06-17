package cn.mijack.meme.adapter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
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
import cn.mijack.meme.utils.Utils;
import cn.mijack.meme.vm.MemeViewModel;

/**
 * @author admin
 * @date 2017/6/16
 */

public class EmojiAdapter extends RecyclerView.Adapter {
    List<Emoji> emojis = new ArrayList<>();
    private FragmentActivity activity;

    public EmojiAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

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
        String url = Utils.imageUrl(emoji.getUrl(),64,64);
        ImageView emojiView = (ImageView) itemView.findViewById(R.id.emojiView);
        Glide.with(context).load(url).into(emojiView);
        viewHolder.itemView.setOnClickListener(v -> {
            MemeViewModel memeViewModel = ViewModelProviders.of(activity).get(MemeViewModel.class);
            memeViewModel.setEmojiUrl(emoji.getUrl());
        });
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
