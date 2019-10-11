package com.foxridge.towerfox.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.utils.Globals;
import com.foxridge.towerfox.views.CustomFontTextView;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;

import java.io.File;
import java.net.URI;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoCropActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_left)
    LinearLayout leftButton;
    @BindView(R.id.iv_action_bar_shadow)
    ImageView ivActionBarShadow;
    @BindView(R.id.btn_right)
    LinearLayout rightButton;
    @BindView(R.id.tv_title)
    CustomFontTextView tvTitle;

    public static void startActivity(Context act, String imagePath) {
        Intent i = new Intent(act, PhotoCropActivity.class);
        i.putExtra("imagePath", imagePath);
        act.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_crop);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");
        Uri imageUri = Uri.fromFile(new File(imagePath));

        if(savedInstanceState == null){
            PhotoCropFragment cropFragment = PhotoCropFragment.newInstance(this);
            cropFragment.mSourceUri = imageUri;
            getSupportFragmentManager().beginTransaction().add(R.id.container, cropFragment).commit();
        }
        initToolbar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("PhotoCropActivity", "onDestroy");
    }

    @Override public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void initToolbar() {
        leftButton.setOnClickListener(this);
        rightButton.setVisibility(View.GONE);
        tvTitle.setText(R.string.take_photo);
        ivActionBarShadow.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
        }
    }

    public void startResultActivity(Uri uri) {
        if (isFinishing()) return;
        // Start ResultActivity
        String imagePath = getRealPathFromURI(this, uri);
        Intent data = new Intent();
        data.putExtra("imagePath", imagePath);
        setResult(RESULT_OK, data);
        finish();
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e("PhotoCropActivity", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
