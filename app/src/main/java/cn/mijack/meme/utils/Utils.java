package cn.mijack.meme.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mr.Yuan
 * @date 2017/6/3
 */
public class Utils {
    private static final String TAG = "Utils";

    public static boolean isUiThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public void saveBitmapFile(Context context, Bitmap bitmap) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String filePath = context.getExternalFilesDir(null).getAbsolutePath() + "/screenshots/" + "myscreen_" + dateFormat.format(new Date(System.currentTimeMillis())) + ".png";
        // write bitmap to a file
        writeBitmapToFile(context, bitmap, filePath);
    }

    public void writeBitmapToFile(Context context, Bitmap bitmap, String filePath) {
        File file = new File(filePath);
        FileOutputStream fos = null;
        try {
            File parentFile = file.getParentFile();
            parentFile.mkdirs();
            file.createNewFile();
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            System.out.println("insertImage:" + filePath);
            Log.e(TAG, "captured image: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
    }
}
