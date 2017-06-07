package cn.mijack.meme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.model.ChannelDetailEntity;
import cn.mijack.meme.model.VideoInfo;
import cn.mijack.meme.utils.ImageUtils;
import cn.mijack.meme.utils.QYPlayerUtils;
import cn.mijack.meme.utils.StringUtils;

/**
 * @author Mr.Yuan
 * @date 2017/6/4
 */
public class ChannelDetailAdapter extends RecyclerView.Adapter<ChannelDetailAdapter.VideoInfoViewHolder> {
    public static final int ITEM_TYPE_LOAD = 1;
    private String channelId;
    private String channelName;
    private String total;
    private List<VideoInfo> videoInfos = new ArrayList<>();
    private int currentPageIndex = 0;

    @Override
    public VideoInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new VideoInfoViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_video_info_item, viewGroup, false)) {
        };
    }

    @Override
    public void onBindViewHolder(VideoInfoViewHolder videoInfoViewHolder, int position) {
        videoInfoViewHolder.setData(videoInfos.get(position));
    }


    @Override
    public int getItemCount() {
        return videoInfos.size();
    }

    public void setData(ChannelDetailEntity.DataEntity dataEntity) {
        if (dataEntity == null || dataEntity.videoInfoList == null) {
            return;
        }
        currentPageIndex = 0;
        this.videoInfos.clear();
        this.videoInfos.addAll(dataEntity.videoInfoList);
        this.channelId = dataEntity.channelId;
        this.channelName = dataEntity.channelName;
        this.total = dataEntity.total;
        this.notifyDataSetChanged();
    }

    public boolean hasMore() {
        try {
            int total = Integer.valueOf(this.total);
            return total > videoInfos.size();
        } catch (Exception e) {
            return false;
        }
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public void appendData(ChannelDetailEntity.DataEntity dataEntity) {
        if (dataEntity == null || dataEntity.videoInfoList == null) {
            return;
        }
        currentPageIndex++;
        this.videoInfos.addAll(dataEntity.videoInfoList);
        this.channelId = dataEntity.channelId;
        this.channelName = dataEntity.channelName;
        this.total = dataEntity.total;
        this.notifyDataSetChanged();
    }


    class VideoInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView cover;
        TextView name;
        TextView playCount;
        TextView snsScore;
        private Context context;
        private VideoInfo video;

        public VideoInfoViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = (TextView) itemView.findViewById(R.id.item_image_name);
            playCount = (TextView) itemView.findViewById(R.id.item_image_description);
            snsScore = (TextView) itemView.findViewById(R.id.item_image_description2);
            cover = (ImageView) itemView.findViewById(R.id.item_image_img);
            itemView.setOnClickListener(this);
            resizeImageView(cover);
        }

        /**
         * 视频图片为竖图展示取分辨率为：_120_160
         *
         * @param cover
         */
        private void resizeImageView(ImageView cover) {
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int coverWidth = (screenWidth - context.getResources().getDimensionPixelSize(R.dimen.video_card_margin_margin_horizontal) * 2 * 4) / 3;
            int coverHeight = (int) (160.0f / 120.0f * coverWidth);
            cover.setMinimumHeight(coverHeight);
            cover.setMaxHeight(coverHeight);
            cover.setMaxHeight(coverWidth);
            cover.setMinimumWidth(coverWidth);
            cover.setAdjustViewBounds(false);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) cover.getLayoutParams();
            lp.height = coverHeight;
            lp.width = coverWidth;
            cover.setLayoutParams(lp);
        }

        public void setData(VideoInfo video) {
            this.video = video;
            if (!StringUtils.isEmpty(video.shortTitle)) {
                name.setText(video.shortTitle);
            } else {
                name.setText("");
            }

            if (!StringUtils.isEmpty(video.playCountText)) {
                playCount.setText(context.getString(R.string.play_count, video.playCountText));
            } else {
                playCount.setText("");
            }

            if (!StringUtils.isEmpty(video.snsScore)) {
                snsScore.setText(context.getString(R.string.sns_score, video.snsScore));
            } else {
                snsScore.setText("");
            }

            Glide.clear(cover); //清除缓存
            Glide.with(cover.getContext()).load(ImageUtils.getRegImage(video.img, ImageUtils.IMG_260_360)).animate(R.anim.alpha_on).into(cover);

        }

        @Override
        public void onClick(View view) {
            if (video != null) {
                QYPlayerUtils.jumpToPlayerActivity(context, video.aId, video.tId);

            }
        }
    }
}
