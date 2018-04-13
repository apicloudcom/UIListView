/**
 * APICloud Modules
 * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */
package com.uzmap.pkg.uzmodules.UIListView.ViewUtils;

import com.uzmap.pkg.uzmodules.UIListView.widget.CircleImageView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHolder {

	public View mFrontView;
	public View mBorder;

	public CircleImageView mItemIv;
	public TextView mTitleTv;
	public TextView mSubTitleTv;

	public TextView mRemarkTv;
	public ImageView mIconIv;

	public LinearLayout leftBtnsLayout;
	public LinearLayout rightBtnsLayout;

	public LinearLayout remarkGroupLayout;

}
