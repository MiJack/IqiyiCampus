package cn.mijack.meme.adapter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.model.MemeEntity;
import cn.mijack.meme.ui.PlayerActivity;
import cn.mijack.meme.user.User;
import cn.mijack.meme.utils.QYPlayerUtils;
import cn.mijack.meme.utils.StringUtils;
import cn.mijack.meme.utils.Utils;
import cn.mijack.meme.vm.PlayerViewModel;

/**
 * @author admin
 * @date 2017/6/18
 */

public class MemeAdapter extends RecyclerView.Adapter {
    private static final int EMPTY_VIEW = 0;
    private List<MemeEntity> list = new ArrayList<>();
    private int type;
    public static final int TYPE_HISTORY = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_SQUARE = 3;

    public MemeAdapter(int type) {
        this.type = type;
    }

    @Override
    public int getItemViewType(int position) {
        if (Utils.isEmpty(list)) {
            return EMPTY_VIEW;
        }
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = viewType == EMPTY_VIEW ? R.layout.layout_empty_meme : getLayoutId();
        return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false)) {
        };
    }

    private int getLayoutId() {
        if (type == TYPE_VIDEO) {
            return R.layout.item_meme_video;
        }
        if (type == TYPE_HISTORY) {
            return R.layout.item_meme_history;
        }
        if (type == TYPE_SQUARE) {
            return R.layout.item_meme;
        }
        throw new IllegalStateException();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position)==EMPTY_VIEW){
            return;
        }
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

        if (type == TYPE_HISTORY) {
            videoName.setText(memeEntity.getShortTitle());
//            userName.setText(user.getNickName());
            updateTime.setText(StringUtils.getDataUtil(System.currentTimeMillis(), memeEntity.getTime()));
//            Glide.with(context)
//                    .load(user.getAvatarUrl())
//                    .into(userAvatar);
            Glide.with(context)
                    .load(memeEntity.getUrl())
                    .placeholder(R.drawable.ic_picture)
                    .into(captureView);
            playView.setOnClickListener(v -> QYPlayerUtils.jumpToPlayerActivity(v.getContext(), memeEntity.getVideoInfo(), memeEntity.getStartTime()));
        }
        if (type == TYPE_VIDEO) {
//            videoName.setText(memeEntity.getShortTitle());
            userName.setText(user.getNickName());
            updateTime.setText(StringUtils.getDataUtil(System.currentTimeMillis(), memeEntity.getTime()));
            Glide.with(context)
                    .load(user.getAvatarUrl())
                    .into(userAvatar);
            Glide.with(context)
                    .load(memeEntity.getUrl())
                    .placeholder(R.drawable.ic_picture)
                    .into(captureView);
            playView.setOnClickListener(v -> {
                Context c = v.getContext();
                if (!(c instanceof PlayerActivity)) {
                    Toast.makeText(c, "无法播放", Toast.LENGTH_SHORT).show();
                } else {
                    PlayerActivity playerActivity = (PlayerActivity) c;
                    PlayerViewModel playerViewModel = ViewModelProviders.of(playerActivity).get(PlayerViewModel.class);
                    playerViewModel.getProgressData().postValue(memeEntity.getStartTime());
                    Toast.makeText(playerActivity, "正在跳转", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (type == TYPE_SQUARE) {
            videoName.setText(memeEntity.getShortTitle());
            userName.setText(user.getNickName());
            updateTime.setText(StringUtils.getDataUtil(System.currentTimeMillis(), memeEntity.getTime()));
            Glide.with(context)
                    .load(user.getAvatarUrl())
                    .into(userAvatar);
            Glide.with(context)
                    .load(memeEntity.getUrl())
                    .placeholder(R.drawable.ic_picture)
                    .into(captureView);
            playView.setOnClickListener(v -> QYPlayerUtils.jumpToPlayerActivity(v.getContext(), memeEntity.getVideoInfo(), memeEntity.getStartTime()));
            shareView.setOnClickListener(v -> {

            });
        }
    }

    @Override
    public int getItemCount() {
        return Utils.size(list) == 0 ? 1 : Utils.size(list);
    }

    public void setData(List<MemeEntity> data) {
        list.clear();
        if (data != null) {
            list.addAll(data);
        }
        notifyDataSetChanged();
    }
}
