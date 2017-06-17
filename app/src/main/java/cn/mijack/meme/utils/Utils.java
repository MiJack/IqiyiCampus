package cn.mijack.meme.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Yuan
 * @date 2017/6/3
 */
public class Utils {
    private static final String TAG = "Utils";

    public static boolean isUiThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static String saveBitmapFile(Context context, Bitmap bitmap) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String filePath = context.getExternalFilesDir(null).getAbsolutePath() + "/screenshots/" + "myscreen_" + dateFormat.format(new Date(System.currentTimeMillis())) + ".png";
        // write bitmap to a file
        return writeBitmapToFile(context, bitmap, filePath);
    }

    public static String writeBitmapToFile(Context context, Bitmap bitmap, String filePath) {
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
        return filePath;
    }

    /**
     * Convert ms to hh:mm:ss
     *
     * @param millis
     * @return
     */
    public static String formatTime(long millis) {
        String result = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return result;
    }

    public static String imageUrl(String url, int width, int height) {
//        emoji.getUrl() + "?imageView2/1/w/64/h/64"
        if (width <= 0 || height <= 0) {
            return url;
        }
        return url + "?imageView2/1/w/" + width + "/h/" + height;
    }
    public static Bitmap getViewBp(View v) {
        if (null == v) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        if (Build.VERSION.SDK_INT >= 11) {
            v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(),
                    View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                    v.getHeight(), View.MeasureSpec.EXACTLY));
            v.layout((int) v.getX(), (int) v.getY(),
                    (int) v.getX() + v.getMeasuredWidth(),
                    (int) v.getY() + v.getMeasuredHeight());
        } else {
            v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        }
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        return b;
    }
}
