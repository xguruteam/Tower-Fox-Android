package com.foxridge.towerfox.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.adapter.CategoryAdapter;
import com.foxridge.towerfox.adapter.RejectedAdapter;
import com.foxridge.towerfox.model.CategoryDisplayModel;
import com.foxridge.towerfox.model.CategoryHeaderModel;
import com.foxridge.towerfox.model.EventPush;
import com.foxridge.towerfox.model.NavigationStack;
import com.foxridge.towerfox.model.PhotoRemainingModel;
import com.foxridge.towerfox.model.ProgressItem;
import com.foxridge.towerfox.model.RejectDisplayModel;
import com.foxridge.towerfox.utils.Globals;
import com.foxridge.towerfox.viewmodels.SyncViewModel;
import com.foxridge.towerfox.views.CustomFontTextView;
import com.foxridge.towerfox.views.CustomSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Hai Nguyen - 7/30/16.
 */
public class CategoryFragment extends BaseFragment {

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

	@BindView(R.id.nested_scrollView)
	NestedScrollView scrollView;

	SyncViewModel syncViewModel;
	CategoryAdapter categoryAdapter;
	NavigationStack navigationStack;
	private static final int REQUEST_CATEGORIRES = 102;
	private static final int REQUEST_TAKEPHOTO = 103;
	RejectedAdapter rejectedAdapter;
	List<RejectDisplayModel> rejectDisplayModelArrayList = new ArrayList<>();
	private String title = "";
	public static CategoryFragment getInstance() {

		return new CategoryFragment();
	}

	@Override
	protected int addView() {
		return R.layout.activity_categories;
	}

	@SuppressLint("DefaultLocale")
	@Override
	protected void initView() {
		super.initView();
		syncViewModel = ViewModelProviders.of(this).get(SyncViewModel.class);
		assert rightButton != null;
		rightButton.setVisibility(View.GONE);
		if (Globals.getInstance().navigationStack.size() > 1) {
			topButton.setVisibility(View.VISIBLE);
		}else{
			topButton.setVisibility(View.GONE);
		}
		assert tvTitle != null;
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

		categoryAdapter = new CategoryAdapter(mAct, new CategoryAdapter.OnClickCallback() {
			@Override
			public void onInfoClick(int position) {
				itemClicked(categoryAdapter.getItem(position));
			}

			@Override
			public void onCategoryClick(int position) {
				categoryClicked(categoryAdapter.getItem(position));
			}
		});
		rcCategory.setLayoutManager( new LinearLayoutManager(mAct, LinearLayoutManager.VERTICAL, false));
		rcCategory.setAdapter(categoryAdapter);
		setObservers();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				Globals.getInstance().navigationStack.remove(Globals.getInstance().navigationStack.size() - 1);
				if (Globals.getInstance().categoriesStack.size() > 0) {
					Globals.getInstance().categoriesStack.remove(Globals.getInstance().categoriesStack.size() - 1);

					String path = Globals.getInstance().storage_loadString("GalleryPath");
					int index = path.lastIndexOf("/");
					path = path.substring(0, index);
					Globals.getInstance().storage_saveObject("GalleryPath", path);
					Log.e("gallery path", path);

				}
				if (Globals.getInstance().navigationStack.size() > 0) {
					EventBus.getDefault().post(new EventPush("refresh", "category"));
				}else{
					EventBus.getDefault().post(new EventPush("upload", "project"));
				}
				mAct.backFragment(false);
				break;
			case R.id.btn_top:
				Globals.getInstance().categoriesStack.clear();
				NavigationStack navigationStack = Globals.getInstance().navigationStack.get(0);
				Globals.getInstance().navigationStack.clear();
				Globals.getInstance().navigationStack.add(navigationStack);
				mAct.backFragment("top");
				EventBus.getDefault().post(new EventPush("refresh", "category"));

				String path = Globals.getInstance().storage_loadString("GalleryPath");
				int index = path.indexOf("/", 1);
				path = path.substring(0, index);
				Globals.getInstance().storage_saveObject("GalleryPath", path);
				Log.e("gallery path", path);

				break;
			case R.id.btn_add_photo:
				Intent newintent = new Intent(mAct, TakeNewPhotoActivity.class);
				mAct.startActivity(newintent);
				mAct.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
				break;
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
		syncViewModel.getUploadError().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (syncViewModel != null) {
					Globals.getInstance().storage_saveObject("SYNC", "");
					syncViewModel.getPhotoRemainingCount();
                }
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

		String path = Globals.getInstance().storage_loadString("GalleryPath");
		path += "/" + model.getCategoryName();
		Globals.getInstance().storage_saveObject("GalleryPath", path);
		Log.e("gallery path", path);


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
			addFragment(CategoryFragment.getInstance(), true);
		}else{
			addFragment(CategoryFragment.getInstance(), "top");
		}


	}

	public void itemClicked(CategoryDisplayModel model) {
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
		Globals.getInstance().storage_saveObject("isRList", false);

		String path = Globals.getInstance().storage_loadString("GalleryPath");
		path += "/" + model.getItemName();
		Globals.getInstance().storage_saveObject("GalleryPath", path);
		Log.e("gallery path", path);

		Intent intent = new Intent(mAct, PhotosViewActivity.class);
		mAct.startActivityForResult(intent, REQUEST_TAKEPHOTO);
		mAct.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(EventPush event) {
		if (event.getMessage().equals("refresh")) {
			if (syncViewModel != null) {
				syncViewModel.getPhotoRemainingCount();
			}
		}
		if (event.getMessage().equals("uploadimage")) {
			if (syncViewModel != null) {
				Globals.getInstance().storage_saveObject("SYNC", "");
				syncViewModel.uploadDataToServer();
			}
		}
	}

}
