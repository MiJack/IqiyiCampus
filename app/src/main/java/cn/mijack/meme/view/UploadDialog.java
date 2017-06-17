package cn.mijack.meme.view;


import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * @author Mr.Yuan
 * @date 2017/6/18
 */
public class UploadDialog {
    AlertDialog alertDialog;

    public UploadDialog(Context context) {
        alertDialog = new AlertDialog.Builder(context)
                .setTitle("上传图片")
                .setMessage("正在获取token")
                .create();
    }

    public void showRequestToken() {
        if (alertDialog != null) {
            alertDialog.show();
        }
    }

    public void dismiss() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}
