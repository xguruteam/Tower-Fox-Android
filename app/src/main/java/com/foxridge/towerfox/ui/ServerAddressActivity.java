package com.foxridge.towerfox.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.utils.Globals;
import com.foxridge.towerfox.utils.Helper;
import com.foxridge.towerfox.viewmodels.ServerIPAddressViewModel;
import com.foxridge.towerfox.views.CustomEditText;
import com.foxridge.towerfox.views.CustomFontTextView;
import com.kaopiz.kprogresshud.KProgressHUD;

import butterknife.BindView;
import butterknife.OnClick;


public class ServerAddressActivity extends BaseActivity implements View.OnClickListener {


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

    @BindView(R.id.edt_server_address)
    CustomEditText edtServerAddress;
    private AlertDialog dialog;
    private KProgressHUD loader;
    private ServerIPAddressViewModel viewModel;

    @Override
    public int addView() {
        return  R.layout.activity_server_address;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        viewModel = ViewModelProviders.of(this).get(ServerIPAddressViewModel.class);

        initView();
        setObservers();
        if (!Globals.getInstance().storage_loadString("SERVER_IP").equals("") && !Globals.getInstance().storage_loadBool("change_server")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Globals.getInstance().storage_saveObject("change_server", false);
                    Intent homeIntent = new Intent(ServerAddressActivity.this, LoginActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                }
            }).start();
        }

        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("uuid", androidId);
    }

    public void initView() {
        leftButton.setVisibility(View.GONE);
        tvTitle.setText(R.string.string_server_address);
        tvRight.setText(R.string.string_next);
        rightButton.setOnClickListener(this);
        edtServerAddress.setText(Globals.getInstance().storage_loadString("SERVER_IP"));
    }

    @OnClick(R.id.rootView)
    public void BackgroundClick() {
        hideSoftKeyboard(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                hideSoftKeyboard(this);
                checkValidServerIPAddress();
                break;
            default:
                hideSoftKeyboard(this);
                break;
        }
    }

    public void checkValidServerIPAddress() {
        if (edtServerAddress.getText().toString().isEmpty()) {
            Helper.showErrorDialog(this, "Server IP cannot be empty.");
            return;
        }
        if (edtServerAddress.getText().toString().contains(" ")) {
            Helper.showErrorDialog(this, "Current Server IP is invalid.");
            return;
        }
        loader.show();
        viewModel.checkServerConnectivtiyTest(edtServerAddress.getText().toString());
    }

    public void setObservers() {
        viewModel.getServerConnectivity().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (loader != null && loader.isShowing()){
                    loader.dismiss();
                }
                if (aBoolean != null && aBoolean) {
                    gotoLogin();
                }else{
                    if (aBoolean != null && !aBoolean) {
                        Helper.showErrorDialog(ServerAddressActivity.this, "Server Ip address is not valid.");
                    }
                }
            }
        });
    }

    public void gotoLogin() {
        Globals.getInstance().storage_saveObject("SERVER_IP", edtServerAddress.getText().toString());
        Globals.getInstance().storage_saveObject("change_server", false);
        Intent login = new Intent(ServerAddressActivity.this, LoginActivity.class);
        startActivity(login);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
