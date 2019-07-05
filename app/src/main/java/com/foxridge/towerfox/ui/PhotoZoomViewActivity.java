package com.foxridge.towerfox.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.foxridge.towerfox.R;
import com.foxridge.towerfox.views.CustomFontTextView;
import com.jsibbold.zoomage.ZoomageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoZoomViewActivity extends AppCompatActivity {

    private boolean controllerVisible;

    private static final float DRAG_THRESHOLD = 10;
    private static final long LONG_PRESS_THRESHOLD_MS = 500;

    private long tapStartTimeMs;
    private float tapPositionX;
    private float tapPositionY;

    View toolbar;
    ZoomageView ivFullScreenPhotoView;

    @BindView(R.id.tv_title)
    CustomFontTextView tvTitle;

    @BindView(R.id.btn_left)
    LinearLayout leftButton;

    public static void startActivity(Context act, String imageUrl, String imageTitle) {
        Intent i = new Intent(act, PhotoZoomViewActivity.class);
        i.putExtra("imageUrl", imageUrl);
        i.putExtra("imageTitle", imageTitle);
        act.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_zoom_view);

        ButterKnife.bind(this);

        toolbar = findViewById(R.id.toobar);

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");
        Uri imageUri = Uri.parse(imageUrl);

        ivFullScreenPhotoView = (ZoomageView) findViewById(R.id.ivFullScreenPhotoView);

        ivFullScreenPhotoView.setImageURI(imageUri);

        String title = intent.getStringExtra("imageTitle");
        tvTitle.setText(title);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        showController();
    }

    public void showController() {
        toolbar.setVisibility(View.VISIBLE);
        controllerVisible = true;
    }

    public void hideController() {
        toolbar.setVisibility(View.INVISIBLE);
        controllerVisible = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                tapStartTimeMs = SystemClock.elapsedRealtime();
                tapPositionX = ev.getX();
                tapPositionY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (tapStartTimeMs != 0
                        && (Math.abs(ev.getX() - tapPositionX) > DRAG_THRESHOLD
                        || Math.abs(ev.getY() - tapPositionY) > DRAG_THRESHOLD)) {
                    tapStartTimeMs = 0;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (tapStartTimeMs != 0) {
                    if (SystemClock.elapsedRealtime() - tapStartTimeMs < LONG_PRESS_THRESHOLD_MS) {
                        if (!controllerVisible) {
                            showController();
                        } else {
                            hideController();
                        }
                    }
                    tapStartTimeMs = 0;
                }
        }
        return true;
    }
}
