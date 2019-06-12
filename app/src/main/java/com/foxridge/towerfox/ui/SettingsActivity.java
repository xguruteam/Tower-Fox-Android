package com.foxridge.towerfox.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.solver.GoalRow;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.foxridge.towerfox.App;
import com.foxridge.towerfox.R;
import com.foxridge.towerfox.service.response.BaseResponse;
import com.foxridge.towerfox.utils.Globals;
import com.foxridge.towerfox.utils.Helper;
import com.foxridge.towerfox.viewmodels.ServerIPAddressViewModel;
import com.foxridge.towerfox.viewmodels.SettingsViewModel;
import com.foxridge.towerfox.views.CustomEditText;
import com.foxridge.towerfox.views.CustomFontTextView;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;


public class SettingsActivity extends BaseActivity implements View.OnClickListener {

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

    @BindView(R.id.btn_about_closeout)
    LinearLayout btnAboutCloseOut;
    @BindView(R.id.btn_sync_server_address)
    LinearLayout btnSyncServerAddress;
    @BindView(R.id.btn_account)
    LinearLayout btnAccount;
    @BindView(R.id.btn_log)
    LinearLayout btnLog;
    @BindView(R.id.view_about_closeout)
    LinearLayout viewAboutCloseOut;
    @BindView(R.id.view_sync_server_address)
    LinearLayout viewSyncServerAddress;
    @BindView(R.id.view_account)
    LinearLayout viewAccount;
    @BindView(R.id.view_log)
    LinearLayout viewLog;
    @BindView(R.id.iv_about_closeout)
    ImageView ivAboutCloseOut;
    @BindView(R.id.iv_sync_server_address)
    ImageView ivSyncServerAddress;
    @BindView(R.id.iv_account)
    ImageView ivAccount;
    @BindView(R.id.iv_log)
    ImageView ivLog;
    @BindView(R.id.tv_about_closeout)
    CustomFontTextView tvAboutCloseOut;
    @BindView(R.id.tv_sync_server_address)
    CustomFontTextView tvSyncServerAddress;
    @BindView(R.id.tv_account)
    CustomFontTextView tvAccount;
    @BindView(R.id.tv_log)
    CustomFontTextView tvLog;

    @BindView(R.id.tv_about_data)
    CustomFontTextView tvAboutData;
    @BindView(R.id.edt_server_ip)
    CustomEditText edtServerIp;
    @BindView(R.id.tv_change_server)
    CustomFontTextView tvChangeServer;
    @BindView(R.id.tv_account_name)
    CustomFontTextView tvAccountName;
    @BindView(R.id.tv_logout)
    CustomFontTextView tvLogout;
    @BindView(R.id.tv_call_support)
    CustomFontTextView tvCallSupport;
    @BindView(R.id.tv_email_log)
    CustomFontTextView tvSendEmailLog;

    String version = "";
    String deviceVersion = "";
    String model = "";

