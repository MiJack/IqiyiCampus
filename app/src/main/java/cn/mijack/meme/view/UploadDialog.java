package cn.mijack.meme.view;


import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DialogTitle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.mijack.meme.R;

/**
 * @author Mr.Yuan
 * @date 2017/6/18
 */
public class UploadDialog implements View.OnClickListener {
    private TextView message;
    private DialogTitle alertTitle;
    AlertDialog alertDialog;
    private Button button1;
    private Button button2;

    public UploadDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_upload, null, false);
        alertTitle = (DialogTitle) view.findViewById(R.id.alertTitle);
        message = (TextView) view.findViewById(android.R.id.message);
        button1 = (Button) view.findViewById(android.R.id.button1);
        button2 = (Button) view.findViewById(android.R.id.button2);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        alertTitle.setText("上传图片");
        message.setText("正在获取token");
        alertDialog = new AlertDialog.Builder(context)
                .setTitle("上传图片")
                .setMessage("正在获取token")
                .setView(view)
                .setCancelable(false)
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

    public void showFail(String msg) {
        alertTitle.setText("上传失败");
        if (TextUtils.isEmpty(msg)) {
            message.setText("");
        } else {
            message.setText(msg);
        }
    }

    public void showFail() {
        showFail(null);
    }

    public void showGenerateByteData() {
        message.setText("正在生成上传数据");
    }

    public void showUploadProgress(double progress) {
        message.setText("正在上传数据:" + ((int) (progress * 100)) + "%");
    }

    public void showSuccess() {
        message.setText("上传成功");
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
