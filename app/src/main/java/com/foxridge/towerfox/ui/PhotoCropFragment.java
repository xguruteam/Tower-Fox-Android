package com.foxridge.towerfox.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.foxridge.towerfox.App;
import com.foxridge.towerfox.R;
import com.foxridge.towerfox.service.RestService;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.isseiaoki.simplecropview.util.Logger;
import com.isseiaoki.simplecropview.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions public class PhotoCropFragment extends Fragment {
  private static final String TAG = PhotoCropFragment.class.getSimpleName();

  private static final int REQUEST_PICK_IMAGE = 10011;
  private static final int REQUEST_SAF_PICK_IMAGE = 10012;
  private static final String PROGRESS_DIALOG = "ProgressDialog";
  private static final String KEY_FRAME_RECT = "FrameRect";
  private static final String KEY_SOURCE_URI = "SourceUri";

  // Views ///////////////////////////////////////////////////////////////////////////////////////
  private CropImageView mCropView;
  private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
  private RectF mFrameRect = null;
  public Uri mSourceUri = null;
  public Uri mTargetUri = null;
  private Context mContext;

  // Note: only the system can call this constructor by reflection.
  public PhotoCropFragment() {
    Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment init");
  }

  public static PhotoCropFragment newInstance(Context context) {
    PhotoCropFragment fragment = new PhotoCropFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    fragment.mContext = context;
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment onCreate");
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment onCreateView");
    return inflater.inflate(R.layout.fragment_photo_crop, null, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment onViewCreated with savedInstanceState:" + (savedInstanceState != null ? "non-null" : "null"));
    // bind Views
    bindViews(view);

    mCropView.setDebug(false);

    if (savedInstanceState != null) {
      // restore data
      mFrameRect = savedInstanceState.getParcelable(KEY_FRAME_RECT);
      mSourceUri = savedInstanceState.getParcelable(KEY_SOURCE_URI);
    }

    if (mSourceUri == null) {
      // default data
//      mSourceUri = getUriFromDrawableResId(getContext(), R.drawable.sample5);
      Log.e("aoki", "mSourceUri = "+mSourceUri);
    }
    // load image
    mCropView.load(mSourceUri)
        .initialFrameRect(mFrameRect)
        .useThumbnail(true)
        .execute(mLoadCallback);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment onSaveInstanceState");
    // save data
    outState.putParcelable(KEY_FRAME_RECT, mCropView.getActualCropRect());
    outState.putParcelable(KEY_SOURCE_URI, mCropView.getSourceUri());
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent result) {
    super.onActivityResult(requestCode, resultCode, result);
    Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment onActivityResult with result:" + resultCode + " request:" + requestCode);
    if (resultCode == Activity.RESULT_OK) {
      // reset frame rect
      mFrameRect = null;
      switch (requestCode) {
        case REQUEST_PICK_IMAGE:
          mSourceUri = result.getData();
          mCropView.load(mSourceUri)
              .initialFrameRect(mFrameRect)
              .useThumbnail(true)
              .execute(mLoadCallback);
          break;
        case REQUEST_SAF_PICK_IMAGE:
          mSourceUri = Utils.ensureUriPermission(getContext(), result);
          mCropView.load(mSourceUri)
              .initialFrameRect(mFrameRect)
              .useThumbnail(true)
              .execute(mLoadCallback);
          break;
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PhotoCropFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
  }

  // Bind views //////////////////////////////////////////////////////////////////////////////////

  private void bindViews(View view) {
    mCropView = (CropImageView) view.findViewById(R.id.cropImageView);
    view.findViewById(R.id.buttonDone).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonFitImage).setOnClickListener(btnListener);
    view.findViewById(R.id.button1_1).setOnClickListener(btnListener);
    view.findViewById(R.id.button3_4).setOnClickListener(btnListener);
    view.findViewById(R.id.button4_3).setOnClickListener(btnListener);
    view.findViewById(R.id.button9_16).setOnClickListener(btnListener);
    view.findViewById(R.id.button16_9).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonFree).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonPickImage).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonCustom).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonCircle).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonShowCircleButCropAsSquare).setOnClickListener(btnListener);
  }

  @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE) public void pickImage() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
          REQUEST_PICK_IMAGE);
    } else {
      Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      intent.setType("image/*");
      startActivityForResult(intent, REQUEST_SAF_PICK_IMAGE);
    }
  }

  @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) public void cropImage() {
    showProgress();
    mTargetUri = createSaveUri();
    Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment cropImage");
    if (mTargetUri == null) return;
    Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment createSaveUri:" + mTargetUri.toString());
    mCropView.crop(mSourceUri).execute(mCropCallback);
  }

  @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
  public void showRationaleForPick(PermissionRequest request) {
    showRationaleDialog(R.string.permission_pick_rationale, request);
  }

  @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
  public void showRationaleForCrop(PermissionRequest request) {
    showRationaleDialog(R.string.permission_crop_rationale, request);
  }

  public void showProgress() {
    ProgressDialogFragment f = ProgressDialogFragment.getInstance();
    getFragmentManager().beginTransaction().add(f, PROGRESS_DIALOG).commitAllowingStateLoss();
  }

  public void dismissProgress() {
    Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment dismissProgress");
    if (!isResumed()) return;
    android.support.v4.app.FragmentManager manager = getFragmentManager();
    if (manager == null) return;
    ProgressDialogFragment f = (ProgressDialogFragment) manager.findFragmentByTag(PROGRESS_DIALOG);
    if (f != null) {
      getFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
    }
  }

  public Uri createSaveUri() {
    Context context = App.getApp().getApplicationContext();
    Crashlytics.log(Log.DEBUG, "PhotoCropFragment.createSaveUri", "context=" + (context == null ? "null" : "non-null"));
    Uri uri = createNewUri(context, mCompressFormat);
    return uri;
  }

  public String getDirPath() {
    String dirPath = "";
    File imageDir = null;
    if (getContext() != null) {
      File extStorageDir = getContext().getExternalCacheDir();
      if (extStorageDir.canWrite()) {
        imageDir = new File(extStorageDir.getPath() + "/simplecropview");
      }
    } else {
      RestService.logOnServer("PhotoCrop Null Context.");
    }
    if (imageDir != null) {
      if (!imageDir.exists()) {
        imageDir.mkdirs();
      }
      if (imageDir.canWrite()) {
        dirPath = imageDir.getPath();
      }
    }
    return dirPath;
  }

  public static Uri getUriFromDrawableResId(Context context, int drawableResId) {
    StringBuilder builder = new StringBuilder().append(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .append("://")
        .append(context.getResources().getResourcePackageName(drawableResId))
        .append("/")
        .append(context.getResources().getResourceTypeName(drawableResId))
        .append("/")
        .append(context.getResources().getResourceEntryName(drawableResId));
    return Uri.parse(builder.toString());
  }

  public Uri createNewUri(Context context, Bitmap.CompressFormat format) {
    long currentTimeMillis = System.currentTimeMillis();
    Date today = new Date(currentTimeMillis);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String title = dateFormat.format(today);
    String dirPath = getDirPath();
    String fileName = "scv" + title + "." + getMimeType(format);
    String path = dirPath + "/" + fileName;
    File file = new File(path);
    ContentValues values = new ContentValues();
    values.put(MediaStore.Images.Media.TITLE, title);
    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format));
    values.put(MediaStore.Images.Media.DATA, path);
    long time = currentTimeMillis / 1000;
    values.put(MediaStore.MediaColumns.DATE_ADDED, time);
    values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
    if (file.exists()) {
      values.put(MediaStore.Images.Media.SIZE, file.length());
    }

    ContentResolver resolver = context.getContentResolver();
    Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    Logger.i("SaveUri = " + uri);
    return uri;
  }

  public static String getMimeType(Bitmap.CompressFormat format) {
    Logger.i("getMimeType CompressFormat = " + format);
    switch (format) {
      case JPEG:
        return "jpeg";
      case PNG:
        return "png";
    }
    return "png";
  }

  public static Uri createTempUri(Context context) {
    return Uri.fromFile(new File(context.getCacheDir(), "cropped"));
  }

  private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
    new AlertDialog.Builder(mContext).setPositiveButton(R.string.button_allow,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(@NonNull DialogInterface dialog, int which) {
            request.proceed();
          }
        }).setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(@NonNull DialogInterface dialog, int which) {
        request.cancel();
      }
    }).setCancelable(false).setMessage(messageResId).show();
  }

  // Handle button event /////////////////////////////////////////////////////////////////////////

  private final View.OnClickListener btnListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.buttonDone:
          v.setEnabled(false);
          Log.e(TAG, "crop done button clicked");
          Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment done button handler");
          PhotoCropFragmentPermissionsDispatcher.cropImageWithCheck(PhotoCropFragment.this);
          break;
        case R.id.buttonFitImage:
          mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
          break;
        case R.id.button1_1:
          mCropView.setCropMode(CropImageView.CropMode.SQUARE);
          break;
        case R.id.button3_4:
          mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
          break;
        case R.id.button4_3:
          mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
          break;
        case R.id.button9_16:
          mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
          break;
        case R.id.button16_9:
          mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
          break;
        case R.id.buttonCustom:
          mCropView.setCustomRatio(7, 5);
          break;
        case R.id.buttonFree:
          mCropView.setCropMode(CropImageView.CropMode.FREE);
          break;
        case R.id.buttonCircle:
          mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
          break;
        case R.id.buttonShowCircleButCropAsSquare:
          mCropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
          break;
        case R.id.buttonRotateLeft:
          mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
          break;
        case R.id.buttonRotateRight:
          mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
          break;
        case R.id.buttonPickImage:
          PhotoCropFragmentPermissionsDispatcher.pickImageWithCheck(PhotoCropFragment.this);
          break;
      }
    }
  };

  // Callbacks ///////////////////////////////////////////////////////////////////////////////////

  private final LoadCallback mLoadCallback = new LoadCallback() {
    @Override
    public void onSuccess() {
      Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment LoadCallback onSuccess");
    }

    @Override
    public void onError(Throwable e) {
      Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment LoadCallback onError");
    }
  };

  private final CropCallback mCropCallback = new CropCallback() {
    @Override
    public void onSuccess(Bitmap cropped) {
      Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment CropCallback onSuccess");
      Uri uri = null;
      if (mTargetUri == null) {
        uri = createSaveUri();
      } else {
        uri = mTargetUri;
      }

      mCropView.save(cropped)
          .compressFormat(mCompressFormat)
          .execute(uri, mSaveCallback);

      mTargetUri = null;
    }

    @Override
    public void onError(Throwable e) {
      Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment CropCallback onError");
    }
  };

  private final SaveCallback mSaveCallback = new SaveCallback() {
    @Override
    public void onSuccess(Uri outputUri) {
      Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment SaveCallback onSuccess");
      dismissProgress();
      if (getActivity() != null) {
          ((PhotoCropActivity) getActivity()).startResultActivity(outputUri);
      } else {
        RestService.logOnServer("PhotoCrop Null Activity.");
      }
    }

    @Override
    public void onError(Throwable e) {
      Crashlytics.log(Log.DEBUG, "CropImageView.saveAsync", "PhotoCropFragment SaveCallback onError");
      dismissProgress();
    }
  };
}