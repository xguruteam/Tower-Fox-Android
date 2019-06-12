package com.foxridge.towerfox.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.utils.Globals;
import com.foxridge.towerfox.views.CustomFontTextView;

import butterknife.BindView;

public class RejectionDetailActivity extends BaseActivity implements View.OnClickListener {

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

    @BindView(R.id.tv_comment)
    CustomFontTextView tvComments;
    @BindView(R.id.tv_item_name)
    CustomFontTextView tvItemName;
    @BindView(R.id.tv_reviewer)
    CustomFontTextView tvReviewer;
    @BindView(R.id.tv_review_date)
    CustomFontTextView tvReviewedDate;

    @Override
    public int addView() {
        return  R.layout.activity_reject_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    public void initView() {
        leftButton.setOnClickListener(this);
        rightButton.setVisibility(View.GONE);
        tvTitle.setText(R.string.rejection_details);
        tvItemName.setText(Globals.getInstance().storage_loadString("ItemName"));
        tvComments.setText(Globals.getInstance().storage_loadString("Comments"));
        tvReviewer.setText(Globals.getInstance().storage_loadString("ReviewerName") != null ? Globals.getInstance().storage_loadString("ReviewerName") : "");
        tvReviewedDate.setText(Globals.getInstance().storage_loadString("ReviewDate") != null ? Globals.getInstance().storage_loadString("ReviewDate") : "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
        }
    }



}
