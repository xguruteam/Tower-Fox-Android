package com.foxridge.towerfox.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.model.CategoryDisplayModel;
import com.foxridge.towerfox.model.ProgressItem;
import com.foxridge.towerfox.views.CustomFontTextView;
import com.foxridge.towerfox.views.CustomSeekBar;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CategoryDisplayModel> items = new ArrayList<>();
    private OnClickCallback onClickCallback;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private static final int ITEM_VIEW_TYPE_ITEM1 = 2;

    private Context context;
    public CategoryAdapter(Context context, OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new ViewItemHolder(view);
        } else if (viewType == ITEM_VIEW_TYPE_ITEM1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item, parent, false);
            return new ViewItem1Holder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_header, parent, false);
            return new ViewHeaderHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == ITEM_VIEW_TYPE_ITEM) {
            ViewItemHolder viewHolder = (ViewItemHolder) holder;
            CategoryDisplayModel model = items.get(position);
            viewHolder.tvCategoryName.setText(model.getCategoryName());
            if (model.get_required() > 0) {
                viewHolder.tvRemaining.setText(model.get_required()+" Required");
            }else{
                viewHolder.tvRemaining.setText("");
            }
            if (model.get_rejected() > 0) {
                viewHolder.tvRejected.setText(model.get_rejected()+" Rejected");
            }else{
                viewHolder.tvRejected.setText("");
            }
            if (model.get_taken() > 0) {
                viewHolder.tvPending.setText(model.get_taken()+" Pending");
            }else{
                viewHolder.tvPending.setText("");
            }
            if (model.get_approved() > 0) {
                viewHolder.tvApproved.setText(model.get_approved()+" Approved");
            }else{
                viewHolder.tvApproved.setText("");
            }

            ArrayList<ProgressItem> mProgressItemsList = new ArrayList<>();
            ProgressItem mProgressItem = new ProgressItem();
            // blue span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = model.get_takenPercent().floatValue();
            mProgressItem.color = R.color.colorBlue1;
            mProgressItemsList.add(mProgressItem);
            // green span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = model.get_approvedPercent().floatValue();
            mProgressItem.color = R.color.colorGreen;
            mProgressItemsList.add(mProgressItem);
            // red span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = model.get_rejectedPercent().floatValue();
            Log.i("Mainactivity", mProgressItem.progressItemPercentage + "");
            mProgressItem.color = R.color.colorRed;
            mProgressItemsList.add(mProgressItem);
            // gray span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = 100f - model.get_takenPercent().floatValue() - model.get_approvedPercent().floatValue() - model.get_rejectedPercent().floatValue();
            Log.i("Mainactivity", mProgressItem.progressItemPercentage + "");
            mProgressItem.color = R.color.transparent;
            mProgressItemsList.add(mProgressItem);

            viewHolder.progressBar.initData(mProgressItemsList);
            viewHolder.progressBar.invalidate();

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCallback.onCategoryClick(position);
                }
            });

        } else if (holder.getItemViewType() == ITEM_VIEW_TYPE_ITEM1){
            ViewItem1Holder viewHolder = (ViewItem1Holder) holder;
            CategoryDisplayModel model = items.get(position);
            if (model.getIStatus() == 1 || model.getIStatus() == 7) {
                viewHolder.tvItemName.setText(model.getItemName());
                viewHolder.tvItemName.setTextColor(ContextCompat.getColor(context, R.color.color_text_dark_gray));
                viewHolder.ivSync.setVisibility(View.GONE);
                viewHolder.ivRejectCamera.setImageResource(R.drawable.ic_camera_gray);
            }else if (model.getIStatus() == 2) {
                viewHolder.tvItemName.setText(model.getItemName());
                viewHolder.tvItemName.setTextColor(ContextCompat.getColor(context, R.color.colorBlue1));
                viewHolder.ivSync.setVisibility(View.VISIBLE);
                viewHolder.ivRejectCamera.setImageResource(R.drawable.ic_clock);
            }else if (model.getIStatus() == 3 || model.getIStatus() == 7) {
                viewHolder.tvItemName.setText(model.getItemName());
                viewHolder.tvItemName.setTextColor(ContextCompat.getColor(context, R.color.colorBlue1));
                viewHolder.ivSync.setVisibility(View.GONE);
                viewHolder.ivRejectCamera.setImageResource(R.drawable.ic_clock);
            }else if (model.getIStatus() == 4 || model.getIStatus() == 7) {
                viewHolder.tvItemName.setText(model.getItemName());
                viewHolder.tvItemName.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGreen));
                viewHolder.ivSync.setVisibility(View.GONE);
                viewHolder.ivRejectCamera.setImageResource(R.drawable.ic_approved);
            }else if (model.getIStatus() == 0 || model.getIStatus() == 7) {
                viewHolder.tvItemName.setText(model.getItemName());
                viewHolder.tvItemName.setTextColor(ContextCompat.getColor(context, R.color.color_text_dark_gray));
                viewHolder.ivSync.setVisibility(View.GONE);
                viewHolder.ivRejectCamera.setImageResource(R.drawable.ic_camera_red);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCallback.onInfoClick(position);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
//        return items.size();
        return items.size();
    }

    public CategoryDisplayModel getItem(int position) {
        if (position < items.size())
            return items.get(position);
        else
            return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).getType().equals("Items")) {
            return ITEM_VIEW_TYPE_ITEM1;
        }
        return ITEM_VIEW_TYPE_ITEM;//items.get(position).getBud().getType() == 0 ? ITEM_VIEW_TYPE_ITEM : ITEM_VIEW_TYPE_HEADER;
    }

    public void setItems(final List<CategoryDisplayModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    public static class ViewItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_category_name)
        CustomFontTextView tvCategoryName;
        @BindView(R.id.tv_remaining)
        CustomFontTextView tvRemaining;
        @BindView(R.id.tv_rejected)
        CustomFontTextView tvRejected;
        @BindView(R.id.tv_pending)
        CustomFontTextView tvPending;
        @BindView(R.id.tv_approved)
        CustomFontTextView tvApproved;
        @BindView(R.id.progress_view)
        CustomSeekBar progressBar;

        public ViewItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
    public static class ViewItem1Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_camera)
        ImageView ivRejectCamera;
        @BindView(R.id.tv_item_name)
        CustomFontTextView tvItemName;
        @BindView(R.id.iv_sync)
        ImageView ivSync;
        @BindView(R.id.iv_right_arrow)
        ImageView ivRightArrow;
        public ViewItem1Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
        void onCategoryClick(int position);
    }

}
