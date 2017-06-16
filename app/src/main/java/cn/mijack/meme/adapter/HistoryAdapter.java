package cn.mijack.meme.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.model.HistoryEntity;
import cn.mijack.meme.utils.Utils;

/**
 * @author Mr.Yuan
 * @date 2017/6/17
 */
public class HistoryAdapter extends RecyclerView.Adapter {
    private List<HistoryEntity> data = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new RecyclerView.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_history, viewGroup, false)) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        HistoryEntity historyEntity = data.get(i);
        TextView tvDate = (TextView) viewHolder.itemView.findViewById(R.id.tvDate);
        ImageView videoImage = (ImageView) viewHolder.itemView.findViewById(R.id.videoImage);
        TextView videoTitle = (TextView) viewHolder.itemView.findViewById(R.id.videoTitle);
        ProgressBar videoProgress = (ProgressBar) viewHolder.itemView.findViewById(R.id.videoProgress);
        TextView tvVideoPlayStatue = (TextView) viewHolder.itemView.findViewById(R.id.tvVideoPlayStatue);
        TextView tvVideoPlayTime = (TextView) viewHolder.itemView.findViewById(R.id.tvVideoPlayTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        //todo 优化界面先显示
        tvDate.setText(dateFormat.format(new Date(historyEntity.getUpdateTime())));
        videoTitle.setText(historyEntity.getTitle());
        videoProgress.setProgress((int) (historyEntity.getProgress() * 100 / historyEntity.getDuration()));
        tvVideoPlayStatue.setText(Utils.formatTime(historyEntity.getProgress()) + "/" + Utils.formatTime(historyEntity.getDuration()));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        tvVideoPlayTime.setText(timeFormat.format(new Date(historyEntity.getUpdateTime())));
        Glide.with(viewHolder.itemView.getContext()).load(historyEntity.getImg()).into(videoImage);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<HistoryEntity> historyEntities) {
        this.data.clear();
        if (historyEntities != null) {
            data.addAll(historyEntities);
        }
        notifyDataSetChanged();
    }
}
