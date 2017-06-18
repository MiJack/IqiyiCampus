package cn.mijack.meme.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qiniu.android.common.AutoZone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import cn.mijack.meme.BuildConfig;
import cn.mijack.meme.R;
import cn.mijack.meme.adapter.EmojiPageAdapter;
import cn.mijack.meme.base.BaseActivity;
import cn.mijack.meme.model.Emoji;
import cn.mijack.meme.model.TokenEntity;
import cn.mijack.meme.model.UploadUnit;
import cn.mijack.meme.model.VideoInfo;
import cn.mijack.meme.remote.ApiResponse;
import cn.mijack.meme.remote.Result;
import cn.mijack.meme.user.UserManager;
import cn.mijack.meme.utils.TextHelper;
import cn.mijack.meme.utils.Utils;
import cn.mijack.meme.view.ColorPickerDialog;
import cn.mijack.meme.view.IntentSelectSheetDialogFragment;
import cn.mijack.meme.view.MemeView;
import cn.mijack.meme.view.UploadDialog;
import cn.mijack.meme.vm.MemeViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.relex.circleindicator.CircleIndicator;

public class MemeActivity extends BaseActivity {
    private static final int SPAN_COUNT = 8;
    public static final int REQUEST_CODE_CHANGE_PERMISSION = 2;
    public static final int REQUEST_CODE_SHARE = 3;
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_CODE_PICK_APPLICATION = 4;
    private static final String FRAGMENT = "fragment";
    private static final int REQUEST_CODE_LOGIN = 5;
    private static final String DIALOG = "dialog";
    VideoInfo videoInfo;
    private int progess;
    private GridLayoutManager layoutManager;
    private MemeViewModel memeViewModel;
    private EmojiPageAdapter emojiPageAdapter;
    private DrawerLayout drawerLayout;
    private Observer<ApiResponse<Result<List<Emoji>>>> dataObserver = emojiResult -> {
        if (emojiResult == null) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            return;
        }
        List<Emoji> data = emojiResult.body.getData();
//        emojiAdapter.setData(data);
        emojiPageAdapter.setEmojis(data);
    };
    private ViewPager viewPager;
    private MemeView memeView;
    Observer<String> emojiObserver = url -> {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        memeView.setEmojiBitmapUrl(url);
        drawerLayout.closeDrawer(Gravity.RIGHT);
    };
    private Configuration config;
    private UploadManager uploadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = new Configuration.Builder().zone(AutoZone.autoZone).build();
        uploadManager = new UploadManager(config);

        setContentView(R.layout.activity_meme);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        if (!intent.hasExtra("image")) {
            finish();
            return;
        }
        memeViewModel = ViewModelProviders.of(this).get(MemeViewModel.class);

        videoInfo = (VideoInfo) intent.getSerializableExtra("videoInfo");
        memeView = (MemeView) findViewById(R.id.memeView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        progess = intent.getIntExtra("progress", -1);
        layoutManager = new GridLayoutManager(this, SPAN_COUNT, GridLayoutManager.HORIZONTAL, false);
        emojiPageAdapter = new EmojiPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(emojiPageAdapter);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        emojiPageAdapter.registerDataSetObserver(indicator.getDataSetObserver());
        final ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
        layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewPager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;
        memeViewModel.loadEmoji().observe(this, dataObserver);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        memeViewModel.getEmojiUrlLiveData().observe(this, emojiObserver);
        String bitmap = intent.getStringExtra("image");
        Observable.just(bitmap)
                .subscribeOn(Schedulers.io())
                .map(s -> {
                    FileInputStream fileInputStream = openFileInput(s);
                    return BitmapFactory.decodeStream(fileInputStream);
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap1 -> memeView.setImageBitmap(bitmap1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meme, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionText:
                showInputTextDialog();
                break;
            case R.id.actionTextColor:
                showColorPicker();
                break;
            case R.id.actionFontSizeDown:
                memeView.makeFontSizeDown();
                break;
            case R.id.actionFontSizeUp:
                memeView.makeFontSizeUp();
                break;
            case R.id.actionEmoji:
                drawerLayout.openDrawer(Gravity.RIGHT);
                break;
            case R.id.actionEmojiSmaller:
                memeView.makeEmojiSizeDown();
                break;
            case R.id.actionEmojiBigger:
                memeView.makeEmojiSizeUp();
                break;
            case R.id.actionUpload:
                uploadImage();
                break;
            case R.id.actionShare:
                checkPermissionAndShare();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void uploadImage() {
        if (UserManager.get(this).isLogin()) {
            uploadToQiniu();
        } else {
            showLoginDialog();
        }
    }

    UploadDialog uploadDialog;
    Gson gson = new Gson();

    @NonNull
    private void uploadToQiniu() {
        memeViewModel.requestToken(UserManager.get(this).getUser().getUid(), videoInfo.tId, progess,videoInfo.title,
                videoInfo.shortTitle, videoInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(token -> {
                    uploadDialog = new UploadDialog(this);
                    uploadDialog.showRequestToken();
                })
                .observeOn(Schedulers.io())
                .map(resultApiResponse -> resultApiResponse.getData())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(disposable -> uploadDialog.showGenerateByteData())
                .observeOn(Schedulers.io())
                .map(tokenEntity -> {
                    Bitmap bmp = Utils.getViewBp(memeView);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    return new UploadUnit(byteArray,tokenEntity.getKey(),tokenEntity.getToken());
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((UploadUnit s) -> {
                    uploadDialog.showUploadProgress(0);
                    uploadManager.put(s.getData(), s.getKey(), s.getToken(), (key, info, response) -> {
                        System.out.println("[complete]key:" + key);
                        try {
                            if (response.has("code") && response.getInt("code") == 200) {
                                uploadDialog.showSuccess();
                            } else {
                                uploadDialog.showFail(response.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            uploadDialog.showFail();
                        }
                    }, new UploadOptions(null, null, true, new UpProgressHandler() {
                        @Override
                        public void progress(String key, double percent) {
                            System.out.println("key:" + key + "\tpercent:" + percent);
                            uploadDialog.showUploadProgress(percent);
                        }
                    }, null));
                    System.out.println("token:" + s);
                }, throwable -> {
                    Toast.makeText(this, "上传失败", Toast.LENGTH_SHORT).show();
                });
    }

    private void showLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle("请登录")
                .setMessage("上传图片需要登录")
                .setPositiveButton("前往登录", (dialog, which) -> {
                    Intent intent = new Intent(this, AccountActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_LOGIN);
                    dialog.cancel();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {

                }).create().show();
    }

    private void checkPermissionAndShare() {
        //请求读取权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this).setTitle("请求权限")
                        .setTitle("截图保持在SD卡中，需要存储权限，请前往设置打开权限重试")
                        .setCancelable(false)
                        .setPositiveButton("确定", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_CODE_CHANGE_PERMISSION);
                            dialog.dismiss();
                        })
                        .setNegativeButton("取消", (dialog, which) -> Toast.makeText(MemeActivity.this, "权限请求失败，无法截图", Toast.LENGTH_SHORT).show()).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            }
        } else {
            shareMeme();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHANGE_PERMISSION) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                shareMeme();
            }
        }
        if (requestCode == REQUEST_CODE_LOGIN) {
            if (UserManager.get(this).isLogin()) {
                uploadToQiniu();
            } else {
                Toast.makeText(this, "登录不成功，无法上传", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CHANGE_PERMISSION) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                shareMeme();
            } else {
                new AlertDialog.Builder(this).setTitle("请求权限")
                        .setTitle("截图保持在SD卡中，需要存储权限，请前往设置打开权限重试")
                        .setCancelable(false)
                        .setPositiveButton("确定", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_CODE_CHANGE_PERMISSION);
                            dialog.dismiss();
                        })
                        .setNegativeButton("取消", (dialog, which) -> Toast.makeText(MemeActivity.this, "权限请求失败，无法截图", Toast.LENGTH_SHORT).show()).create().show();
            }
        }
    }

    private AlertDialog dialog;

    private void shareMeme() {
        Observable.just(memeView)
                .doOnNext(memeView -> {
                    dialog = new AlertDialog.Builder(MemeActivity.this)
                            .setCancelable(false)
                            .setTitle(R.string.dialog_title)
                            .setView(R.layout.progress_dialog)
                            .create();
                    dialog.show();
                })
                .observeOn(Schedulers.io())
                .map(memeView -> Utils.getViewBp(memeView))
                .map(bitmap -> Utils.saveBitmapFile(MemeActivity.this, bitmap))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(filePath -> {
                    if (dialog != null) {
                        dialog.cancel();
                    }

                    Uri fileUri = FileProvider.getUriForFile(MemeActivity.this, getString(R.string.file_provider_authority), new File(filePath));
//                    intent.setDataAndType(fileUri, getContentResolver().getType(fileUri));
                    Intent intent = ShareCompat.IntentBuilder.from(this)
                            .setType(getContentResolver().getType(fileUri))
                            .setStream(fileUri)
                            .getIntent();
                    intent.setData(fileUri);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//请求URI授权读取+
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//
//                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    // First search for compatible apps with sharing (Intent.ACTION_SEND)
                    ArrayList<ActivityInfo> activityInfos = new ArrayList<>();
                    List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(intent, 0);
                    if (!resInfo.isEmpty()) {
                        for (ResolveInfo resolveInfo : resInfo) {
                            activityInfos.add(resolveInfo.activityInfo);
                        }
                        IntentSelectSheetDialogFragment fragment = new IntentSelectSheetDialogFragment();
                        Bundle args = new Bundle();
                        args.putParcelable("intent", intent);
                        args.putParcelableArrayList("activityInfos", activityInfos);
                        fragment.setArguments(args);
                        fragment.show(getSupportFragmentManager(), FRAGMENT);
                    } else {
                        Toast.makeText(this, "无法分享", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showInputTextDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_meme_content, null);
        EditText editText = (EditText) view.findViewById(R.id.editText);
        editText.setText(memeView.getText());
        new AlertDialog.Builder(this)
                .setTitle(R.string.meme_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    EditText e = (EditText) view.findViewById(R.id.editText);
                    dialog.cancel();
                    memeView.setText(TextHelper.getText(e));
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.cancel();
                })
                .create().show();
    }

    private void showColorPicker() {
        new ColorPickerDialog(this, memeView.getTextColor())
                .setListener((alertDialog, rgb) -> {
                    alertDialog.dismiss();
                    memeView.setTextColor(rgb);
                }).show();
    }
}
