/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.uzmap.pkg.uzmodules.UIListView.refreshable.internal;

import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzmodules.UIListView.refreshable.ILoadingLayout;
import com.uzmap.pkg.uzmodules.UIListView.refreshable.PullToRefreshBase.Mode;
import com.uzmap.pkg.uzmodules.UIListView.refreshable.PullToRefreshBase.Orientation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("ViewConstructor")
public abstract class LoadingLayout extends FrameLayout implements ILoadingLayout {

	static final String LOG_TAG = "PullToRefresh-LoadingLayout";

	static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();

	private FrameLayout mInnerLayout;

	protected final ImageView mHeaderImage;
	protected final ProgressBar mHeaderProgress;

	private boolean mUseIntrinsicAnimation;

	private final TextView mHeaderText;
	private final TextView mSubHeaderText;

	protected final Mode mMode;
	protected final Orientation mScrollDirection;

	private CharSequence mPullLabel;
	private CharSequence mRefreshingLabel;
	private CharSequence mReleaseLabel;
	
	public TextView getHeaderText(){
		return mHeaderText;
	}
	
	public TextView getSubHeaderText(){
		return mSubHeaderText;
	}

	public LoadingLayout(Context context, final Mode mode, final Orientation scrollDirection, TypedArray attrs) {
		super(context);
		mMode = mode;
		mScrollDirection = scrollDirection;
//
		switch (scrollDirection) {
			case HORIZONTAL:
//				LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_horizontal, this);
				break;
			case VERTICAL:
			default:
				int layoutId =UZResourcesIDFinder.getResLayoutID("pull_to_refresh_header_vertical");
				LayoutInflater.from(context).inflate(layoutId, this);
				break;
		}
		int fl_innerId =UZResourcesIDFinder.getResIdID("fl_inner");
		mInnerLayout = (FrameLayout) findViewById(fl_innerId);
		int pull_to_refresh_textId = UZResourcesIDFinder.getResIdID("pull_to_refresh_text");
		mHeaderText = (TextView) mInnerLayout.findViewById(pull_to_refresh_textId);
		int pull_to_refresh_progressId = UZResourcesIDFinder.getResIdID("pull_to_refresh_progress");
		mHeaderProgress = (ProgressBar) mInnerLayout.findViewById(pull_to_refresh_progressId);
		int pull_to_refresh_sub_textId = UZResourcesIDFinder.getResIdID("pull_to_refresh_sub_text");
		mSubHeaderText = (TextView) mInnerLayout.findViewById(pull_to_refresh_sub_textId);
		int pull_to_refresh_imageId = UZResourcesIDFinder.getResIdID("pull_to_refresh_image");
		mHeaderImage = (ImageView) mInnerLayout.findViewById(pull_to_refresh_imageId);

		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInnerLayout.getLayoutParams();

		switch (mode) {
			case PULL_FROM_END:
				lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.TOP : Gravity.LEFT;
				break;

			case PULL_FROM_START:
			default:
				lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.BOTTOM : Gravity.RIGHT;
				break;
		}
		
		reset();
	}

	public final void setHeight(int height) {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
		lp.height = height;
		requestLayout();
	}

	public final void setWidth(int width) {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
		lp.width = width;
		requestLayout();
	}

	public final int getContentSize() {
		switch (mScrollDirection) {
			case HORIZONTAL:
				return mInnerLayout.getWidth();
			case VERTICAL:
			default:
				return mInnerLayout.getHeight();
		}
	}

	public final void hideAllViews() {
		if (View.VISIBLE == mHeaderText.getVisibility()) {
			mHeaderText.setVisibility(View.INVISIBLE);
		}
		if (View.VISIBLE == mHeaderProgress.getVisibility()) {
			mHeaderProgress.setVisibility(View.INVISIBLE);
		}
		if (View.VISIBLE == mHeaderImage.getVisibility()) {
			mHeaderImage.setVisibility(View.INVISIBLE);
		}
		if (View.VISIBLE == mSubHeaderText.getVisibility()) {
			mSubHeaderText.setVisibility(View.INVISIBLE);
		}
	}

	public final void onPull(float scaleOfLayout) {
		if (!mUseIntrinsicAnimation) {
			onPullImpl(scaleOfLayout);
		}
	}

