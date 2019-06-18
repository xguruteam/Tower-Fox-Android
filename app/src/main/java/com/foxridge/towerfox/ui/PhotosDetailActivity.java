package com.foxridge.towerfox.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.foxridge.towerfox.App;
import com.foxridge.towerfox.R;
import com.foxridge.towerfox.model.EventPush;
import com.foxridge.towerfox.model.NavigationStack;
import com.foxridge.towerfox.model.Photos;
import com.foxridge.towerfox.utils.Globals;
import com.foxridge.towerfox.viewmodels.SyncViewModel;
import com.foxridge.towerfox.views.CustomFontTextView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;

public class PhotosDetailActivity extends BaseActivity implements View.OnClickListener {

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

    @BindView(R.id.btn_top)
    LinearLayout topButton;
    @BindView(R.id.iv_top_action)
    ImageView ivTop;
    @BindView(R.id.tv_top_action)
    CustomFontTextView tvTop;

    @BindView(R.id.tv_description)
    CustomFontTextView tvDescription;
    @BindView(R.id.btn_delete)
    RelativeLayout btnDelete;
    @BindView(R.id.btn_save_continue)
    RelativeLayout btnSaveAndContinue;
    @BindView(R.id.btn_save_return)
    RelativeLayout btnSaveAndReturn;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;

    String detailText = "";
    String imageNameStr = "";
    SyncViewModel syncViewModel;
    NavigationStack navigationStack;
    private static final int REQUEST_CAMERA = 103;
    private boolean isReturn = false;

    @Override
    public int addView() {
        return  R.layout.activity_photo_detail;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncViewModel = ViewModelProviders.of(this).get(SyncViewModel.class);
        initView();
        setObservers();
    }

