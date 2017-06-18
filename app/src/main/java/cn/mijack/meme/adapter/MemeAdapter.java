package cn.mijack.meme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.model.MemeEntity;
import cn.mijack.meme.user.User;
import cn.mijack.meme.utils.QYPlayerUtils;
import cn.mijack.meme.utils.StringUtils;

/**
 * @author admin
 * @date 2017/6/18
 */

public class MemeAdapter extends RecyclerView.Adapter {
    private List<MemeEntity> list = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meme, parent, false)) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        View itemView = holder.itemView;
        Context context = itemView.getContext();
        ImageView userAvatar = (ImageView) itemView.findViewById(R.id.userAvatar);
        TextView userName = (TextView) itemView.findViewById(R.id.userName);
        ImageView captureView = (ImageView) itemView.findViewById(R.id.captureView);
        TextView videoName = (TextView) itemView.findViewById(R.id.videoName);
//        TextView progress = (TextView) itemView.findViewById(R.id.progress);
        TextView updateTime = (TextView) itemView.findViewById(R.id.updateTime);
        Button playView = (Button) itemView.findViewById(R.id.playView);
        Button shareView = (Button) itemView.findViewById(R.id.shareView);
        MemeEntity memeEntity = list.get(position);
        User user = memeEntity.getUser();
        videoName.setText(memeEntity.getShortTitle());
        userName.setText(user.getNickName());
//        progress.setText(Utils.formatTime(memeEntity.getStartTime()));
        updateTime.setText(StringUtils.getDataUtil(System.currentTimeMillis(), memeEntity.getTime()));
        Glide.with(context)
                .load(user.getAvatarUrl())
                .into(userAvatar);
        Glide.with(context)
                .load(memeEntity.getUrl())
                .placeholder(R.drawable.ic_picture)
                .into(captureView);
        playView.setOnClickListener(v -> QYPlayerUtils.jumpToPlayerActivity(v.getContext(), memeEntity.getVideoInfo(),memeEntity.getStartTime()));
        shareView.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<MemeEntity> data) {
        list.clear();
        if (data != null) {
            list.addAll(data);
        }
        notifyDataSetChanged();
    }
}
