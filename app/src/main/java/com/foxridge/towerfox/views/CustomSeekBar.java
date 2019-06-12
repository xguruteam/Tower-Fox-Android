package com.foxridge.towerfox.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.foxridge.towerfox.model.ProgressItem;

import java.util.ArrayList;

public class CustomSeekBar extends SeekBar {
//	private float totalSpan = 100;
//	private float redSpan = 12;
//	private float blueSpan = 40;
//	private float greenSpan = 20;
//
	private ArrayList<ProgressItem> mProgressItemsList = new ArrayList<>();

	public CustomSeekBar(Context context) {
		super(context);
//		initDummyData();
	}

	public CustomSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
//		initDummyData();
	}

	public CustomSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		initDummyData();
	}

	public void initData(ArrayList<ProgressItem> progressItemsList) {
		this.mProgressItemsList = progressItemsList;
	}

	public void initDummyData() {
//		ProgressItem mProgressItem = new ProgressItem();
//		// blue span
//		mProgressItem = new ProgressItem();
//		mProgressItem.progressItemPercentage = (blueSpan / totalSpan) * 100;
//		mProgressItem.color = R.color.colorPrimary;
//		mProgressItemsList.add(mProgressItem);
//		// green span
//		mProgressItem = new ProgressItem();
//		mProgressItem.progressItemPercentage = (greenSpan / totalSpan) * 100;
//		mProgressItem.color = R.color.colorGreen;
//		mProgressItemsList.add(mProgressItem);
//		// red span
//		mProgressItem = new ProgressItem();
//		mProgressItem.progressItemPercentage = ((redSpan / totalSpan) * 100);
//		Log.i("Mainactivity", mProgressItem.progressItemPercentage + "");
//		mProgressItem.color = R.color.colorRed;
//		mProgressItemsList.add(mProgressItem);
//		// gray span
//		mProgressItem = new ProgressItem();
//		mProgressItem.progressItemPercentage = ((totalSpan - redSpan - greenSpan - blueSpan) / totalSpan) * 100;
//		Log.i("Mainactivity", mProgressItem.progressItemPercentage + "");
//		mProgressItem.color = R.color.colorTabGray;
//		mProgressItemsList.add(mProgressItem);
//
//		this.initData(mProgressItemsList);
//		this.invalidate();
//
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	protected void onDraw(Canvas canvas) {
		if (mProgressItemsList.size() > 0) {
			int progressBarWidth = getWidth();
			int progressBarHeight = getHeight();
			int thumboffset = getThumbOffset();
			int lastProgressX = 0;
			int progressItemWidth, progressItemRight;
			for (int i = 0; i < mProgressItemsList.size(); i++) {
				ProgressItem progressItem = mProgressItemsList.get(i);
				Paint progressPaint = new Paint();
				progressPaint.setColor(getResources().getColor(
						progressItem.color));

				progressItemWidth = (int) (progressItem.progressItemPercentage
						* progressBarWidth / 100);

				progressItemRight = lastProgressX + progressItemWidth;

				// for last item give right to progress item to the width
				if (i == mProgressItemsList.size() - 1
						&& progressItemRight != progressBarWidth) {
					progressItemRight = progressBarWidth;
				}
				Rect progressRect = new Rect();
				progressRect.set(lastProgressX, thumboffset / 2,
						progressItemRight, progressBarHeight - thumboffset / 2);
				canvas.drawRect(progressRect, progressPaint);
				lastProgressX = progressItemRight;
			}
			super.onDraw(canvas);
		}

	}

}
