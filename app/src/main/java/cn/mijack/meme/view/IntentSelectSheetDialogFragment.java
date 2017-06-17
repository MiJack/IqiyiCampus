package cn.mijack.meme.view;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

import cn.mijack.meme.R;
import cn.mijack.meme.adapter.ResolveInfoAdapter;
import cn.mijack.meme.utils.Utils;

/**
 * @author admin
 * @date 2017/6/17
 */

public class IntentSelectSheetDialogFragment extends BottomSheetDialogFragment {
    private BottomSheetBehavior mBehavior;
    private RecyclerView recyclerView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.dialog_bottom_sheet, null);
        dialog.setContentView(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 6));
        List<ActivityInfo> resolveInfos = getArguments().getParcelableArrayList("activityInfos");
        Intent intent = getArguments().getParcelable("intent");
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        recyclerView.setAdapter(new ResolveInfoAdapter(getActivity(), resolveInfos, getActivity().getPackageManager(), intent, this));
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        //默认全屏展开
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void close() {
        //点击任意布局关闭
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
