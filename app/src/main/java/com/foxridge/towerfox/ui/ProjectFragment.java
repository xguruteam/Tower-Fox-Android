package com.foxridge.towerfox.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.foxridge.towerfox.ui.CategoryFragment;
import com.foxridge.towerfox.R;
import com.foxridge.towerfox.adapter.ProjectAdapter;
import com.foxridge.towerfox.model.EventPush;
import com.foxridge.towerfox.model.NavigationStack;
import com.foxridge.towerfox.model.ProjectDisplayModel;
import com.foxridge.towerfox.utils.Globals;
import com.foxridge.towerfox.utils.Helper;
import com.foxridge.towerfox.viewmodels.ServerIPAddressViewModel;
import com.foxridge.towerfox.viewmodels.SyncViewModel;
import com.foxridge.towerfox.views.CustomFontTextView;
import com.capton.colorfulprogressbar.ColorfulProgressbar;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Hai Nguyen - 7/30/16.
 */
public class ProjectFragment extends BaseFragment {

	@BindView(R.id.recycler_projects)
	RecyclerView rcProjects;

	@BindView(R.id.progress_view)
	LinearLayout viewProgressView;

	@BindView(R.id.indeterminate_progress)
	ColorfulProgressbar progressBar;

    @BindView(R.id.tv_progress_detail)
    CustomFontTextView tvProgressDetails;

    @BindView(R.id.view_empty_projects)
    LinearLayout viewEmpty;


    private SyncViewModel syncViewModel;
	private ServerIPAddressViewModel serverIPAddressViewModel;
	ProjectAdapter projectAdapter;
	List<ProjectDisplayModel> projectDisplayModelArrayList = new ArrayList<>();
	private static  final int REQUEST_ADD_PROJECT = 92;
	public int selectedDeletePosition = -1;

	public static ProjectFragment getInstance() {

		return new ProjectFragment();
	}

	@Override
	protected int addView() {
		return R.layout.activity_project;
	}
	private KProgressHUD loader;

	@SuppressLint("DefaultLocale")
	@Override
	protected void initView() {
		super.initView();
		loader = KProgressHUD.create(mAct)
				.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
				.setCancellable(true)
				.setAnimationSpeed(2)
				.setLabel("Synchronizing...")
				.setDimAmount(0.5f);
		syncViewModel = ViewModelProviders.of(this).get(SyncViewModel.class);
		serverIPAddressViewModel = ViewModelProviders.of(this).get(ServerIPAddressViewModel.class);
		projectAdapter = new ProjectAdapter(new ProjectAdapter.OnClickCallback() {
			@Override
			public void onInfoClick(int position) {
				projectAdapter.notifyDataSetChanged();
				gotoCategory(position);
			}

			@Override
			public void onDeleteClick(int position) {
				projectAdapter.notifyDataSetChanged();
				if (Globals.getInstance().storage_loadString("SYNC").equals("")) {
					selectedDeletePosition = position;
					deleteProject(position);
				}else{
					Helper.showErrorDialog(mAct, "Synchronizing...");
				}
			}
		});
		progressBar.showPercentText(false);
		rcProjects.setLayoutManager( new LinearLayoutManager(mAct, LinearLayoutManager.VERTICAL, false));
		rcProjects.setAdapter(projectAdapter);
		setObservers();
		assert tvLeft != null;
		tvLeft.setVisibility(View.GONE);
		assert tvRight != null;
		tvRight.setVisibility(View.GONE);
		assert ivLeft != null;
		ivLeft.setImageResource(R.drawable.ic_tune_24px);
		assert ivRight != null;
		ivRight.setImageResource(R.drawable.ic_add_24px);
		assert tvTitle != null;
		tvTitle.setText(R.string.string_projects);
		checkServerConnectivity();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (projectAdapter != null) {
//			projectAdapter.saveStates(outState);
		}
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			if (projectAdapter != null) {
//				projectAdapter.restoreStates(savedInstanceState);
				projectAdapter.notifyDataSetChanged();
			}
		}
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
	public void onResume() {
		super.onResume();
		getProjectList();
	}

	public void deleteProject(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mAct).setTitle("Remove Project").setMessage("Are you sure you want to remove this project form your device?")
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						ProjectDisplayModel projectDisplayModel = projectDisplayModelArrayList.get(selectedDeletePosition);
						removeProject(projectDisplayModel.getProjectID(), projectDisplayModel.getCasperID());
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


