package com.foxridge.towerfox.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.model.ProgressItem;
import com.foxridge.towerfox.model.ProjectDisplayModel;
import com.foxridge.towerfox.views.CustomFontTextView;
import com.foxridge.towerfox.views.CustomSeekBar;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProjectDisplayModel> items = new ArrayList<>();
    private OnClickCallback onClickCallback;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    public ProjectAdapter(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }
//    public void saveStates(Bundle outState) {
//        viewBinderHelper.saveStates(outState);
//    }
//
//    public void restoreStates(Bundle inState) {
//        viewBinderHelper.restoreStates(inState);
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
            final ViewItemHolder holder = new ViewItemHolder(view);
            return holder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
            return new ViewHeaderHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == ITEM_VIEW_TYPE_ITEM) {
            ViewItemHolder viewHolder = (ViewItemHolder) holder;
//            viewBinderHelper.bind(viewHolder.swipeRevealLayout, items.get(position).getProjectID());
            viewHolder.btnRemoveProject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCallback.onDeleteClick(position);
                    notifyDataSetChanged();
                }
            });
            viewHolder.swipeRevealLayout.close(true);
            viewHolder.rlMainItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCallback.onInfoClick(position);
                }
            });
            viewHolder.setData(items.get(position));
        } else {
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_VIEW_TYPE_ITEM;//items.get(position).getBud().getType() == 0 ? ITEM_VIEW_TYPE_ITEM : ITEM_VIEW_TYPE_HEADER;
    }

    public void setItems(List<ProjectDisplayModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    public static class ViewItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_progress)
        CustomFontTextView tvProgress;
        @BindView(R.id.tv_project_id)
        CustomFontTextView tvProjectID;
        @BindView(R.id.tv_project_name)
        CustomFontTextView tvProjectName;
        @BindView(R.id.tv_casper_id)
        CustomFontTextView tvCasperID;
        @BindView(R.id.progress_view)
        CustomSeekBar progressBar;
        @BindView(R.id.swipe_layout)
        SwipeRevealLayout swipeRevealLayout;
        @BindView(R.id.btn_remove_project)
        RelativeLayout btnRemoveProject;

        @BindView(R.id.main_item)
        RelativeLayout rlMainItem;

        public ViewItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(ProjectDisplayModel s) {
            tvCasperID.setText(s.getCasperID());
            tvProjectID.setText(s.getProjectID());
            tvProjectName.setText(s.getProjectName());
            if (s.getApproved() != null) {
                tvProgress.setText(String.format("%.2f%% Completed / Remaining: %d", s.getApproved(), s.getRequiredCount() + s.getRejectedCount()));
            }else{
                tvProgress.setText(String.format("0%% Completed / Remaining: %d", s.getRequiredCount() + s.getRejectedCount()));
            }
            if (s.getTaken() != null) {
                ArrayList<ProgressItem> mProgressItemsList = new ArrayList<>();
                ProgressItem mProgressItem = new ProgressItem();
                // blue span
                mProgressItem = new ProgressItem();
                mProgressItem.progressItemPercentage = s.getTaken().floatValue();
                mProgressItem.color = R.color.colorBlue1;
                mProgressItemsList.add(mProgressItem);
                // green span
                mProgressItem = new ProgressItem();
                mProgressItem.progressItemPercentage = s.getApproved().floatValue();
                mProgressItem.color = R.color.colorGreen;
                mProgressItemsList.add(mProgressItem);
                // red span
                mProgressItem = new ProgressItem();
                mProgressItem.progressItemPercentage = s.getRejected().floatValue();
                Log.i("Mainactivity", mProgressItem.progressItemPercentage + "");
                mProgressItem.color = R.color.colorRed;
                mProgressItemsList.add(mProgressItem);
                // gray span
                mProgressItem = new ProgressItem();
                mProgressItem.progressItemPercentage = 100f - s.getTaken().floatValue() - s.getApproved().floatValue() - s.getRejected().floatValue();
                Log.i("Mainactivity", mProgressItem.progressItemPercentage + "");
                mProgressItem.color = R.color.transparent;
                mProgressItemsList.add(mProgressItem);

                progressBar.initData(mProgressItemsList);
                progressBar.invalidate();
            }else{
                ArrayList<ProgressItem> mProgressItemsList = new ArrayList<>();
                ProgressItem mProgressItem = new ProgressItem();
                // blue span
                mProgressItem = new ProgressItem();
                mProgressItem.progressItemPercentage = 0;
                mProgressItem.color = R.color.colorBlue1;
                mProgressItemsList.add(mProgressItem);
                // green span
                mProgressItem = new ProgressItem();
                mProgressItem.progressItemPercentage = 0;
                mProgressItem.color = R.color.colorGreen;
                mProgressItemsList.add(mProgressItem);
                // red span
                mProgressItem = new ProgressItem();
                mProgressItem.progressItemPercentage = 0;
                Log.i("Mainactivity", mProgressItem.progressItemPercentage + "");
                mProgressItem.color = R.color.colorRed;
                mProgressItemsList.add(mProgressItem);
                // gray span
                mProgressItem = new ProgressItem();
                mProgressItem.progressItemPercentage = 100f;
                Log.i("Mainactivity", mProgressItem.progressItemPercentage + "");
                mProgressItem.color = R.color.transparent;
                mProgressItemsList.add(mProgressItem);

                progressBar.initData(mProgressItemsList);
                progressBar.invalidate();
            }
        }
    }

    public static class ViewHeaderHolder extends RecyclerView.ViewHolder {

        public ViewHeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface OnClickCallback {
        void onInfoClick(int position);
        void onDeleteClick(int position);
    }

}