    public void initView() {
        leftButton.setOnClickListener(this);
        rightButton.setVisibility(View.GONE);
        tvTitle.setText(R.string.review_photo);
        tvLeft.setText(R.string.cancel);
        btnDelete.setOnClickListener(this);
        btnSaveAndContinue.setOnClickListener(this);
        btnSaveAndReturn.setOnClickListener(this);

        updatePhotoDetail();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case R.id.btn_delete:
                delete();
                break;
            case R.id.btn_save_continue:
                saveAndContinue();
                break;
            case R.id.btn_save_return:
//                startActivity(new Intent(PhotosDetailActivity.this, RejectionDetailActivity.class));
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                saveAndReturn();
                break;
        }
    }

    public void saveAndContinue() {
        isReturn = false;
        Globals.getInstance().storage_saveObject("ImageName", imageNameStr);
        if (Globals.getInstance().storage_loadBool("isAdhoc")) {
            Photos photos = new Photos();
            photos.setProjectPhotoID("0");
            photos.setProjectID(Globals.getInstance().storage_loadString("ProjectID"));
            photos.setItemID("0");
            photos.setCategoryID(Globals.getInstance().storage_loadString("ParentID"));
            photos.setSectorID(Globals.getInstance().storage_loadString("SectorID"));
            photos.setPositionID(Globals.getInstance().storage_loadString("PositionID"));
            photos.setItemName(Globals.getInstance().storage_loadString("ItemName"));
            photos.setDescription(Globals.getInstance().storage_loadString("Description"));
            photos.setTakenBy(Globals.getInstance().storage_loadString("UserName"));
            photos.setTakenDate(Globals.getInstance().storage_loadString("TakenDate"));
            photos.setStatus(2);
            photos.setCapturedImageName(imageNameStr);
            photos.setQuantity(1);
            photos.setLatitude(String.valueOf(Globals.getInstance().storage_loadFloat("lat")));
            photos.setLongitude(String.valueOf(Globals.getInstance().storage_loadFloat("lng")));
            photos.setAdhoc(true);
            syncViewModel.insertAdhocPhotoDataDB(photos);
        }else{
            Photos photos = new Photos();
            photos.setSectorID(Globals.getInstance().storage_loadString("SectorID"));
            photos.setPositionID(Globals.getInstance().storage_loadString("PositionID"));
            photos.setTakenBy(Globals.getInstance().storage_loadString("UserName"));
            photos.setTakenDate(Globals.getInstance().storage_loadString("TakenDate"));
            photos.setStatus(1);
            photos.setCapturedImageName(imageNameStr);
            photos.setAdhocPhotoID(Globals.getInstance().storage_loadString("AdhocPhotoID"));
            photos.setQuantity(1);
            photos.setLatitude(String.valueOf(Globals.getInstance().storage_loadFloat("lat")));
            photos.setLongitude(String.valueOf(Globals.getInstance().storage_loadFloat("lng")));
            photos.setAdhoc(true);
            syncViewModel.updateCapturedPhoto(photos);
        }

        syncViewModel.getNextItemToDisplay();
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);

    }

    public void saveAndReturn() {
        isReturn = true;
        Globals.getInstance().storage_saveObject("ImageName", imageNameStr);
        if (Globals.getInstance().storage_loadBool("isAdhoc")) {
            Photos photos = new Photos();
            photos.setProjectPhotoID("0");
            photos.setProjectID(Globals.getInstance().storage_loadString("ProjectID"));
            photos.setItemID("0");
            photos.setCategoryID(Globals.getInstance().storage_loadString("ParentID"));
            photos.setSectorID(Globals.getInstance().storage_loadString("SectorID"));
            photos.setPositionID(Globals.getInstance().storage_loadString("PositionID"));
            photos.setItemName(Globals.getInstance().storage_loadString("ItemName"));
            photos.setDescription(Globals.getInstance().storage_loadString("Description"));
            photos.setTakenBy(Globals.getInstance().storage_loadString("UserName"));
            photos.setTakenDate(Globals.getInstance().storage_loadString("TakenDate"));
            photos.setStatus(2);
            photos.setCapturedImageName(imageNameStr);
            photos.setQuantity(1);
            photos.setLatitude(String.valueOf(Globals.getInstance().storage_loadFloat("lat")));
            photos.setLongitude(String.valueOf(Globals.getInstance().storage_loadFloat("lng")));
            photos.setAdhoc(true);
            syncViewModel.insertAdhocPhotoDataDB(photos);
        }else{
            Photos photos = new Photos();
            photos.setSectorID(Globals.getInstance().storage_loadString("SectorID"));
            photos.setPositionID(Globals.getInstance().storage_loadString("PositionID"));
            photos.setTakenBy(Globals.getInstance().storage_loadString("UserName"));
            photos.setTakenDate(Globals.getInstance().storage_loadString("TakenDate"));
            photos.setStatus(1);
            photos.setCapturedImageName(imageNameStr);
            photos.setQuantity(1);
            photos.setLatitude(String.valueOf(Globals.getInstance().storage_loadFloat("lat")));
            photos.setLongitude(String.valueOf(Globals.getInstance().storage_loadFloat("lng")));
            photos.setAdhocPhotoID(Globals.getInstance().storage_loadString("AdhocPhotoID"));
            photos.setAdhoc(true);
            syncViewModel.updateCapturedPhoto(photos);

            String path = Globals.getInstance().storage_loadString("GalleryPath");
            int index = path.lastIndexOf("/");
            path = path.substring(0, index);
            Globals.getInstance().storage_saveObject("GalleryPath", path);
            Log.e("gallery path", path);
        }
        try {
            Thread.sleep(100);
            EventBus.getDefault().post(new EventPush("refresh", "category"));
            EventBus.getDefault().post(new EventPush("uploadimage", "category"));
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        } catch (InterruptedException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new EventPush("refresh", "category"));
            EventBus.getDefault().post(new EventPush("uploadimage", "category"));
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        }
    }

    public void delete() {
        if (Globals.getInstance().storage_loadInteger("ProjectStatus") == 1) {
            String userName = Globals.getInstance().storage_loadString("UserName");
            String takenDate = Globals.getInstance().storage_loadString("TakenDate");
            String adhocPhotoID = Globals.getInstance().storage_loadString("AdhocPhotoID");
            App.getApp().getProjectsRepository().resetPhoto(userName, takenDate, adhocPhotoID);
        }
        String filepath = Globals.getInstance().storage_loadString("photoPath");
        File file1 = new File(filepath);
        try {
            if (file1.exists()) {
                boolean isdeleted = file1.createNewFile();
            }
        } catch (Exception e) {
            Log.e("deleteFile", e.getLocalizedMessage());
        }
        String dirstr = Globals.getInstance().IMAGE_LOCATION_PATH + "/CapturedPhotos/";
        File dir = new File(dirstr);
        File file = new File(dir, imageNameStr);
        try {
            if (file.exists()) {
                boolean isdeleted = file.createNewFile();
            }
        } catch (Exception e) {
            Log.e("deleteFile", e.getLocalizedMessage());
        }

        String filePath = tempGalleryPath();
        File galleryFile = new File(filePath);
        if (galleryFile.delete() == false ) {
            Crashlytics.getInstance().crash();
        }

        onBackPressed();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }


    public void setObservers() {
        syncViewModel.getGetNextItem().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null) {
                    EventBus.getDefault().post(new EventPush("refresh", "category"));
                    if (isReturn) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                    }else{
                        onBackPressed();
                    }
                }
            }
        });
    }

    public void updatePhotoDetail() {
        SimpleDateFormat fulldateformat = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss Z(z)");
        Date date = new Date();
        String fulldateString = fulldateformat.format(date);
        if (Globals.getInstance().storage_loadFloat("lat") != 0 && Globals.getInstance().storage_loadFloat("lng") != 0) {
            detailText = String.format("Lat: %f,Long: %f,%s", Globals.getInstance().storage_loadFloat("lat"), Globals.getInstance().storage_loadFloat("lng"), fulldateString);
        }else{
            detailText = String.format("Lat: 0,Long: 0,%s", fulldateString);
        }
        tvDescription.setText(Globals.getInstance().storage_loadString("Description"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy, hh:mm:ss a");
        Globals.getInstance().storage_saveObject("TakenDate", dateFormat.format(date));
        if (!Globals.getInstance().storage_loadBool("isRList")) {
            if (Globals.getInstance().storage_loadString("SectorID") != null) {
                if (Integer.valueOf(Globals.getInstance().storage_loadString("SectorID")) > 0) {
                    if (Globals.getInstance().storage_loadInteger("ItemsCount") > 1 && !Globals.getInstance().storage_loadBool("isAdhoc")) {
                        btnDelete.setVisibility(View.VISIBLE);
                    }else if (Globals.getInstance().storage_loadBool("isAdhoc")) {
                        btnDelete.setVisibility(View.GONE);
                        btnSaveAndContinue.setVisibility(View.GONE);
                    }else{
                        btnDelete.setVisibility(View.GONE);
                    }
                }else{
                    if (Globals.getInstance().storage_loadInteger("ItemsCount") > 1 && !Globals.getInstance().storage_loadBool("isAdhoc")) {
                        btnDelete.setVisibility(View.VISIBLE);
                    }else if (Globals.getInstance().storage_loadBool("isAdhoc")) {
                        btnDelete.setVisibility(View.GONE);
                        btnSaveAndContinue.setVisibility(View.GONE);
                    }else{
                        btnDelete.setVisibility(View.GONE);
                    }
                }
            }else{
                if (Globals.getInstance().storage_loadInteger("ItemsCount") > 1 && !Globals.getInstance().storage_loadBool("isAdhoc")) {
                    btnDelete.setVisibility(View.VISIBLE);
                }else if (Globals.getInstance().storage_loadBool("isAdhoc")) {
                    btnDelete.setVisibility(View.GONE);
                    btnSaveAndContinue.setVisibility(View.GONE);
                }else{
                    btnDelete.setVisibility(View.GONE);
                }
            }
        }else{
            btnSaveAndContinue.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }

        imageNameStr = String.format("%s_%s_%s_%s_%s_%s.jpg", Globals.getInstance().storage_loadString("ProjectID"), Globals.getInstance().storage_loadString("ParentID"), Globals.getInstance().storage_loadString("ItemID"),
                Globals.getInstance().storage_loadString("SectorID"), Globals.getInstance().storage_loadString("PositionID"), dateConversion());
        saveImage();
    }

    public String dateConversion() {
        String conversionDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-DD-YYYY-HH:mm:ss");
        conversionDate = dateFormat.format(new Date());
        conversionDate = conversionDate.replace("-", "_");
        conversionDate = conversionDate.replace("/", "_");
        conversionDate = conversionDate.replace(":", "_");
        conversionDate = conversionDate.replace(" ", "_");

        return conversionDate;
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
        }
    }

    public void saveImage() {
        String filepath = Globals.getInstance().storage_loadString("photoPath");
        Bitmap originalBitmap = loadBitmap(filepath);
        String dirstr = Globals.getInstance().IMAGE_LOCATION_PATH + "/CapturedPhotos/";
        File dir = new File(dirstr);
        if (!dir.exists()) {
            dir.mkdir();
        }

        String galleryFilePath = tempGalleryPath();
        Log.e("gallery path", "save image " + galleryFilePath);
        File galleryFile = new File(galleryFilePath);
        File dir2 = galleryFile.getParentFile();
        if (!dir2.exists()) {
            if (!dir2.mkdirs()) {
                Crashlytics.getInstance().crash();
            }
        }

        File noNameFile = new File(dir2, ".nomedia");
        if (noNameFile.exists()) {
            Crashlytics.getInstance().crash();
        }

        File file = new File(dir, imageNameStr);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            if (!galleryFile.exists()) {
                if (!galleryFile.createNewFile()) {
                    Crashlytics.getInstance().crash();
                }
            }


            FileOutputStream out = new FileOutputStream(file);
            FileOutputStream out_gallery = new FileOutputStream(galleryFile);
            Bitmap mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

            // NEWLY ADDED CODE STARTS HERE [
            Canvas canvas = new Canvas(mutableBitmap);
            float textHeight = originalBitmap.getWidth() / 30f;
            Paint paint = new Paint();
            paint.setColor(Color.WHITE); // Text Color
            paint.setTextSize(textHeight); // Text Size
            paint.setTextAlign(Paint.Align.RIGHT);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern
// radius=10, y-offset=2, color=black
            paint.setShadowLayer(textHeight / 15f, textHeight / 15f, textHeight / 15f, 0xFF000000);
            // some more settings...
            float baseline = -paint.ascent() * 1.2f; // ascent() is negative

            String[] str = detailText.split(",");
            for (int i = 0; i < str.length; i++){
                //Center text here
//                float textOffset = (canvas.getWidth()-paint.measureText((str[i])))/2;
//                canvas.drawText(str[i], textOffset, ((i+1)*baseline), paint);
                canvas.drawText(str[i], originalBitmap.getWidth() - textHeight * 0.5f, originalBitmap.getHeight() - textHeight * 0.5f - ((2 - i)*baseline), paint);
            }
            // NEWLY ADDED CODE ENDS HERE ]

            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            ivPhoto.setImageBitmap(mutableBitmap);

            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out_gallery);
            out_gallery.flush();
            out_gallery.close();


        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.getInstance().crash();
        }
    }

    private String tempGalleryPath() {
        String projectID = Globals.getInstance().storage_loadString("ProjectID");
        if (projectID.length() < 1) {
            Crashlytics.getInstance().crash();
        }
        String path = "/" + projectID + "/" + imageNameStr;
//        String path = Globals.getInstance().storage_loadString("GalleryPath");
//        if (Globals.getInstance().storage_loadBool("isAdhoc")) {
//            int index = path.indexOf("/", 1);
//            if (index > 1) {
//                path = path.substring(0, index);
//            }
//            path += "/" + imageNameStr;
//        }
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        ApplicationInfo applicationInfo = this.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        String appName = stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : this.getString(stringId);
        if (appName.length() < 1) {
            Crashlytics.getInstance().crash();
        }
        String filePath = storageDir.getAbsolutePath() + "/" + appName + path;
        return filePath;
    }

    private void copyFile(File inputFile, File outputFile) {
        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = outputFile.getParentFile();
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputFile);
            out = new FileOutputStream(inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public Bitmap loadBitmap(String url)
    {
        Bitmap bitmap = null;
        File f= new File(url);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            ExifInterface ei = new ExifInterface(url);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            bitmap = rotatedBitmap;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


}
