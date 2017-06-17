package cn.mijack.meme.adapter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.ui.MemeActivity;
import cn.mijack.meme.view.IntentSelectSheetDialogFragment;

/**
 * @author admin
 * @date 2017/6/17
 */

public class ResolveInfoAdapter extends RecyclerView.Adapter {
    private Activity activity;
    private List<ActivityInfo> resolveInfos;
    private PackageManager packageManager;
    private Intent intent;
    private IntentSelectSheetDialogFragment fragment;

    public ResolveInfoAdapter(Activity activity, List<ActivityInfo> resolveInfos,
                              PackageManager packageManager, Intent intent,
                              IntentSelectSheetDialogFragment fragment) {
        this.activity = activity;
        this.resolveInfos = resolveInfos;
        this.packageManager = packageManager;
        this.intent = intent;
        this.fragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resolve_info, parent, false)) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ActivityInfo activityInfo = resolveInfos.get(position);
        ((TextView) holder.itemView.findViewById(R.id.name)).setText(activityInfo.loadLabel(packageManager));
        ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.icon);
        imageView.setImageDrawable(activityInfo.loadIcon(packageManager));
        holder.itemView.setOnClickListener(v -> {
            intent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
            activity.grantUriPermission(activityInfo.packageName, intent.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            activity.startActivityForResult(intent, MemeActivity.REQUEST_CODE_SHARE);
            fragment.close();
        });
    }

    @Override
    public int getItemCount() {
        return resolveInfos != null ? resolveInfos.size() : 0;
    }
}
