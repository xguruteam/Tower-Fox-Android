package com.foxridge.towerfox.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.adapter.RejectedAdapter;
import com.foxridge.towerfox.model.RejectDisplayModel;
import com.foxridge.towerfox.utils.Globals;
import com.foxridge.towerfox.viewmodels.SyncViewModel;
import com.foxridge.towerfox.views.CustomFontTextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

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

    @BindView(R.id.recycler_rejectes)
    RecyclerView rcRejects;
    @BindView(R.id.btn_projects)
    RelativeLayout btnProjects;
    @BindView(R.id.btn_rejects)
    RelativeLayout btnRejects;
    @BindView(R.id.iv_projects_list)
    ImageView ivProjects;
    @BindView(R.id.tv_projects_list)
    CustomFontTextView tvProjects;
    @BindView(R.id.iv_rejected_list)
    ImageView ivRejects;
    @BindView(R.id.tv_rejected_list)
    CustomFontTextView tvRejects;
    @BindView(R.id.view_reject)
    RelativeLayout viewReject;

    @BindView(R.id.view_empty_rejects)
    LinearLayout viewEmpty;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int PERMISSIONS_REQUEST_LOCATION = 99;

    public SyncViewModel syncViewModel;
    RejectedAdapter rejectedAdapter;
    List<RejectDisplayModel> rejectDisplayModelArrayList = new ArrayList<>();
    private static  final int REQUEST_ADD_PROJECT = 92;
    private static final int REQUEST_TAKEPHOTO = 103;
    private KProgressHUD loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncViewModel = ViewModelProviders.of(this).get(SyncViewModel.class);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setLabel("Sending Device Detail")
                .setDimAmount(0.5f);
        initView();
        if (checkLocationPermission()) {
            setLocations();
        }else{
            chekPermissions();
        }
        Globals.getInstance().storage_saveObject("SYNC" , "");
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        FragmentManager manager = getSupportFragmentManager();
                        if (manager != null) {
                            BaseFragment fragment = (BaseFragment) manager
                                    .findFragmentById(R.id.container_view);
                            if (fragment instanceof ProjectFragment) {
                                ((ProjectFragment) fragment).checkuploadData();
                            }
                        }
                    }
                });

        setObservers();
    }

    @Override
    public int addView() {
        return  R.layout.activity_main;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        if (savedInstanceState == null) {

            addFragment(ProjectFragment.getInstance(), false);
        }
        printKeyHash(this);
    }


    public void initView() {
        btnProjects.setOnClickListener(this);
        btnRejects.setOnClickListener(this);
        rightButton.setVisibility(View.GONE);
        leftButton.setVisibility(View.GONE);

        rejectedAdapter = new RejectedAdapter(this, new RejectedAdapter.OnClickCallback() {
            @Override
            public void onInfoClick(int position) {
                Globals.getInstance().storage_saveObject("ItemID", rejectedAdapter.getItem(position).getItemID());
                Globals.getInstance().storage_saveObject("AdhocPhotoID", rejectedAdapter.getItem(position).getAdhocPhotoID());
                Globals.getInstance().storage_saveObject("ParentID", rejectedAdapter.getItem(position).getCategoryID());
                Globals.getInstance().storage_saveObject("ProjectID", rejectedAdapter.getItem(position).getProjectID());
                Globals.getInstance().storage_saveObject("SectorID", rejectedAdapter.getItem(position).getSectorID());
                Globals.getInstance().storage_saveObject("PositionID", rejectedAdapter.getItem(position).getPositionID());
                Globals.getInstance().storage_saveObject("isRList", true);

                RejectDisplayModel model = rejectedAdapter.getItem(position);
                startActivity(new Intent(MainActivity.this, PhotosViewActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

        rcRejects.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcRejects.setAdapter(rejectedAdapter);
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("uuid", androidId);

    }
    @Override
    public void onResume() {
        super.onResume();
        FragmentManager manager = getSupportFragmentManager();
        if (manager != null) {
            BaseFragment fragment = (BaseFragment) manager
                    .findFragmentById(R.id.container_view);
            if (fragment instanceof ProjectFragment) {
                ((ProjectFragment) fragment).checkuploadData();
            }
        }

    }

    public void getRejects() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                syncViewModel.getRejects();
            }
        });
        thread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                Intent setting = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(setting);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case R.id.btn_right:
                Intent login = new Intent(MainActivity.this, AddProjectActivity.class);
                startActivityForResult(login, REQUEST_ADD_PROJECT);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                break;
            case R.id.btn_projects:
                selectTab(0);
                break;
            case R.id.btn_rejects:
                selectTab(1);
                break;
        }
    }

    public void selectTab(int index){
        if (index == 0) {
            ivProjects.setImageResource(R.drawable.ic_list_white_24px);
            tvProjects.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
            btnProjects.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            ivRejects.setImageResource(R.drawable.ic_list_blue);
            tvRejects.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            btnRejects.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
//            tvTitle.setText(R.string.string_projects);
            assert frameLayout != null;
            frameLayout.setVisibility(View.VISIBLE);
            viewReject.setVisibility(View.INVISIBLE);
            FragmentManager manager = getSupportFragmentManager();
            if (manager != null) {
                BaseFragment fragment = (BaseFragment) manager
                        .findFragmentById(R.id.container_view);
                if (fragment instanceof ProjectFragment) {
                    ((ProjectFragment) fragment).checkuploadData();
                }
            }
        }else{
            ivProjects.setImageResource(R.drawable.ic_list_blue);
            tvProjects.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            btnProjects.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
            ivRejects.setImageResource(R.drawable.ic_list_white_24px);
            tvRejects.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
            btnRejects.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            tvTitle.setText(R.string.rejected_list);
            assert frameLayout != null;
            frameLayout.setVisibility(View.INVISIBLE);
            viewReject.setVisibility(View.VISIBLE);
            getRejects();
        }
    }

    public void setObservers() {

        syncViewModel.getRejectDisplayModels().observe(this, new Observer<List<RejectDisplayModel>>() {
            @Override
            public void onChanged(@Nullable List<RejectDisplayModel> rejectDisplayModels) {
                if (rejectDisplayModels.size() > 0) {
                    viewEmpty.setVisibility(View.GONE);
                }else{
                    viewEmpty.setVisibility(View.VISIBLE);
                }
                rejectDisplayModelArrayList = rejectDisplayModels;
                List<RejectDisplayModel> realRejectList = new ArrayList<>();
                RejectDisplayModel prev = null;
                for (int i = 0; i < rejectDisplayModels.size(); i++) {
                    RejectDisplayModel rej = rejectDisplayModels.get(i);
                    if (prev != null) {
                        if (prev.getProjectID().equals(rej.getProjectID()) && prev.getCategoryName().equals(rej.getCategoryName())) {
                        }else{
                            String rejectTitle = String.format("%s >> %s", rej.getProjectID(), rej.getCategoryName());
                            RejectDisplayModel titleModel = new RejectDisplayModel();
                            titleModel.setStatus(101);
                            titleModel.setDescription(rejectTitle);
                            realRejectList.add(titleModel);
                        }
                        realRejectList.add(rej);
                    }else{
                        String rejectTitle = String.format("%s >> %s", rej.getProjectID(), rej.getCategoryName());
                        RejectDisplayModel titleModel = new RejectDisplayModel();
                        titleModel.setStatus(101);
                        titleModel.setDescription(rejectTitle);
                        realRejectList.add(titleModel);
                        realRejectList.add(rej);
                    }
                    prev = rej;
                }
                rejectedAdapter.setItems(realRejectList);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_PROJECT) {
            if (resultCode == RESULT_OK) {
            }
            this.onResume();
        }else if (requestCode == REQUEST_TAKEPHOTO) {
            if (resultCode == RESULT_OK) {
                this.onResume();
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        List<Fragment> fragments = manager.getFragments();
        Fragment fragment = fragments.get(fragments.size() - 1);
        if (fragment instanceof CategoryFragment) {
            Log.e("MainActivity", "fragment back");
            ((CategoryFragment)fragment).onBack();
        }
    }

    public void chekPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // permission denied, block the feature.
                Log.e("Permission denied : ", deniedPermissions.toString());
            }

        });
    }

    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //Request location updates:
                        setLocations();
                    }
                }
                break;
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void setLocations() {

        mFusedLocationClient.requestLocationUpdates(createLocationRequest(), mLocationCallback, null /* Looper */);
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                Globals.getInstance().storage_saveObject("lat", location.getLatitude());
                Globals.getInstance().storage_saveObject("lng", location.getLongitude());
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                break;
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            if (!locationAvailability.isLocationAvailable()) {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Globals.getInstance().storage_saveObject("lat", location.getLatitude());
                            Globals.getInstance().storage_saveObject("lng", location.getLongitude());
                        }
                    }
                });
            }
        }
    };

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }


}
