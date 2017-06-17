package cn.mijack.meme.view;

import android.app.Dialog;
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
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(Utils.dipToPixels(getActivity(),90),0,Utils.dipToPixels(getActivity(),90),0);
//        dialog.setContentView(view,layoutParams);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 6));
        List<ResolveInfo> resolveInfos = getArguments().getParcelableArrayList("intent");
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        recyclerView.setAdapter(new ResolveInfoAdapter(resolveInfos, getActivity().getPackageManager()));
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        //默认全屏展开
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void doclick(View v) {
        //点击任意布局关闭
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