    private AlertDialog dialog;
    private KProgressHUD loader;
    private ServerIPAddressViewModel viewModel;
    private SettingsViewModel settingsViewModel;
    private static final int REQUEST_READ_PHONE_STATE = 91;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        viewModel = ViewModelProviders.of(this).get(ServerIPAddressViewModel.class);
        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        initView();

    }

    @Override
    public int addView() {
        return  R.layout.activity_settings;
    }

    public void initView() {
        leftButton.setVisibility(View.GONE);
        tvTitle.setText(R.string.please_login);
        tvRight.setText(R.string.string_back);
        rightButton.setOnClickListener(this);
        btnAboutCloseOut.setOnClickListener(this);
        btnAccount.setOnClickListener(this);
        btnSyncServerAddress.setOnClickListener(this);
        btnLog.setOnClickListener(this);
        viewAboutCloseOut.setVisibility(View.GONE);
        viewSyncServerAddress.setVisibility(View.GONE);
        viewAccount.setVisibility(View.GONE);
        viewLog.setVisibility(View.GONE);
        ivAboutCloseOut.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
        ivSyncServerAddress.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
        ivAccount.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
        ivLog.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
        tvAboutCloseOut.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
        tvSyncServerAddress.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
        tvAccount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
        tvLog.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));

        tvChangeServer.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        tvSendEmailLog.setOnClickListener(this);
        tvCallSupport.setOnClickListener(this);
        setObservers();
        if (Globals.getInstance().storage_loadString("setting_index").equals("1")) {
            viewAboutCloseOut.performClick();
        } else if (Globals.getInstance().storage_loadString("setting_index").equals("2")) {
            viewSyncServerAddress.performClick();
        } else if (Globals.getInstance().storage_loadString("setting_index").equals("3")) {
            viewAccount.performClick();
        } else if (Globals.getInstance().storage_loadString("setting_index").equals("4")) {
            viewLog.performClick();
        }

        try {
            Context context = getBaseContext();
            PackageManager pm = context.getPackageManager();
            PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
            deviceVersion = Build.VERSION.RELEASE;
            model = Build.DEVICE;
            tvAboutData.setText(String.format("Tower Fox for Android\nVersion %s\n\nDevice Model: %s\nDevice Version: %s", version, model, deviceVersion));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {
        hideSoftKeyboard(this);
        switch (v.getId()) {
            case R.id.btn_right:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                break;
            case R.id.btn_about_closeout:
                if (viewAboutCloseOut.getVisibility() == View.VISIBLE) {
                    viewSyncServerAddress.setVisibility(View.GONE);
                    viewAccount.setVisibility(View.GONE);
                    viewLog.setVisibility(View.GONE);
                    viewAboutCloseOut.setVisibility(View.GONE);
                    ivAboutCloseOut.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivSyncServerAddress.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivAccount.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivLog.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    tvAboutCloseOut.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvSyncServerAddress.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvAccount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvLog.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    Globals.getInstance().storage_saveObject("setting_index", "1");
                } else {
                    viewSyncServerAddress.setVisibility(View.GONE);
                    viewAccount.setVisibility(View.GONE);
                    viewLog.setVisibility(View.GONE);
                    viewAboutCloseOut.setVisibility(View.VISIBLE);
                    ivSyncServerAddress.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivAccount.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivLog.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    tvSyncServerAddress.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvAccount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvLog.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    ivAboutCloseOut.setImageResource(R.drawable.ic_keyboard_arrow_down_gray_24px);
                    tvAboutCloseOut.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                    Globals.getInstance().storage_saveObject("setting_index", "");
                }
                break;
            case R.id.btn_sync_server_address:
                if (viewSyncServerAddress.getVisibility() == View.VISIBLE) {
                    viewAboutCloseOut.setVisibility(View.GONE);
                    viewAccount.setVisibility(View.GONE);
                    viewLog.setVisibility(View.GONE);
                    viewSyncServerAddress.setVisibility(View.GONE);
                    ivAboutCloseOut.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivSyncServerAddress.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivAccount.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivLog.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    tvAboutCloseOut.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvSyncServerAddress.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvAccount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvLog.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    Globals.getInstance().storage_saveObject("setting_index", "2");
                } else {
                    viewAboutCloseOut.setVisibility(View.GONE);
                    viewAccount.setVisibility(View.GONE);
                    viewLog.setVisibility(View.GONE);
                    viewSyncServerAddress.setVisibility(View.VISIBLE);
                    ivAboutCloseOut.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivAccount.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivLog.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    tvAboutCloseOut.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvAccount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvLog.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    ivSyncServerAddress.setImageResource(R.drawable.ic_keyboard_arrow_down_gray_24px);
                    tvSyncServerAddress.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                    Globals.getInstance().storage_saveObject("setting_index", "");
                }
                break;
            case R.id.btn_account:
                if (viewAccount.getVisibility() == View.VISIBLE) {
                    viewAboutCloseOut.setVisibility(View.GONE);
                    viewSyncServerAddress.setVisibility(View.GONE);
                    viewLog.setVisibility(View.GONE);
                    viewAccount.setVisibility(View.GONE);
                    ivAboutCloseOut.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivSyncServerAddress.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivAccount.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivLog.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    tvAboutCloseOut.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvSyncServerAddress.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvAccount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvLog.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    Globals.getInstance().storage_saveObject("setting_index", "3");
                } else {
                    viewAboutCloseOut.setVisibility(View.GONE);
                    viewSyncServerAddress.setVisibility(View.GONE);
                    viewLog.setVisibility(View.GONE);
                    viewAccount.setVisibility(View.VISIBLE);
                    ivAboutCloseOut.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivSyncServerAddress.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivLog.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    tvAboutCloseOut.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvSyncServerAddress.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvLog.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    ivAccount.setImageResource(R.drawable.ic_keyboard_arrow_down_gray_24px);
                    tvAccount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                    Globals.getInstance().storage_saveObject("setting_index", "");
                }
                break;
            case R.id.btn_log:
                if (viewLog.getVisibility() == View.VISIBLE) {
                    viewAboutCloseOut.setVisibility(View.GONE);
                    viewSyncServerAddress.setVisibility(View.GONE);
                    viewAccount.setVisibility(View.GONE);
                    viewLog.setVisibility(View.GONE);
                    ivAboutCloseOut.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivSyncServerAddress.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivAccount.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivLog.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    tvAboutCloseOut.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvSyncServerAddress.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvAccount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvLog.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    Globals.getInstance().storage_saveObject("setting_index", "4");
                } else {
                    viewAboutCloseOut.setVisibility(View.GONE);
                    viewSyncServerAddress.setVisibility(View.GONE);
                    viewAccount.setVisibility(View.GONE);
                    viewLog.setVisibility(View.VISIBLE);
                    ivAboutCloseOut.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivSyncServerAddress.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    ivAccount.setImageResource(R.drawable.ic_keyboard_arrow_right_gray_24px);
                    tvAboutCloseOut.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvSyncServerAddress.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    tvAccount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_text_dark_gray));
                    ivLog.setImageResource(R.drawable.ic_keyboard_arrow_down_gray_24px);
                    tvLog.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                    Globals.getInstance().storage_saveObject("setting_index", "");
                }
                break;

            case R.id.tv_change_server:
                checkServerIp();
                break;
            case R.id.tv_logout:
                showLogoutAlert();
                break;
            case R.id.tv_call_support:
                callSupport();
                break;
            case R.id.tv_email_log:
                requestSupport();
                break;
        }

    }

    public void callSupport() {
        new AlertDialog.Builder(this).setTitle("Support Call").setMessage("Call the Help Desk for assistance at\n602-540-3771")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "6025403771", null));
                startActivity(intent);
            }
        }).show();

    }

    public void requestSupport() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL,new String[] { "info@foxridgellc.com" });
        intent.putExtra(Intent.EXTRA_SUBJECT, "CMP Support Request");
        intent.putExtra(Intent.EXTRA_TEXT, "Hi,\nPlease describe your issue below this line:");
        startActivity(Intent.createChooser(intent, "CMP Support Request"));
    }

    public void checkServerIp() {
        if (!Globals.getInstance().storage_loadString("SYNC").endsWith("")) {
            Helper.showErrorDialog(this, "Synchronizing. Please wait.");
            return;
        }
        if (edtServerIp.getText().toString().equals(Globals.getInstance().storage_loadString("SERVER_IP"))) {
            Helper.showErrorDialog(this, "Please Enter Different IP Address");
            return;
        }
        if (edtServerIp.getText().toString().contains(" ")) {
            Helper.showErrorDialog(this, "Please Enter Valid IP Address");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("IP Address").setMessage("Changing IP Address clears your Local Data. Do you want to change IP?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        loader.show();
                        viewModel.checkServerConnectivtiyTest(edtServerIp.getText().toString());
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public void showLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Logout").setMessage("Changing User clears your Local Data. Do you want to change User?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sendLogoutDeviceInfo();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();

    }

    public void sendLogoutDeviceInfo() {
        long timeinterval = new Date().getTime();
        HashMap<String, String> request = new HashMap<>();
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        request.put("DeviceID", androidId);
        request.put("DeviceModel", model);
        request.put("DevicePlatform", "Android");
        request.put("DeviceToken", Globals.getInstance().storage_loadString("TokenID"));
        request.put("DeviceVersion", deviceVersion);
        request.put("LoginDate", "/Date("+timeinterval+")/");
        request.put("ProjectID", Globals.getInstance().storage_loadString("ProjectID"));
        request.put("UserName", Globals.getInstance().storage_loadString("UserName"));
        settingsViewModel.logout(request);
    }

    public String getUniqueID() {
        String myAndroidDeviceId = "";
        TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPhoneStausPermission();
            return "";
        }
        if (mTelephony.getDeviceId() != null) {
            myAndroidDeviceId = mTelephony.getDeviceId();
        }else{
            myAndroidDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return myAndroidDeviceId;
    }

    public void requestPhoneStausPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;

            default:
                break;
        }
    }

    public void setObservers() {
        settingsViewModel.getServerIP();
        settingsViewModel.getUserName();
        settingsViewModel.getServerIPLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    edtServerIp.setText(s);
                }
            }
        });

        settingsViewModel.getLoggedUserData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    tvAccountName.setText(s);
                }
            }
        });

        settingsViewModel.getResponseLogoutData().observe(this, new Observer<BaseResponse>() {
            @Override
            public void onChanged(@Nullable BaseResponse baseResponse) {
                loader.dismiss();
                if (baseResponse != null) {
                    if (baseResponse.getServiceStatus().equals("SUCCESS")) {
                        Globals.getInstance().storage_removeItem("UserName");
                        Globals.getInstance().storage_removeItem("SyncTime");
                        String serverIp = Globals.getInstance().storage_loadString("SERVER_IP");
                        Globals.getInstance().editor.clear().apply();
                        Globals.getInstance().storage_saveObject("SERVER_IP", serverIp);
                        Globals.getInstance().storage_saveObject("change_server", false);
                        gotoServerIpAddress();
                    }else{
                        Helper.showErrorDialog(SettingsActivity.this, "Logout Failed");
                    }
                }else{
                    Helper.showErrorDialog(SettingsActivity.this, "Logout Failed");
                }
            }
        });

        viewModel.getServerConnectivity().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (loader != null && loader.isShowing()){
                    loader.dismiss();
                }
                if (aBoolean != null && aBoolean) {
                    Globals.getInstance().storage_removeItem("SyncTime");
                    Globals.getInstance().storage_removeItem("UserName");
                    Globals.getInstance().editor.clear().apply();
                    Globals.getInstance().storage_saveObject("SERVER_IP", edtServerIp.getText().toString());
                    Globals.getInstance().storage_saveObject("change_server", true);
                    gotoServerIpAddress();
                }else{
                    if (aBoolean != null && !aBoolean) {
                        Helper.showErrorDialog(SettingsActivity.this, "Server Ip address is not valid.");
                    }
                }
            }
        });
    }

    public void gotoServerIpAddress() {
        Globals.getInstance().projectsGlobalArray.clear();
        Globals.getInstance().categoriesListNameArray.clear();
        Globals.getInstance().categoriesListStringArray.clear();
        Globals.getInstance().init(App.getApp());
        App.getApp().getProjectsRepository().clearAll();
        restartApp();
    }

    public void restartApp() {
        Intent i = App.getApp().getPackageManager()
                .getLaunchIntentForPackage( App.getApp().getPackageName() );
        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(i);
        finish();
    }
}
