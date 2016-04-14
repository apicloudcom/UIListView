/**
 * APICloud Modules
 * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */

package com.uzmap.pkg.uzmodules.UIListView.adapter;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.uzmap.pkg.uzmodules.UIListView.ViewUtils.ViewHolder;
import com.uzmap.pkg.uzmodules.UIListView.data.Config;
import com.uzmap.pkg.uzmodules.UIListView.data.ItemData;

/**
 * 可以继承此类自定义自己的 item 布局
 */

public abstract class BaseExtendsAdapter<T extends ItemData> extends BaseAdapter {
	
	private List<T> mDatas;
	private Context mContext;
	
	/**
	 * Config 类里面包含 item 样式的信息，比如背景，字体颜色，字体大小等
	 **/
	private Config mConfig;
	
	public BaseExtendsAdapter(Context context, Config config){
		this.mContext = context;
		this.mConfig = config;
	}
	
	public void setDatas(List<T> datas){
		this.mDatas = datas;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mDatas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		T curItemData = mDatas.get(arg0);

		ViewHolder viewHolder = null;

		arg1 = View.inflate(mContext, getItemLayoutId(), null);
		viewHolder = createViewHolder(arg1);
		
		/**
		 * item style setting
		 */
		setItemStyles(viewHolder, mConfig);

		/**
		 * item data setting
		 */
		setItemData(viewHolder, curItemData);

		/**
		 * click listener
		 */
		setClickListener(viewHolder, arg0);

		return arg1;
	}
	
	public abstract int getItemLayoutId();
	
	public abstract ViewHolder createViewHolder(View itemView);
	
	public abstract void setItemStyles(ViewHolder viewHolder, Config config);
	
	public abstract void setItemData(ViewHolder viewHolder, T itemData);
	
	public abstract void setClickListener(ViewHolder viewHolder, int position);
	
}
