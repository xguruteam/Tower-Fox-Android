package com.foxridge.towerfox.ui;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.utils.Utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    private Handler mHandler;
    private Runnable mRunnable;
    private View[] mViews = null;

    @Nullable
    @BindView(R.id.container_view)
    public FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupStatusbar();
        int layoutId = addView();
        setContentView(layoutId);
        ButterKnife.bind(this);
//		CustomPreferences.init(this);
        initView(savedInstanceState);

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }


    public void setupStatusbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            if (true) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                // We want to change tint color to white again.
                // You can also record the flags in advance so that you can turn UI back completely if
                // you have set other flags before, such as translucent or full screen.
                decor.setSystemUiVisibility(0);
            }
        }

    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (activity != null) {
            if (activity.getCurrentFocus() != null) {
                if(activity.getCurrentFocus().getWindowToken() != null) {
                    inputMethodManager.hideSoftInputFromWindow(
                            activity.getCurrentFocus().getWindowToken(), 0);
                }
            }
        }
    }
    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    protected int addView() {

        return 0;
    }

    protected void initView(@Nullable Bundle savedInstanceState) {

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {

                if (mViews != null) {

                    for (View view : mViews) {

                        view.setVisibility(View.VISIBLE);
                    }
                }

            }
        };
    }

    @Override
    public void onClick(View view) {

    }

    public void addFragment(BaseFragment fragment, boolean isAddToBackStack) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        BaseFragment currentFragment = (BaseFragment) getSupportFragmentManager()
                .findFragmentById(R.id.container_view);
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left);
        ft.add(R.id.container_view, fragment, fragment.getClass().getName());
        if (isAddToBackStack) {

            ft.addToBackStack(null);
        }

        if (currentFragment != null) {
            ft.hide(currentFragment);
        }

        View current = getCurrentFocus();
        if (current != null) {
            Utilities.dismissKeyboard(this, current);
        }
//        ft.setCustomAnimations( R.anim.slide_in_right, 0, 0, R.anim.slide_out_right);
        ft.commit();
    }

    public void addFragment(BaseFragment fragment, String isAddToBackStack) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        BaseFragment currentFragment = (BaseFragment) getSupportFragmentManager()
                .findFragmentById(R.id.container_view);
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left);
        ft.add(R.id.container_view, fragment, fragment.getClass().getName());
        if (!isAddToBackStack.equals("")) {
            ft.addToBackStack(isAddToBackStack);
        }

        if (currentFragment != null) {

            ft.hide(currentFragment);
        }

        View current = getCurrentFocus();
        if (current != null) {
            Utilities.dismissKeyboard(this, current);
        }
//        ft.setCustomAnimations( R.anim.slide_in_right, 0, 0, R.anim.slide_out_right);
        ft.commit();
    }

    /**
     * Back to previous fragment
     */
    public void backFragment(boolean backToHome) {

        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() <= 0) {

            finish();
            return;
        }

        if (backToHome) {

            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);;
        } else {

            fm.popBackStack();
        }
    }

    public void backFragment(String backToHome) {

        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() <= 0) {

            finish();
            return;
        }

        if (!backToHome.equals("")) {

            fm.popBackStack(backToHome, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {

            fm.popBackStack();
        }
    }


}
