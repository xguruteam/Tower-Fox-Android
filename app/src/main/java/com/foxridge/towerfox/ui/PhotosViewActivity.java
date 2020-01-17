package com.foxridge.towerfox.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.foxridge.towerfox.App;
import com.foxridge.towerfox.R;
import com.foxridge.towerfox.model.NavigationStack;
import com.foxridge.towerfox.model.Photos;
import com.foxridge.towerfox.utils.Globals;
import com.foxridge.towerfox.utils.Helper;
import com.foxridge.towerfox.viewmodels.SyncViewModel;
import com.foxridge.towerfox.views.CustomFontTextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class PhotosViewActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CAMERA_PERMISSION = 105;

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

    @BindView(R.id.btn_delete_action)
    LinearLayout deleteButton;
    @BindView(R.id.iv_top_action)
    ImageView ivTop;
    @BindView(R.id.tv_top_action)
    CustomFontTextView tvTop;

    @BindView(R.id.btn_rejection_detail)
    RelativeLayout btnRejectionDetail;
    @BindView(R.id.btn_take_photo)
    RelativeLayout btnTakePhoto;
    @BindView(R.id.tv_category_root)
    CustomFontTextView tvCategoryRoot;
    @BindView(R.id.tv_item_name)
    CustomFontTextView tvItemName;
    @BindView(R.id.tv_example_photo)
    CustomFontTextView tvExamplePhoto;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;



    SyncViewModel syncViewModel;
    NavigationStack navigationStack;
    private static final int REQUEST_CAMERA = 103;
    private final static int REQUEST_TAKE_PHOTO = 105;

    String currentImagePath = null;

    @Override
    public int addView() {
        return  R.layout.activity_photo;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncViewModel = ViewModelProviders.of(this).get(SyncViewModel.class);
        initView();
        setObservers();

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentImagePath != null) {
                    String title = Globals.getInstance().storage_loadString("ItemName");
                    PhotoZoomViewActivity.startActivity(PhotosViewActivity.this, currentImagePath, title);
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        syncViewModel.getDisplayItemCount();
    }

    public void initView() {
        leftButton.setOnClickListener(this);
        rightButton.setVisibility(View.GONE);
        tvTitle.setText(R.string.take_photo);
        btnTakePhoto.setOnClickListener(this);
        btnRejectionDetail.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case R.id.btn_take_photo:
                checkCameraPermission();
                break;
            case R.id.btn_rejection_detail:
                startActivity(new Intent(PhotosViewActivity.this, RejectionDetailActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                break;
            case R.id.btn_delete_action:
                deletePhoto();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setObservers() {
        syncViewModel.getPhotoDetail().observe(this, new Observer<List<Photos>>() {
            @Override
            public void onChanged(@Nullable List<Photos> photos) {
                if (photos.size() > 0 ) {
                    updatePhotoDetail(photos.get(0));
                }
            }
        });
        syncViewModel.getGetLocationMatrix().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    tvCategoryRoot.setText(s);
                }
            }
        });

    }

    public void updatePhotoDetail(Photos photodetail) {
        if (photodetail.getStatus() == 0) {
            Globals.getInstance().storage_saveObject("Description", photodetail.getDescription());
            btnRejectionDetail.setVisibility(View.VISIBLE);
        }else{
            btnRejectionDetail.setVisibility(View.GONE);
        }
        Globals.getInstance().storage_saveObject("ItemName", photodetail.getItemName());
        Globals.getInstance().storage_saveObject("Description", photodetail.getDescription());

        if (photodetail.getStatus() == 4 || photodetail.getStatus() == 2 || photodetail.getStatus() == 3) {
            tvExamplePhoto.setText(R.string.captured_image);
            if (photodetail.getCapturedImageName().equals("Sample.jpg")) {
                ivPhoto.setImageResource(R.drawable.ic_camera_gray);
                currentImagePath = null;
            }else{
                String imagePath = Globals.getInstance().IMAGE_LOCATION_PATH+"/CapturedPhotos/"+photodetail.getCapturedImageName();
                File imgFile = new  File(imagePath);
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ivPhoto.setImageBitmap(myBitmap);
                    currentImagePath = imgFile.toURI().toString();
                }
            }
        }else{
            tvExamplePhoto.setText(R.string.example_photo);
            String imagePath = Globals.getInstance().IMAGE_LOCATION_PATH+"/ReferencePhotos/"+photodetail.getReferenceImageName();
            File imgFile = new  File(imagePath);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivPhoto.setImageBitmap(myBitmap);
                currentImagePath = imgFile.toURI().toString();
            } else {
                ivPhoto.setImageResource(R.drawable.ic_camera_gray);
                currentImagePath = null;
            }
        }
        if (photodetail.getStatus() == 4) {
            btnTakePhoto.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        } else if (photodetail.getStatus() == 3 || (photodetail.getStatus() == 2)) {
            btnTakePhoto.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }else{
            btnTakePhoto.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
        }
        Globals.getInstance().storage_saveObject("Comments", photodetail.getComments() == null ? "" : photodetail.getComments());
        Globals.getInstance().storage_saveObject("ReviewerName", photodetail.getTakenBy());
        Globals.getInstance().storage_saveObject("ReviewDate", photodetail.getTakenDate());
        Globals.getInstance().storage_saveObject("TakenDate", photodetail.getTakenDate());
        Globals.getInstance().storage_saveObject("PhotoStatus", photodetail.getStatus());