	public void removeProject(String projectID, String caspID) {
		Globals.getInstance().storage_saveObject("DeleteProjectID", projectID);
		Globals.getInstance().storage_saveObject("DeleteCasperID", caspID);
		syncViewModel.deleteProject(mAct, projectID, caspID);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				projectAdapter.notifyDataSetChanged();
				if (!Globals.getInstance().storage_loadString("SYNC").equals("")){
					Helper.showErrorDialog(mAct, "Synchronizing. Please wait.");
				}else {
					Intent setting = new Intent(mAct, SettingsActivity.class);
					mAct.startActivity(setting);
					mAct.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
				}
				break;
			case R.id.btn_right:
				projectAdapter.notifyDataSetChanged();
				if (!Globals.getInstance().storage_loadString("SYNC").equals("")){
					Helper.showErrorDialog(mAct, "Synchronizing. Please wait.");
				}else{
					Intent intent = new Intent(mAct, AddProjectActivity.class);
					mAct.startActivityForResult(intent, REQUEST_ADD_PROJECT);
					mAct.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
				}
				break;
		}
	}
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser){
			//When fragment is visible
		}
		Log.e("my_fragment","setUserVisibleHint: "+isVisibleToUser);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		Log.e("my_fragment","setUserVisibleHint: "+true);
	}

	public void checkServerConnectivity() {
		Log.e("ProjectFragment", "checkServerConnectivity");
		if (Globals.getInstance().storage_loadString("SYNC").equals("")) {
			syncViewModel.checkServerConnectivtiyTest(Globals.getInstance().storage_loadString("SERVER_IP"));
		}
		getProjectList();
	}

	public void getProjectList() {
		if (syncViewModel != null) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					syncViewModel.getProjects();
				}
			});
			thread.start();
		}
	}

	public void setObservers() {
		syncViewModel.getProjectDisplayModels().observe(this, new Observer<List<ProjectDisplayModel>>() {
			@Override
			public void onChanged(@Nullable List<ProjectDisplayModel> projectDisplayModels) {
			    if (projectDisplayModels.size() > 0) {
                    viewEmpty.setVisibility(View.GONE);
                }else{
			        viewEmpty.setVisibility(View.VISIBLE);
                }
				projectDisplayModelArrayList = projectDisplayModels;
				projectAdapter.setItems(projectDisplayModelArrayList);
			}
		});

		syncViewModel.getDownloadCapturedPhotos().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(@Nullable Integer integer) {
				if (!Globals.getInstance().storage_loadString("SYNC").equals("")) {
					tvProgressDetails.setText("Downloading Captured Photos, Please wait "+integer+" of "+Globals.getInstance().capturedImageNamesList.size());
					if (integer != null) {
						int progress = (int) (integer.floatValue() * 100f / Globals.getInstance().capturedImageNamesList.size());
						progressBar.setProgress(progress);
					}
					viewProgressView.setVisibility(View.VISIBLE);
				}else{
					getProjectList();
					viewProgressView.setVisibility(View.GONE);
				}
			}
		});

		syncViewModel.getDownloadReferencePhotos().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(@Nullable Integer integer) {
				if (!Globals.getInstance().storage_loadString("SYNC").equals("")) {
					tvProgressDetails.setText("Downloading Reference Photos, Please wait "+integer+" of "+Globals.getInstance().referenceImageNamesList.size());
					if (integer != null) {
						int progress = (int) (integer.floatValue() * 100f / Globals.getInstance().referenceImageNamesList.size());
						progressBar.setProgress(progress);
					}
					viewProgressView.setVisibility(View.VISIBLE);
				}else{
					getProjectList();
					viewProgressView.setVisibility(View.GONE);
				}
			}
		});

		syncViewModel.getUploadCapturedPhotos().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(@Nullable Integer integer) {
				if (!Globals.getInstance().storage_loadString("SYNC").equals("")) {
					tvProgressDetails.setText("Uploading CapturedPhotos, Please wait "+integer+" of "+Globals.getInstance().uploadImageNamesList.size());
					if (integer != null) {
						int progress = (int) (integer.floatValue() * 100f / Globals.getInstance().uploadImageNamesList.size());
						progressBar.setProgress(progress);
					}
					viewProgressView.setVisibility(View.VISIBLE);
				}else{
					getProjectList();
					viewProgressView.setVisibility(View.GONE);
				}
			}
		});

		syncViewModel.getUploadError().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(@Nullable Boolean aBoolean) {
				if (aBoolean != null) {
					Globals.getInstance().storage_saveObject("SYNC", "");
					viewProgressView.setVisibility(View.GONE);
					getProjectList();
				}else {
					Globals.getInstance().storage_saveObject("SYNC", "");
					Helper.showErrorDialog(mAct, "Delete Project failed");
				}
			}
		});

		syncViewModel.getServerConnectivity().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(@Nullable Boolean aBoolean) {
				if (Globals.getInstance().storage_loadString("SYNC").equals("")) {
					syncViewModel.uploadDataToServer();
				}else{

				}
			}
		});
		syncViewModel.getJsonDeleteProject().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(@Nullable Boolean aBoolean) {
				if (aBoolean != null) {
//					projectDisplayModelArrayList.remove(selectedDeletePosition);
//					projectAdapter.setItems(projectDisplayModelArrayList);
					getProjectList();
				}
			}
		});
		syncViewModel.getShowProgress().observe(this, new Observer<Boolean>() {
			@Override
			public void onChanged(@Nullable Boolean aBoolean) {
				if (aBoolean != null) {
					if (aBoolean) {
						if (loader != null) {
								mAct.runOnUiThread(new Runnable() {
									public void run() {
										loader.show();
//										if (Globals.getInstance().storage_loadBool("isFirstTime")) {
//											loader.show();
//											try {
//												Thread.sleep(100);
//												loader.dismiss();
//												loader.show();
//											} catch (InterruptedException e) {
//												e.printStackTrace();
//											}
//											Globals.getInstance().storage_saveObject("isFirstTime", false);
//										}else{
//											loader.show();
//										}
									}
								});

//							}
						}else{
							loader = KProgressHUD.create(mAct)
									.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
									.setCancellable(true)
									.setAnimationSpeed(2)
									.setLabel("Synchronizing...")
									.setDimAmount(0.5f);
							mAct.runOnUiThread(new Runnable() {
								public void run() {
									loader.show();
								}
							});
						}
					}else{
						if (loader != null) {
							if (loader.isShowing()) {
								loader.dismiss();
							}
						}
					}
				}else{
					if (loader != null) {
						if (loader.isShowing()) {
							loader.dismiss();
						}
					}
				}
			}
		});
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(EventPush event) {
		if (event.getMessage().equals("upload")) {
			checkuploadData();
		}
	}

	public void checkuploadData() {
		Log.e("Project Fragment", "check uploaddataServer");
		if (syncViewModel != null) {
			syncViewModel.uploadDataToServer();
//				syncViewModel.checkServerConnectivtiyTest(Globals.getInstance().storage_loadString("SERVER_IP"));
		}
	}

	public void gotoCategory(int position) {
		ProjectDisplayModel projectDisplayModel = projectDisplayModelArrayList.get(position);
		if (!projectDisplayModel.getProjectID().equals("")){
			Globals.getInstance().storage_saveObject("ProjectID", projectDisplayModel.getProjectID());
		}
		if (!projectDisplayModel.getProjectName().equals("")){
			Globals.getInstance().storage_saveObject("ProjectName", projectDisplayModel.getProjectName());
		}
		if (!projectDisplayModel.getPaceID().equals("")){
			Globals.getInstance().storage_saveObject("PaceID", projectDisplayModel.getPaceID());
		}
		if (!projectDisplayModel.getCasperID().equals("")){
			Globals.getInstance().storage_saveObject("CasprID", projectDisplayModel.getCasperID());
		}
		if (projectDisplayModel.getRequired() != null){
			Globals.getInstance().storage_saveObject("ProjectRequired", projectDisplayModel.getRequired());
		}
		if (projectDisplayModel.getTaken() != null){
			Globals.getInstance().storage_saveObject("ProjectTaken", projectDisplayModel.getTaken());
		}
		if (projectDisplayModel.getApproved() != null){
			Globals.getInstance().storage_saveObject("ProjectApproved", projectDisplayModel.getApproved());
		}
		if (projectDisplayModel.getRejected() != null){
			Globals.getInstance().storage_saveObject("ProjectRejected", projectDisplayModel.getRejected());
		}

		Globals.getInstance().storage_saveObject("ProjectRequiredCount", projectDisplayModel.getRequiredCount());
		Globals.getInstance().storage_saveObject("ProjectTakenCount", projectDisplayModel.getTakenCount());
		Globals.getInstance().storage_saveObject("ProjectRejectedCount", projectDisplayModel.getRejectedCount());
		Globals.getInstance().storage_saveObject("ProjectOutOfScopeCount", projectDisplayModel.getOutOfScopeCount());
		Globals.getInstance().storage_saveObject("ParentID", "0");
		Globals.getInstance().storage_saveObject("SectorID", "0");
		Globals.getInstance().storage_saveObject("PositionID", "0");
		Globals.getInstance().storage_saveObject("RequiredSectorPosition", false);

		NavigationStack navigationStack = new NavigationStack();
		navigationStack.setParentID("0");
		navigationStack.setSectorID("0");
		navigationStack.setPositionID("0");
		navigationStack.setRequireSectorPosition(false);
		navigationStack.setProjectID(projectDisplayModel.getProjectID());
		navigationStack.setProjectName(projectDisplayModel.getProjectName());
		navigationStack.setCategoryName("Photos");

		Globals.getInstance().navigationStack.add(navigationStack);
		Globals.getInstance().categoriesStack.clear();
		Globals.getInstance().categoriesListNameArray.clear();
		Globals.getInstance().categoriesListStringArray.clear();

		addFragment(CategoryFragment.getInstance(), true);
//		Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
//		startActivity(intent);
//		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

	}


}
