package com.foxridge.towerfox.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.adapter.CategoryAdapter;
import com.foxridge.towerfox.adapter.RejectedAdapter;
import com.foxridge.towerfox.model.CategoryDisplayModel;
import com.foxridge.towerfox.model.CategoryHeaderModel;
import com.foxridge.towerfox.model.NavigationStack;
import com.foxridge.towerfox.model.PhotoRemainingModel;
import com.foxridge.towerfox.model.ProgressItem;
import com.foxridge.towerfox.model.RejectDisplayModel;
import com.foxridge.towerfox.utils.Globals;
import com.foxridge.towerfox.viewmodels.SyncViewModel;
import com.foxridge.towerfox.views.CustomFontTextView;
import com.foxridge.towerfox.views.CustomSeekBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesActivity extends AppCompatActivity implements View.OnClickListener {

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

    @BindView(R.id.recycler_categories)
    RecyclerView rcCategory;

    @BindView(R.id.progress_view)
    CustomSeekBar progressBar;
    @BindView(R.id.tv_remaining_photos)
    CustomFontTextView tvRemainingPhotos;
    @BindView(R.id.tv_taken)
    CustomFontTextView tvTaken;
    @BindView(R.id.tv_rejected)
    CustomFontTextView tvRejected;
    @BindView(R.id.tv_out_of_scope)
    CustomFontTextView tvOutOfScope;
    @BindView(R.id.tv_project_id)
    CustomFontTextView tvProjectID;
    @BindView(R.id.tv_project_name)
    CustomFontTextView tvProjectName;
    @BindView(R.id.tv_casper_id)
    CustomFontTextView tvCasperID;
    @BindView(R.id.btn_add_photo)
    RelativeLayout btnAddPhoto;
    @BindView(R.id.tv_category_root)
    CustomFontTextView tvCategoryRoot;

    @BindView(R.id.recycler_rejectes)
    RecyclerView rcRejects;
    @BindView(R.id.nested_scrollView)
    NestedScrollView scrollView;
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

    SyncViewModel syncViewModel;
    CategoryAdapter categoryAdapter;
    NavigationStack navigationStack;
    private static final int REQUEST_CATEGORIRES = 102;
    private static final int REQUEST_TAKEPHOTO = 103;
    private static final int REQUEST_CAMERA = 104;
    RejectedAdapter rejectedAdapter;
    List<RejectDisplayModel> rejectDisplayModelArrayList = new ArrayList<>();
    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        ButterKnife.bind(this);
        syncViewModel = ViewModelProviders.of(this).get(SyncViewModel.class);
        initView();
        setObservers();
    }

    public void initView() {
        leftButton.setOnClickListener(this);
        rightButton.setVisibility(View.GONE);
        topButton.setOnClickListener(this);
        if (Globals.getInstance().navigationStack.size() > 1) {
            topButton.setVisibility(View.VISIBLE);
        }else{
            topButton.setVisibility(View.GONE);
        }
        tvTitle.setText(R.string.photos);
        btnAddPhoto.setOnClickListener(this);

        navigationStack = Globals.getInstance().navigationStack.get(Globals.getInstance().navigationStack.size() - 1);
        tvProjectID.setText(Globals.getInstance().storage_loadString("ProjectID"));
        tvProjectName.setText(Globals.getInstance().storage_loadString("ProjectName"));
        tvCasperID.setText(Globals.getInstance().storage_loadString("PaceID"));
        float takenPercent = Globals.getInstance().storage_loadFloat("ProjectTaken");
        float approvedPercent = Globals.getInstance().storage_loadFloat("ProjectApproved");
        float rejectedPercent = Globals.getInstance().storage_loadFloat("ProjectRejected");

        ArrayList<ProgressItem> mProgressItemsList = new ArrayList<>();
        ProgressItem mProgressItem = new ProgressItem();
        // blue span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = takenPercent;
        mProgressItem.color = R.color.colorBlue1;
        mProgressItemsList.add(mProgressItem);
        // green span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = approvedPercent;
        mProgressItem.color = R.color.colorGreen;
        mProgressItemsList.add(mProgressItem);
        // red span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = rejectedPercent;
        Log.i("Mainactivity", mProgressItem.progressItemPercentage + "");
        mProgressItem.color = R.color.colorRed;
        mProgressItemsList.add(mProgressItem);
        // gray span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = 100f - takenPercent - approvedPercent - rejectedPercent;
        Log.i("Mainactivity", mProgressItem.progressItemPercentage + "");
        mProgressItem.color = R.color.transparent;
        mProgressItemsList.add(mProgressItem);
        progressBar.initData(mProgressItemsList);
        progressBar.invalidate();

        if (Globals.getInstance().categoriesStack.size() > 0) {
            String categoryRoot = "";
            for (String categoryName: Globals.getInstance().categoriesStack) {
                if (categoryRoot.equals("")) {
                    categoryRoot = categoryName;
                }else{
                    categoryRoot = categoryRoot+" >> "+categoryName;
                }
            }
            tvCategoryRoot.setText(categoryRoot);
            tvCategoryRoot.setVisibility(View.VISIBLE);
            title = Globals.getInstance().categoriesStack.get(Globals.getInstance().categoriesStack.size() - 1);
            tvTitle.setText(Globals.getInstance().categoriesStack.get(Globals.getInstance().categoriesStack.size() - 1));
        }else {
            tvCategoryRoot.setVisibility(View.GONE);
            tvTitle.setText("Photos");
            title = "Photos";
        }

        categoryAdapter = new CategoryAdapter(this, new CategoryAdapter.OnClickCallback() {
            @Override
            public void onInfoClick(int position) {
                itemClicked(categoryAdapter.getItem(position));
            }

            @Override
            public void onCategoryClick(int position) {
                categoryClicked(categoryAdapter.getItem(position));
            }
        });
        rcCategory.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcCategory.setAdapter(categoryAdapter);
        btnProjects.setOnClickListener(this);
        btnRejects.setOnClickListener(this);
        rejectedAdapter = new RejectedAdapter(this, new RejectedAdapter.OnClickCallback() {
            @Override
            public void onInfoClick(int position) {
                Globals.getInstance().storage_saveObject("ItemID", rejectedAdapter.getItem(position).getItemID());
                Globals.getInstance().storage_saveObject("AdhocPhotoID", rejectedAdapter.getItem(position).getAdhocPhotoID());
                Globals.getInstance().storage_saveObject("ParentID", rejectedAdapter.getItem(position).getCategoryID());
                startActivity(new Intent(CategoriesActivity.this, PhotosViewActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
        rcRejects.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcRejects.setAdapter(rejectedAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                Globals.getInstance().navigationStack.remove(Globals.getInstance().navigationStack.size() - 1);
                if (Globals.getInstance().categoriesStack.size() > 0) {
                    Globals.getInstance().categoriesStack.remove(Globals.getInstance().categoriesStack.size() - 1);
                }
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case R.id.btn_top:
                Globals.getInstance().categoriesStack.clear();
                NavigationStack navigationStack = Globals.getInstance().navigationStack.get(0);
                Globals.getInstance().navigationStack.clear();
                Globals.getInstance().navigationStack.add(navigationStack);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case R.id.btn_add_photo:
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
            tvTitle.setText(title);
            scrollView.setVisibility(View.VISIBLE);
            rcRejects.setVisibility(View.INVISIBLE);
            leftButton.setVisibility(View.VISIBLE);
            if (Globals.getInstance().navigationStack.size() > 1) {
                topButton.setVisibility(View.VISIBLE);
            }else{
                topButton.setVisibility(View.GONE);
            }

        }else{
            ivProjects.setImageResource(R.drawable.ic_list_blue);
            tvProjects.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            btnProjects.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
            ivRejects.setImageResource(R.drawable.ic_list_white_24px);
            tvRejects.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
            btnRejects.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            tvTitle.setText(R.string.rejected_list);
            scrollView.setVisibility(View.INVISIBLE);
            rcRejects.setVisibility(View.VISIBLE);
            leftButton.setVisibility(View.GONE);
            topButton.setVisibility(View.GONE);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    syncViewModel.getRejects();
                }
            });
            thread.start();
        }
    }


    public void setObservers() {
        syncViewModel.getPhotoRemainingCount();
        syncViewModel.getPhotoRemainingModels().observe(this, new Observer<List<PhotoRemainingModel>>() {
            @Override
            public void onChanged(@Nullable List<PhotoRemainingModel> photoRemainingModels) {
                syncViewModel.getCategoryHeaderCount();
                if (photoRemainingModels.size() > 0) {
                    int remaining = photoRemainingModels.get(0).getRejectedCount() + photoRemainingModels.get(0).getRequiredCount();
                    tvRemainingPhotos.setText("PHOTOS REMAINING: "+remaining);
                    tvTaken.setText(""+photoRemainingModels.get(0).getRequiredCount());
                    tvRejected.setText(""+photoRemainingModels.get(0).getRejectedCount());
                    tvOutOfScope.setText(""+photoRemainingModels.get(0).getOutOfScopeCount());
                }
                Log.e("Photos", "onChanged: "+photoRemainingModels.size() );
            }
        });

        syncViewModel.getCategoryDisplayHeaderModels().observe(this, new Observer<List<CategoryHeaderModel>>() {
            @Override
            public void onChanged(@Nullable List<CategoryHeaderModel> categoryHeaderModels) {
                syncViewModel.getCategoryDisplay();
                if (categoryHeaderModels.size() > 0) {

                }
                Log.e("Headers", "onChanged: "+categoryHeaderModels.size() );
            }
        });

        syncViewModel.getCategoryDisplayModels().observe(this, new Observer<List<CategoryDisplayModel>>() {
            @Override
            public void onChanged(@Nullable List<CategoryDisplayModel> categoryDisplayModels) {
                Log.e("Categories", "onChanged: "+categoryDisplayModels.size() );
                categoryAdapter.setItems(categoryDisplayModels);
            }
        });

        syncViewModel.getRejectDisplayModels().observe(this, new Observer<List<RejectDisplayModel>>() {
            @Override
            public void onChanged(@Nullable List<RejectDisplayModel> rejectDisplayModels) {
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

    public void categoryClicked(CategoryDisplayModel model) {
        Globals.getInstance().storage_saveObject("Required", model.get_required());
        Globals.getInstance().storage_saveObject("Taken", model.get_taken());
        Globals.getInstance().storage_saveObject("Approved", model.get_approved());
        Globals.getInstance().storage_saveObject("Rejected", model.get_rejected());
        Globals.getInstance().storage_saveObject("TakenPercent", model.get_takenPercent());
        Globals.getInstance().storage_saveObject("ApprovedPercent", model.get_approvedPercent());
        Globals.getInstance().storage_saveObject("RejectedPercent", model.get_rejectedPercent());
        if (model.getProjectID() != null) {
            if (!model.getProjectID().equals("") && !model.getProjectID().equals("undefined") && !model.getProjectID().equals("null")) {
                Globals.getInstance().storage_saveObject("ProjectID", model.getProjectID());
            }
        }
        if (model.getPCategoryID() != null) {
            if (!model.getPCategoryID().equals("") && !model.getPCategoryID().equals("undefined") && !model.getPCategoryID().equals("null")) {
                Globals.getInstance().storage_saveObject("ParentID", model.getPCategoryID());
            }
        }
        if (model.getType() != null) {
            if (model.getType().equals("Category") && model.getType().equals("Sector")) {
                Globals.getInstance().storage_saveObject("SectorID", model.getISectorID());
            }
        }
        Globals.getInstance().storage_saveObject("RequiredSectorPosition", model.isRequireSectorPosition());
        Globals.getInstance().storage_saveObject("PositionID", model.getIPositionID());

        NavigationStack navigationStack = new NavigationStack();
        navigationStack.setParentID(Globals.getInstance().storage_loadString("ParentID"));
        navigationStack.setSectorID(Globals.getInstance().storage_loadString("SectorID"));
        navigationStack.setPositionID(Globals.getInstance().storage_loadString("PositionID"));
        navigationStack.setRequireSectorPosition(Globals.getInstance().storage_loadBool("RequiredSectorPosition"));
        navigationStack.setProjectID(Globals.getInstance().storage_loadString("ProjectID"));
        navigationStack.setProjectName(Globals.getInstance().storage_loadString("ProjectName"));
        navigationStack.setCategoryName(Globals.getInstance().storage_loadString("CategoryName"));
        navigationStack.setType(model.getType());
        navigationStack.setItemID("");
        navigationStack.setRequired(model.get_required());
        navigationStack.setTaken(model.get_taken());
        navigationStack.setApproved(model.get_approved());
        navigationStack.setRejected(model.get_rejected());
        navigationStack.setApprovedPercent(model.get_approvedPercent());
        navigationStack.setRejectedPercent(model.get_rejectedPercent());
        navigationStack.setTakenPercent(model.get_takenPercent());
        Globals.getInstance().navigationStack.add(navigationStack);
        Globals.getInstance().categoriesStack.add(model.getCategoryName());

        if (Globals.getInstance().categoriesStack.size() > 1) {
            Intent intent = new Intent(CategoriesActivity.this, CategoriesActivity.class);
            startActivityForResult(intent, REQUEST_CATEGORIRES);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }else{
            Intent intent = new Intent(CategoriesActivity.this, CategoriesActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }


    }

    public void itemClicked(CategoryDisplayModel model) {
        if (model == null) return;

        if (model.getIItemID() != null) {
            if (!model.getIItemID().equals("") && !model.getIItemID().equals("undefined")) {
                Globals.getInstance().storage_saveObject("ItemID", model.getIItemID());
            }
        }
        if (model.getAdhocPhotoID() != null) {
            if (!model.getAdhocPhotoID().equals("") && !model.getAdhocPhotoID().equals("undefined")) {
                Globals.getInstance().storage_saveObject("AdhocPhotoID", model.getAdhocPhotoID());
            }
        }
        if (model.getPCategoryID() != null) {
            if (!model.getPCategoryID().equals("") && !model.getPCategoryID().equals("undefined") && !model.getPCategoryID().equals("null")) {
                Globals.getInstance().storage_saveObject("ParentID", model.getPCategoryID());
            }
        }

        Intent intent = new Intent(CategoriesActivity.this, PhotosViewActivity.class);
        startActivityForResult(intent, REQUEST_TAKEPHOTO);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CATEGORIRES) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        }
    }


}
