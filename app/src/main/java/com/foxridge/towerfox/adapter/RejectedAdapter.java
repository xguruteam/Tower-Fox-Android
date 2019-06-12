package com.foxridge.towerfox.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.model.RejectDisplayModel;
import com.foxridge.towerfox.views.CustomFontTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RejectedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RejectDisplayModel> items = new ArrayList<>();
    private OnClickCallback onClickCallback;

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private Context context;
    public RejectedAdapter(Context context, OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
        this.context = context;
    }

    public RejectDisplayModel getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item, parent, false);
            final ViewItemHolder holder = new ViewItemHolder(view);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCallback.onInfoClick(holder.getAdapterPosition());
                }
            });
            return holder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            return new ViewHeaderHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == ITEM_VIEW_TYPE_ITEM) {
            ViewItemHolder viewHolder = (ViewItemHolder) holder;
            RejectDisplayModel model = items.get(position);
            viewHolder.tvItemName.setText(model.getItemName());
            viewHolder.tvItemName.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            viewHolder.ivSync.setVisibility(View.GONE);
            viewHolder.ivRejectCamera.setImageResource(R.drawable.ic_camera_red);
        } else {
            ViewHeaderHolder viewHolder = (ViewHeaderHolder) holder;
            RejectDisplayModel model = items.get(position);
            viewHolder.tvHeader.setText(model.getDescription());
        }
    }

    @Override
    public int getItemCount() {
//        return items.size();
        return items.size();
    }

    public boolean isHeader(int position) {
        return false;//items.get(position).getBud().getType() != 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).getStatus() == 101) {
            return ITEM_VIEW_TYPE_HEADER;
        }
        return ITEM_VIEW_TYPE_ITEM;//items.get(position).getBud().getType() == 0 ? ITEM_VIEW_TYPE_ITEM : ITEM_VIEW_TYPE_HEADER;
    }

    public void setItems(List<RejectDisplayModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    public static class ViewItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_camera)
        ImageView ivRejectCamera;
        @BindView(R.id.tv_item_name)
        CustomFontTextView tvItemName;
        @BindView(R.id.iv_sync)
        ImageView ivSync;
        @BindView(R.id.iv_right_arrow)
        ImageView ivRightArrow;

        public ViewItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public static class ViewHeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_item_name)
        CustomFontTextView tvHeader;
        public ViewHeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface OnClickCallback {
        void onInfoClick(int position);
    }

}