	public final void pullToRefresh() {
		if (null != mHeaderText) {
			mHeaderText.setText(mPullLabel);
		}

		// Now call the callback
		pullToRefreshImpl();
	}

	public final void refreshing() {
		if (null != mHeaderText) {
			mHeaderText.setText(mRefreshingLabel);
		}

		if (mUseIntrinsicAnimation) {
			((AnimationDrawable) mHeaderImage.getDrawable()).start();
		} else {
			// Now call the callback
			refreshingImpl();
		}

		if (null != mSubHeaderText) {
			mSubHeaderText.setVisibility(View.GONE);
		}
	}

	public final void releaseToRefresh() {
		if (null != mHeaderText) {
			mHeaderText.setText(mReleaseLabel);
		}

		// Now call the callback
		releaseToRefreshImpl();
	}

	public final void reset() {
		if (null != mHeaderText) {
			mHeaderText.setText(mPullLabel);
		}
		mHeaderImage.setVisibility(View.VISIBLE);

		if (mUseIntrinsicAnimation) {
			((AnimationDrawable) mHeaderImage.getDrawable()).stop();
		} else {
			// Now call the callback
			resetImpl();
		}

		if (null != mSubHeaderText) {
			if (TextUtils.isEmpty(mSubHeaderText.getText())) {
				mSubHeaderText.setVisibility(View.GONE);
			} else {
				mSubHeaderText.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		setSubHeaderText(label);
	}

	public final void setLoadingDrawable(Drawable imageDrawable) {
		// Set Drawable
		mHeaderImage.setImageDrawable(imageDrawable);
		mUseIntrinsicAnimation = (imageDrawable instanceof AnimationDrawable);

		// Now call the callback
		onLoadingDrawableSet(imageDrawable);
	}

	public void setPullLabel(CharSequence pullLabel) {
		mPullLabel = pullLabel;
	}

	public void setRefreshingLabel(CharSequence refreshingLabel) {
		mRefreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(CharSequence releaseLabel) {
		mReleaseLabel = releaseLabel;
	}

	@Override
	public void setTextTypeface(Typeface tf) {
		mHeaderText.setTypeface(tf);
	}

	public final void showInvisibleViews() {
		if (View.INVISIBLE == mHeaderText.getVisibility()) {
			mHeaderText.setVisibility(View.VISIBLE);
		}
		if (View.INVISIBLE == mHeaderProgress.getVisibility()) {
			mHeaderProgress.setVisibility(View.VISIBLE);
		}
		if (View.INVISIBLE == mHeaderImage.getVisibility()) {
			mHeaderImage.setVisibility(View.VISIBLE);
		}
		if (View.INVISIBLE == mSubHeaderText.getVisibility()) {
			mSubHeaderText.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Callbacks for derivative Layouts
	 */

	protected abstract int getDefaultDrawableResId();

	protected abstract void onLoadingDrawableSet(Drawable imageDrawable);

	protected abstract void onPullImpl(float scaleOfLayout);

	protected abstract void pullToRefreshImpl();

	protected abstract void refreshingImpl();

	protected abstract void releaseToRefreshImpl();

	protected abstract void resetImpl();

	private void setSubHeaderText(CharSequence label) {
		if (null != mSubHeaderText) {
			if (TextUtils.isEmpty(label)) {
				mSubHeaderText.setVisibility(View.GONE);
			} else {
				mSubHeaderText.setText(label);
				mSubHeaderText.setVisibility(View.VISIBLE);
			}
		}
	}

	@SuppressWarnings("unused")
	private void setSubTextAppearance(int value) {
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextAppearance(getContext(), value);
		}
	}

	@SuppressWarnings("unused")
	private void setSubTextColor(ColorStateList color) {
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextColor(color);
		}
	}

	@SuppressWarnings("unused")
	private void setTextAppearance(int value) {
		if (null != mHeaderText) {
			mHeaderText.setTextAppearance(getContext(), value);
		}
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextAppearance(getContext(), value);
		}
	}

	@SuppressWarnings("unused")
	private void setTextColor(ColorStateList color) {
		if (null != mHeaderText) {
			mHeaderText.setTextColor(color);
		}
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextColor(color);
		}
	}

}
