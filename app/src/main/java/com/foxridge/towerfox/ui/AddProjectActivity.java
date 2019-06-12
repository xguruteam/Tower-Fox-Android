package com.foxridge.towerfox.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.utils.Globals;
import com.foxridge.towerfox.utils.Helper;
import com.foxridge.towerfox.viewmodels.SyncViewModel;
import com.foxridge.towerfox.views.CustomEditText;
import com.foxridge.towerfox.views.CustomFontTextView;
import com.kaopiz.kprogresshud.KProgressHUD;

import butterknife.BindView;


public class AddProjectActivity extends BaseActivity implements View.OnClickListener {

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

    @BindView(R.id.edt_project_id)
    CustomEditText edtProjectID;
    @BindView(R.id.backgroundview)
    RelativeLayout backgroundview;

    private SyncViewModel syncViewModel;
    private KProgressHUD loader;

    @Override
    public int addView() {
        return  R.layout.activity_add_project;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setLabel("Sending Device Detail")
                .setDimAmount(0.5f);
        syncViewModel = ViewModelProviders.of(this).get(SyncViewModel.class);
        initView();
        syncViewModel.getJsonDevicePostLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String aBoolean) {
                if (aBoolean != null) {
                    if (aBoolean.equals("")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                syncViewModel.getProjectsArray();
                            }
                        }).start();
                    }else{
                        loader.dismiss();
                        Helper.showErrorDialog(AddProjectActivity.this, aBoolean);
                    }
                }
            }
        });
        syncViewModel.getGetProjectArrayData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                loader.dismiss();
                if (aBoolean != null) {
                    if (aBoolean) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    }else{
                        Helper.showErrorDialog(AddProjectActivity.this, "Sync Failed, Please try again");
                    }
                }

            }
        });
    }

    public void initView() {
        leftButton.setOnClickListener(this);
        tvTitle.setText(R.string.string_add_project);
        tvLeft.setText(R.string.string_cancel);
        tvRight.setText(R.string.string_next);
        rightButton.setOnClickListener(this);
        backgroundview.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                hideSoftKeyboard(this);
                addProject();
                break;
            case R.id.btn_left:
                hideSoftKeyboard(this);
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case R.id.backgroundview:
                hideSoftKeyboard(this);
                break;
        }
    }

    public void addProject() {
        if (edtProjectID.getText().toString().isEmpty()) {
            Helper.showErrorDialog(this, "ProjectID is cannot be empty");
            return;
        }
        if (!Globals.getInstance().storage_loadString("SYNC").equals("")) {
            Helper.showErrorDialog(this, "Synchronizing, please wait.");
            return;
        }
        if (!Globals.getInstance().storage_loadString(edtProjectID.getText().toString()).equals("")) {
            Helper.showErrorDialog(this, "Project already exist");
            return;
        }
        loader.show();
        Globals.getInstance().storage_saveObject("ProjectID", edtProjectID.getText().toString());
        syncViewModel.sendDeviceInfo(this);

    }
}
