package com.foxridge.towerfox.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.utils.Utilities;
import com.foxridge.towerfox.views.CustomFontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment implements View.OnClickListener, OnBackPressListener {

    @Nullable
    @BindView(R.id.btn_left)
    LinearLayout leftButton;

    @Nullable
    @BindView(R.id.iv_left_action)
    ImageView ivLeft;

    @Nullable
    @BindView(R.id.tv_left_action)
    CustomFontTextView tvLeft;

    @Nullable
    @BindView(R.id.btn_right)
    LinearLayout rightButton;

    @Nullable
    @BindView(R.id.iv_right_action)
    ImageView ivRight;

    @Nullable
    @BindView(R.id.tv_right_action)
    CustomFontTextView tvRight;

    @Nullable
    @BindView(R.id.tv_title)
    CustomFontTextView tvTitle;

    @Nullable
    @BindView(R.id.btn_top)
    LinearLayout topButton;

    @Nullable
    @BindView(R.id.iv_top_action)
    ImageView ivTop;

    @Nullable
    @BindView(R.id.tv_top_action)
    CustomFontTextView tvTop;

    @Nullable
    @BindView(R.id.container_view)
    public
    FrameLayout containerview;

    protected MainActivity mAct;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        int viewId = addView();
        View view = inflater.inflate(viewId, container, false);
        ButterKnife.bind(this, view);
        initView();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utilities.dismissKeyboard(mAct, view);
            }
        });
        return view;
    }

    /**
     * Add fragment
     */
    protected void addFragment(BaseFragment fragment, boolean addToStack) {

        mAct.addFragment(fragment, addToStack);
    }

    protected void addFragment(BaseFragment fragment, String addToStack) {

        mAct.addFragment(fragment, addToStack);
    }

    /**
     * Back fragment
     *
     * @param backToHome true if wanna back to home
     */
    protected void backFragment(boolean backToHome) {

        mAct.backFragment(backToHome);
    }

    protected void backFragment(String backToHome) {

        mAct.backFragment(backToHome);
    }

    /**
     * Add view
     */
    protected int addView() {

        return 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mAct = (MainActivity) context;
    }

    /**
     * Init child view
     */
    protected void initView() {

        if (leftButton != null) {
            leftButton.setOnClickListener(this);
        }
        if (rightButton != null) {
            rightButton.setOnClickListener(this);
        }
        if (topButton != null) {
            topButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_left:

                break;
            case R.id.btn_right:
                break;
            case R.id.btn_top:
                break;
        }
    }


    @Override
    public boolean onBackPressed() {

        return new BackPressImpl(this).onBackPressed();
    }

    /**
     * add fragment
     *
     * @param fragment         Fragment
     * @param isAddToBackStack Add fragment to back stack or not
     */
    public void addChildFragment(Fragment fragment, boolean isAddToBackStack) {

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        BaseFragment currentFragment = (BaseFragment) getChildFragmentManager()
                .findFragmentById(R.id.container_view);
        ft.add(R.id.container_view, fragment, fragment.getClass().getName());
        if (isAddToBackStack) {

            ft.addToBackStack(null);
        }

        if (currentFragment != null) {

            ft.hide(currentFragment);
        } else {
            ft.show(fragment);
        }

        ft.commit();
    }

    /**
     * add fragment
     *
     * @param fragment         Fragment
     * @param isAddToBackStack Add fragment to back stack or not
     */
    public void addChildFragment(BaseFragment fragment, boolean isAddToBackStack) {

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        BaseFragment currentFragment = (BaseFragment) getChildFragmentManager()
                .findFragmentById(R.id.container_view);
        ft.add(R.id.container_view, fragment, fragment.getClass().getName());
        if (isAddToBackStack) {

            ft.addToBackStack(null);
        }

        if (currentFragment != null) {

            ft.hide(currentFragment);
        } else {
            ft.show(fragment);
        }

        ft.commit();
    }

    public void addChildFragment(BaseFragment fragment, String backStack) {

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        BaseFragment currentFragment = (BaseFragment) getChildFragmentManager()
                .findFragmentById(R.id.container_view);
        ft.add(R.id.container_view, fragment, fragment.getClass().getName());
//		if (isAddToBackStack) {

        ft.addToBackStack(backStack);
//		}

        if (currentFragment != null) {

            ft.hide(currentFragment);
        }

        ft.commit();
    }

    public void backChildFragment() {

        FragmentManager fm = getChildFragmentManager();
        if (fm.getBackStackEntryCount() <= 0) {

            return;
        }

        fm.popBackStack();
    }

    public void backChildFragment(String backStack) {

        FragmentManager fm = getChildFragmentManager();
        if (fm.getBackStackEntryCount() <= 0) {

            return;
        }

        fm.popBackStack(backStack, 0);
    }
}