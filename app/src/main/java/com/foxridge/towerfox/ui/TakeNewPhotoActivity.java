package com.foxridge.towerfox.ui;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.utils.Globals;
import com.foxridge.towerfox.utils.Helper;
import com.foxridge.towerfox.viewmodels.SyncViewModel;
import com.foxridge.towerfox.views.CustomEditText;
import com.foxridge.towerfox.views.CustomFontTextView;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


public class TakeNewPhotoActivity extends BaseActivity implements View.OnClickListener {

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

    @BindView(R.id.edt_photo_name)
    CustomEditText edtPhotoName;

    @BindView(R.id.edt_photo_description)
    CustomEditText edtPhotoDescription;
    @BindView(R.id.tv_category_name)
    CustomFontTextView tvCategoryName;
    @BindView(R.id.btn_take_photo)
    RelativeLayout btnTakePhoto;
    @BindView(R.id.backgroundview)
    RelativeLayout backgroundView;

    private SyncViewModel syncViewModel;
    private KProgressHUD loader;
    private static final int REQUEST_CAMERA = 104;
    private static final int REQUEST_CAMERA_PERMISSION = 105;

    Uri imageUri;
    private final static int REQUEST_TAKE_PHOTO = 106;

    @Override
    public int addView() {
        return  R.layout.activity_take_new_photo;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        syncViewModel = ViewModelProviders.of(this).get(SyncViewModel.class);
        initView();
    }

    public void initView() {
        leftButton.setOnClickListener(this);
        tvTitle.setText(R.string.add_photo);
        tvLeft.setText(R.string.string_cancel);
        rightButton.setVisibility(View.GONE);
        btnTakePhoto.setOnClickListener(this);
        backgroundView.setOnClickListener(this);

        String title = "";
        for (String cat : Globals.getInstance().categoriesStack) {
            if (title.equals("")) {
                title = cat;
            }else{
                title = String.format("%s >> %s", title, cat);
            }
        }

        tvCategoryName.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                hideSoftKeyboard(this);
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case R.id.btn_take_photo:
                hideSoftKeyboard(this);
                checkCameraPermission();
                break;
            case R.id.backgroundview:
                hideSoftKeyboard(this);
                break;
        }
    }

    public void checkCameraPermission() {
        if (edtPhotoName.getText().toString().isEmpty()) {
            Helper.showErrorDialog(this, "Photo ID is required.");
            return;
        }
        if (edtPhotoDescription.getText().toString().isEmpty()) {
            Helper.showErrorDialog(this, "Photo Description is required.");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.need_files_permission);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(TakeNewPhotoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else {
            EasyImage.openCamera(this, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO ) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        }else{
            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                    //Some error handling
                    Helper.showErrorDialog(TakeNewPhotoActivity.this, e.getLocalizedMessage());
                }

                @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                    ExifInterface ei = null;
                    try {
                        ei = new ExifInterface(imageFile.getAbsolutePath());
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);
                        Log.e("Orientation", "rotated: "+orientation);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Globals.getInstance().storage_saveObject("ItemName", edtPhotoName.getText().toString());
                    Globals.getInstance().storage_saveObject("Description", edtPhotoDescription.getText().toString());
                    Log.e("Camera", "file: " + imageFile.getAbsolutePath() + ", " + imageFile.length());
                    Globals.getInstance().storage_saveObject("photoPath", imageFile.getAbsolutePath());
                    Globals.getInstance().storage_saveObject("isAdhoc", true);
                    Globals.getInstance().storage_saveObject("isRList", false);
                    startActivityForResult(new Intent(TakeNewPhotoActivity.this, PhotosDetailActivity.class), REQUEST_TAKE_PHOTO);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
//                viewModel.sendImage(imageFile);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    EasyImage.openCamera(this, 0);
                } else {
                    Toast.makeText(this, R.string.access_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

}
