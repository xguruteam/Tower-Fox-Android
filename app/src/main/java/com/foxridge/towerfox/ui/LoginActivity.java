package com.foxridge.towerfox.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.service.response.AuthenticateModel;
import com.foxridge.towerfox.utils.Globals;
import com.foxridge.towerfox.utils.Helper;
import com.foxridge.towerfox.viewmodels.LoginViewModel;
import com.foxridge.towerfox.views.CustomEditText;
import com.foxridge.towerfox.views.CustomFontTextView;
import com.kaopiz.kprogresshud.KProgressHUD;

import butterknife.BindView;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_left)
    LinearLayout leftButton;
    @BindView(R.id.iv_left_action)
    ImageView ivLeft;
    @BindView(R.id.tv_left_action)
    CustomFontTextView tvLeft;
    @BindView(R.id.btn_right)
    LinearLayout rightButton;
    @BindView(R.id.iv_right_action)
    ImageView ivRight;
    @BindView(R.id.tv_right_action)
    CustomFontTextView tvRight;
    @BindView(R.id.tv_title)
    CustomFontTextView tvTitle;

    @BindView(R.id.edt_username)
    CustomEditText edtUsername;
    @BindView(R.id.edt_password)
    CustomEditText edtPassword;
    private AlertDialog dialog;
    private KProgressHUD loader;
    private LoginViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        setObservers();
        if (!Globals.getInstance().storage_loadString("UserName").equals("") && !Globals.getInstance().storage_loadBool("logout")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                }
            }).start();
        }
    }

    public void initView() {
        leftButton.setOnClickListener(this);
        tvTitle.setText(R.string.please_login);
        tvRight.setText(R.string.string_next);
        rightButton.setOnClickListener(this);
    }

    @Override
    public int addView() {
        return  R.layout.activity_login;
    }

    @OnClick(R.id.rootView)
    public void BackgroundClick() {
        hideSoftKeyboard(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                hideSoftKeyboard(this);
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case R.id.btn_right:
                hideSoftKeyboard(this);
                checkLogin();
                break;
            default:
                hideSoftKeyboard(this);
                break;
        }
    }

    public void checkLogin() {
        if (edtUsername.getText().toString().isEmpty()) {
            Helper.showErrorDialog(this, "UserName cannot empty");
            return;
        }

        if (edtPassword.getText().toString().isEmpty()) {
            Helper.showErrorDialog(this, "Password cannot empty");
            return;
        }
        loader.show();
        viewModel.login(edtUsername.getText().toString(), edtPassword.getText().toString());
    }

    public void setObservers() {
        viewModel.getResponseLoginLiveData().observe(this, new Observer<AuthenticateModel>() {
            @Override
            public void onChanged(@Nullable AuthenticateModel authenticateModel) {
                if (loader != null && loader.isShowing()){
                    loader.dismiss();
                }
                if (authenticateModel != null) {
                    if (authenticateModel.getStatus().equals("true")) {
                        authenticateModel.save();
                        Globals.getInstance().storage_saveObject("UserName", edtUsername.getText().toString());
                        gotoMain();
                    }else{
                        Helper.showErrorDialog(LoginActivity.this, "Login failed.");
                    }
                }else{
                    Helper.showErrorDialog(LoginActivity.this, "Login failed.");
                }
            }
        });

        viewModel.getErrorLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (loader != null && loader.isShowing()){
                    loader.dismiss();
                }
                Helper.showErrorDialog(LoginActivity.this, "Login failed.");
            }
        });
    }

    public void gotoMain() {
        Intent login = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(login);
        finish();
    }
}
