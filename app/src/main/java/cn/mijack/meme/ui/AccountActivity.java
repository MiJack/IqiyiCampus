package cn.mijack.meme.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.mijack.meme.R;
import cn.mijack.meme.base.BaseActivity;
import cn.mijack.meme.remote.ApiService;
import cn.mijack.meme.remote.RetrofitClient;
import cn.mijack.meme.user.UserManager;
import cn.mijack.meme.utils.TextHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Mr.Yuan
 * @date 2017/6/8
 */
public class AccountActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AccountActivity";
    private static final int LOGIN = 1;
    private static final int CREATE = 2;
    TextView info;
    TextInputLayout userNameLayout;
    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;
    Button otherChoice;
    Button nextAction;
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    int status;
    ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        info = (TextView) findViewById(R.id.info);
        RetrofitClient client = RetrofitClient.getInstance();
        apiService = client.createApi(ApiService.class);
        userNameLayout = (TextInputLayout) findViewById(R.id.userNameLayout);
        emailLayout = (TextInputLayout) findViewById(R.id.emailLayout);
        passwordLayout = (TextInputLayout) findViewById(R.id.passwordLayout);
        otherChoice = (Button) findViewById(R.id.otherChoice);
        nextAction = (Button) findViewById(R.id.nextAction);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content);
        emailLayout = (TextInputLayout) findViewById(R.id.emailLayout);
        passwordLayout = (TextInputLayout) findViewById(R.id.passwordLayout);
        otherChoice.setOnClickListener(this);
        nextAction.setOnClickListener(this);
        setStatus(LOGIN);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextAction:
                String email = TextHelper.getText(emailLayout);
                if (TextUtils.isEmpty(email)) {
                    emailLayout.setError(getString(R.string.email_is_empty));
                    return;
                }
                if (!TextHelper.isEmail(email)) {
                    emailLayout.setError(getString(R.string.email_format_is_not_correct));
                    return;
                }
                String password = TextHelper.getText(passwordLayout);
                if (TextUtils.isEmpty(password)) {
                    passwordLayout.setError(getString(R.string.password_is_empty));
                    return;
                }
                if (password.length() < 6) {
                    passwordLayout.setError(getString(R.string.password_too_short));
                    return;
                }
                if (status == LOGIN) {
                    login(email, password);
                } else if (status == CREATE) {
                    String nickName = TextHelper.getText(userNameLayout);
                    create(nickName, email, password);
                }
                break;
            case R.id.otherChoice:
                if (status == LOGIN) {
                    setStatus(CREATE);
                } else if (status == CREATE) {
                    setStatus(LOGIN);
                }
                break;
        }
    }

    private void setStatus(int s) {
        status = s;
        if (status == CREATE) {
            nextAction.setText(R.string.create_account);
            otherChoice.setText(R.string.login);
            info.setText(R.string.signin_with_your_email);
            userNameLayout.setVisibility(View.VISIBLE);
        } else if (status == LOGIN) {
            userNameLayout.setVisibility(View.GONE);
            otherChoice.setText(R.string.create_account);
            nextAction.setText(R.string.login);
            info.setText(R.string.login_with_your_account);
        }
    }

    private void create(String nickName, String email, String password) {
        apiService.register(nickName, email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(userResult -> {
                    int code = userResult.getCode();
                    if (code == 200) {
                        setResult(RESULT_OK);
                        UserManager.get(this).save(userResult.getData());
                        finish();
                        Toast.makeText(this, R.string.register_ok, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, userResult.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void login(String email, String password) {
        apiService.login(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(userResult -> {
                    int code = userResult.getCode();
                    if (code == 200) {
                        setResult(RESULT_OK);
                        UserManager.get(this).save(userResult.getData());
                        Toast.makeText(this, R.string.login_ok, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, userResult.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