//        if (Globals.getInstance().categoriesStack.size() > 0) {
//            String categoryRoot = "";
//            for (String categoryName: Globals.getInstance().categoriesStack) {
//                if (categoryRoot.equals("")) {
//                    categoryRoot = categoryName;
//                }else{
//                    categoryRoot = categoryRoot+" >> "+categoryName;
//                }
//            }
//            tvCategoryRoot.setText(categoryRoot);
//            tvCategoryRoot.setVisibility(View.VISIBLE);
//        }else {
//            tvCategoryRoot.setVisibility(View.GONE);
//        }
        tvItemName.setText(photodetail.getDescription());
        if (photodetail != null) {
            if (photodetail.getAdhocPhotoID() != null && photodetail.getParentCategoryID() != null) {
                syncViewModel.getCategoryLocation(photodetail.getAdhocPhotoID(), photodetail.getParentCategoryID());
            }
        }
    }

    public void deletePhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Attention!").setMessage("Are you sure you want to delete the photo?").setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String userName = Globals.getInstance().storage_loadString("UserName");
                String takenDate = Globals.getInstance().storage_loadString("TakenDate");
                String adhocPhotoID = Globals.getInstance().storage_loadString("AdhocPhotoID");
                App.getApp().getProjectsRepository().resetPhoto(userName, takenDate, adhocPhotoID);
                syncViewModel.getDisplayItemCount();
            }
        });
        builder.show();
    }


    public void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.need_files_permission);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(PhotosViewActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
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
            String album = Globals.getInstance().storage_loadString("ProjectName");
            if (album.length() > 0) {
                Globals.getInstance().storage_saveObject("AlbumName", album);
            }
            EasyImage.openChooserWithGallery(this, null, 0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        }else if (requestCode == REQUEST_TAKE_PHOTO ) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }else{
                this.onResume();
            }
        }else{
                EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                    @Override
                    public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                        //Some error handling
                        Helper.showErrorDialog(PhotosViewActivity.this, e.getLocalizedMessage());
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

                        Log.e("Camera", "file: " + imageFile.getAbsolutePath() + ", " + imageFile.length());
                        Globals.getInstance().storage_saveObject("photoPath", imageFile.getAbsolutePath());
                        Globals.getInstance().storage_saveObject("isAdhoc", false);
                        Intent intent = new Intent(PhotosViewActivity.this, PhotosDetailActivity.class);
                        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
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
                    String album = Globals.getInstance().storage_loadString("ProjectName");
                    if (album.length() > 0) {
                        Globals.getInstance().storage_saveObject("AlbumName", album);
                    }
                    EasyImage.openChooserWithGallery(this, null, 0);
                } else {
                    Toast.makeText(this, R.string.access_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


}
