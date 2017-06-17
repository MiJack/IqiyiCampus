package cn.mijack.meme.adapter;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.mijack.meme.R;

/**
 * @author admin
 * @date 2017/6/17
 */

public class ResolveInfoAdapter extends RecyclerView.Adapter {
    private List<ResolveInfo> resolveInfos;
    private PackageManager packageManager;

    public ResolveInfoAdapter(List<ResolveInfo> resolveInfos, PackageManager packageManager) {
        this.resolveInfos = resolveInfos;
        this.packageManager = packageManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resolve_info,parent,false)) {};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ResolveInfo resolveInfo = resolveInfos.get(position);
        ((TextView)holder.itemView.findViewById(R.id.name)).setText(resolveInfo.loadLabel(packageManager));
        ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.icon);
        imageView.setImageDrawable(resolveInfo.loadIcon(packageManager));
    }

    @Override
    public int getItemCount() {
        return resolveInfos != null ? resolveInfos.size() : 0;
    }
}
