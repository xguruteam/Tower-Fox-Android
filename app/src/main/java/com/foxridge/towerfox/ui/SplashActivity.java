package com.foxridge.towerfox.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.utils.Constants;


public class SplashActivity extends BaseActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public int addView() {
        return  R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashActivity.this, ServerAddressActivity.class);
                editor.putBoolean("isLoggedIn", false);
                editor.putBoolean("isFirstTime", true);
                editor.apply();
                startActivity(homeIntent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        }, 2000);

    }
}
