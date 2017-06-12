package cn.mijack.meme.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import cn.mijack.meme.R;
import cn.mijack.meme.base.BaseFragment;
import cn.mijack.meme.ui.AccountActivity;
import cn.mijack.meme.ui.MemeHistoryActivity;
import cn.mijack.meme.ui.VideoHistoryActivity;
import cn.mijack.meme.user.User;
import cn.mijack.meme.user.UserManager;

/**
 * @author Mr.Yuan
 * @date 2017/5/25
 */
public class AccountFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_CODE_LOGIN = 1;
    ImageView userAvatar;
    TextView userLogin;
    TextView tvUserName;
    TextView tvUserEmail;
    TextView btnVideo;
    TextView btnMeme;
    TextView btnLogout;
    private UserManager userManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        userAvatar = (ImageView) view.findViewById(R.id.userAvatar);
        userLogin = (TextView) view.findViewById(R.id.userLogin);
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        tvUserEmail = (TextView) view.findViewById(R.id.tvUserEmail);
        btnVideo = (TextView) view.findViewById(R.id.btnVideo);
        btnMeme = (TextView) view.findViewById(R.id.btnMeme);
        btnLogout = (TextView) view.findViewById(R.id.btnLogout);
        userManager = UserManager.get(getActivity());
        updateUserInfo();
        userLogin.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
        btnMeme.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

    }

    private void updateUserInfo() {
        boolean login = userManager.isLogin();
        userLogin.setVisibility(login ? View.GONE : View.VISIBLE);
        tvUserName.setVisibility(!login ? View.GONE : View.VISIBLE);
        tvUserEmail.setVisibility(!login ? View.GONE : View.VISIBLE);
        btnLogout.setVisibility(!login ? View.GONE : View.VISIBLE);
        RequestManager requestManager = Glide.with(this);
        if (userManager.isLogin()) {
            User user = userManager.getUser();
            if (TextUtils.isEmpty(user.getAvatarUrl())) {
                requestManager.load(R.drawable.ic_default_profile).into(userAvatar);
            } else {
                requestManager.load(user.getAvatarUrl()).into(userAvatar);
            }
        } else {
            requestManager.load(R.drawable.ic_profile).into(userAvatar);
        }
        if (login) {
            User user = userManager.getUser();
            tvUserName.setText(user.getNickName());
            tvUserEmail.setText(user.getEmail());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                updateUserInfo();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userLogin:
                requestLogin();
                break;
            case R.id.btnVideo:
                if (userManager.isLogin()) {
                    Intent intent = new Intent(getActivity(), VideoHistoryActivity.class);
                    startActivity(intent);
                } else {
                    showRequestLoginDialog();
                }
                break;
            case R.id.btnMeme:
                if (userManager.isLogin()) {
                    Intent intent = new Intent(getActivity(), MemeHistoryActivity.class);
                    startActivity(intent);
                } else {
                    showRequestLoginDialog();
                }
                break;
            case R.id.btnLogout:
                showLogoutDialog();
                break;
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("注销")
                .setMessage("你确定要注销账号嘛")
                .setPositiveButton("确定", (dialog, which) -> {
                    userManager.logout();
                    updateUserInfo();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.cancel())
                .create().show();
    }

    private void requestLogin() {
        Intent intent = new Intent(getActivity(), AccountActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    private void showRequestLoginDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("请登陆")
                .setMessage("该功能的使用需要登陆")
                .setPositiveButton("登陆", (dialog, which) -> requestLogin())
                .setNegativeButton("取消", (dialog, which) -> dialog.cancel())
                .create().show();
    }
}
