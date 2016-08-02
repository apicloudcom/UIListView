/**
 * APICloud Modules
 * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */

package com.uzmap.pkg.uzmodules.UIListView;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.annotation.UzJavascriptMethod;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import com.uzmap.pkg.uzkit.UZUtility;
import com.uzmap.pkg.uzmodules.UIListView.adapter.ListAdapter;
import com.uzmap.pkg.uzmodules.UIListView.constants.Constants;
import com.uzmap.pkg.uzmodules.UIListView.data.ButtonInfo;
import com.uzmap.pkg.uzmodules.UIListView.data.Config;
import com.uzmap.pkg.uzmodules.UIListView.data.ItemData;
import com.uzmap.pkg.uzmodules.UIListView.data.Utils;
import com.uzmap.pkg.uzmodules.UIListView.refreshable.PullToRefreshBase;
import com.uzmap.pkg.uzmodules.UIListView.refreshable.PullToRefreshBase.Mode;
import com.uzmap.pkg.uzmodules.UIListView.refreshable.PullToRefreshBase.OnRefreshListener2;
import com.uzmap.pkg.uzmodules.UIListView.refreshable.PullToRefreshListView;
import com.uzmap.pkg.uzmodules.UIListView.refreshable.internal.LoadingLayout;
import com.uzmap.pkg.uzmodules.UIListView.swipeList.SwipeMenu;
import com.uzmap.pkg.uzmodules.UIListView.swipeList.SwipeMenuListView;
import com.uzmap.pkg.uzmodules.UIListView.swipeList.SwipeMenuListView.OnMenuItemClickListener;

public class UIListView extends UZModule {

	public static final String TAG = UIListView.class.getSimpleName();

	private PullToRefreshListView mRefreshableList = null;

	/**
	 * Adapter of the SwipeMenuListView
	 */
	private ListAdapter mAdapter;

	private Config mConfig;

	private static final String TYPE_RIGHT = "right";

	private static final String TEXT_DOWN = "下拉可以刷新…";
	private static final String TEXT_UP = "松开开始刷新…";
	private static final String REFRESH_LABEL = "正在加载...";
	private static final String REFRESH_LABEL_TIME_TIPS = "最后更新:";

	private static final String EVENT_TYPE_SHOW = "show";
	private static final String EVENT_TYPE_RIGHT_BTN_CLICK = "clickRightBtn";

	private static final String EVENT_TYPE_ITEM_CLICK = "clickContent";
	public static final String EVENT_TYPE_ITEM_IMG_CLICK = "clickImg";
	public static final String EVENT_TYPE_ITEM_REMARK_CLICK = "clickRemark";

	private OnRefreshListener refreshListener;
	
	private int mFirstVisibleItem = 0;

	public UIListView(UZWebView webView) {
		super(webView);
	}

	@UzJavascriptMethod
	public void jsmethod_open(final UZModuleContext moduleContext) {

		mConfig = new Config(moduleContext, getContext());
		Context contex = this.getContext();
		Constants.WIDGET_INFO = getWidgetInfo();

		mAdapter = new ListAdapter(contex, moduleContext, mConfig.itemDatas, mConfig, getWidgetInfo());

		if (mRefreshableList == null) {
			mRefreshableList = new PullToRefreshListView(getContext());
		} else {
			removeViewFromCurWindow(mRefreshableList);
			mRefreshableList = new PullToRefreshListView(getContext());
		}

		mRefreshableList.setMode(Mode.DISABLED);
		SwipeMenuListView swipeListView = (SwipeMenuListView) mRefreshableList.getRefreshableView();
		swipeListView.setAdapter(mAdapter);
		swipeListView.setDivider(null);
		swipeListView.setDividerHeight(0);

		refreshListener = new OnRefreshListener();
		mRefreshableList.setOnRefreshListener(refreshListener);
		
		mRefreshableList.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				mFirstVisibleItem = firstVisibleItem;
			}
		});

		/**
		 * click the item menu listener
		 */
		swipeListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				Utils.callback(moduleContext, EVENT_TYPE_RIGHT_BTN_CLICK, position, index);
				return false;
			}
		});

		/**
		 * click the item listener
		 */
		swipeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Utils.callback(moduleContext, EVENT_TYPE_ITEM_CLICK, arg2, -1);
			}
		});

		LayoutParams params = new LayoutParams(mConfig.w, mConfig.h);
		params.leftMargin = mConfig.x;
		params.topMargin = mConfig.y;

		insertViewToCurWindow(mRefreshableList, params, mConfig.fixedOn, mConfig.fixed);
		Utils.callback(moduleContext, EVENT_TYPE_SHOW, -1, -1);

	}

	@SuppressLint("SimpleDateFormat")
	private class OnRefreshListener implements OnRefreshListener2<ListView> {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			if (showHeaderTime) {

				SimpleDateFormat dataFormat = new SimpleDateFormat("MM-dd hh:mm");
				String label = dataFormat.format(new Date());
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(REFRESH_LABEL_TIME_TIPS + label);

			}

			Log.i("lyh", "onPullDownToRefresh ... ");

			if (pullDownContext != null) {
				JSONObject json = new JSONObject();
				pullDownContext.success(json, false);
			}
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			if (showFooterTime) {
				SimpleDateFormat dataFormat = new SimpleDateFormat("MM-dd hh:mm");
				String label = dataFormat.format(new Date());
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(REFRESH_LABEL_TIME_TIPS + label);
			}

			Log.i("lyh", "onPullUpToRefresh ... ");

			if (pullUpContext != null) {
				JSONObject json = new JSONObject();
				pullUpContext.success(json, false);
			}
		}
	}

	@UzJavascriptMethod
	public void jsmethod_getIndex(UZModuleContext context) {

		String key = context.optString("key");
		String value = context.optString("value");

		if (mAdapter == null) {
			return;
		}

		if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value) || mAdapter.getItemDatas() == null) {
			return;
		}

		for (int i = 0; i < mAdapter.getItemDatas().size(); i++) {
			ItemData itemData = mAdapter.getItemDatas().get(i);
			if (itemData != null) {

				JSONObject itemJson = itemData.itemObj;
				if (value.equals(itemJson.optString(key))) {

					JSONObject ret = new JSONObject();
					try {
						ret.put("index", i);
						ret.put("data", itemJson);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					context.success(ret, false);
					return;
				}
			}
		}
	}

	@UzJavascriptMethod
	public void jsmethod_setAttr(UZModuleContext moduleContext) {

		if (!moduleContext.isNull("y")) {
			mConfig.y = moduleContext.optInt("y");
		}

		if (!moduleContext.isNull("h")) {
			mConfig.h = moduleContext.optInt("h");
		}

		if (mRefreshableList != null) {
			removeViewFromCurWindow(mRefreshableList);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mConfig.w, mConfig.h);
			params.topMargin = mConfig.y;
			params.leftMargin = mConfig.x;
			insertViewToCurWindow(mRefreshableList, params);

			callback(moduleContext, true);
		}
	}

	public void callback(UZModuleContext uzContext, boolean status) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", status);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		uzContext.success(ret, false);
	}

	public void jsmethod_getDataByIndex(UZModuleContext uzContext) {

		if (mAdapter == null || mAdapter.getItemDatas() == null) {
			callback(uzContext, false);
			return;
		}
		int index = uzContext.optInt("index");
		if (index >= 0 && index < mAdapter.getItemDatas().size()) {
			ItemData itemData = mAdapter.getItemDatas().get(index);

			JSONObject ret = new JSONObject();
			try {
				ret.put("data", itemData.itemObj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			uzContext.success(ret, false);
		} else {
			callback(uzContext, false);
		}
	}

	public void jsmethod_setSwipeBtns(UZModuleContext uzContext) {

		int index = uzContext.optInt("index");
		String type = TYPE_RIGHT;

		if (mAdapter == null) {
			callback(uzContext, false);
			return;
		}

		if (index < 0 || index >= mAdapter.getItemDatas().size()) {
			callback(uzContext, false);
			return;
		}

		if (!uzContext.isNull("type") && !TextUtils.isEmpty(uzContext.optString("type"))) {
			type = uzContext.optString("type");
		}

		if (!uzContext.isNull("btns") && mAdapter != null && type.equals(TYPE_RIGHT)) {
			JSONArray menuBtns = uzContext.optJSONArray("btns");
			ArrayList<ButtonInfo> btns = new ArrayList<ButtonInfo>();

			if (menuBtns != null) {
				for (int i = 0; i < menuBtns.length(); i++) {
					btns.add(Utils.parseBtnInfo(menuBtns.optJSONObject(i)));
				}
				mAdapter.getItemDatas().get(index).rightBtns = btns;
			}
			callback(uzContext, true);
		} else {
			callback(uzContext, false);
		}
		mAdapter.notifyDataSetChanged();

	}

	public void jsmethod_reloadData(UZModuleContext uzContext) {

		if (mAdapter == null) {
			callback(uzContext, false);
			return;
		}

		if (mRefreshableList != null) {
			mRefreshableList.onRefreshComplete();
		}

		if (!uzContext.isNull("data")) {
			JSONArray datas = uzContext.optJSONArray("data");
			ArrayList<ItemData> itemDatas = new ArrayList<ItemData>();
			for (int i = 0; i < datas.length(); i++) {
				ItemData itemData = new ItemData(datas.optJSONObject(i));
				if (itemData.rightBtns.size() == 0) {
					itemData.setRightBtns(mConfig.rightBtns);
				}
				itemDatas.add(itemData);
			}
			
			mAdapter.setData(itemDatas);
			mAdapter.notifyDataSetChanged();

			callback(uzContext, true);
		} else {
			callback(uzContext, false);
		}
	}

	public void jsmethod_deleteItem(UZModuleContext uzContext) {

		int index = uzContext.optInt("index");

		if (mAdapter == null) {
			callback(uzContext, false);
			return;
		}

		if (index < 0 || index > mAdapter.getCount()) {
			callback(uzContext, false);
			return;
		}

		mAdapter.getItemDatas().remove(index);
		mAdapter.notifyDataSetChanged();

		callback(uzContext, true);
	}

	public void jsmethod_updateItem(UZModuleContext uzContext) {
		int index = uzContext.optInt("index");

		if (mAdapter == null) {
			callback(uzContext, false);
			return;
		}

		if (index < 0 || index > mAdapter.getCount()) {
			callback(uzContext, false);
			return;
		}

		if (!uzContext.isNull("data")) {
			JSONObject dataObj = uzContext.optJSONObject("data");
			ItemData data = new ItemData(dataObj);
			mAdapter.getItemDatas().set(index, data);
			mAdapter.notifyDataSetChanged();
			callback(uzContext, true);
		}
	}

	public void jsmethod_getCount(UZModuleContext uzContext) {

		if (mAdapter == null) {
			return;
		}
		JSONObject ret = new JSONObject();
		try {
			ret.put("count", mAdapter.getCount());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		uzContext.success(ret, false);

	}

	public void jsmethod_insertItem(UZModuleContext uzContext) {

		if (mAdapter == null) {
			callback(uzContext, false);
			return;
		}

		int index = mAdapter.getCount();
		if (!uzContext.isNull("index")) {
			index = uzContext.optInt("index");
		}

		if (index < 0 || index > mAdapter.getCount()) {
			callback(uzContext, false);
			return;
		}

		if (!uzContext.isNull("data")) {

			JSONObject dataObj = uzContext.optJSONObject("data");
			ItemData data = new ItemData(dataObj);
			mAdapter.getItemDatas().add(index, data);
			mAdapter.notifyDataSetChanged();

		}
		callback(uzContext, true);
	}

	public void jsmethod_appendData(UZModuleContext uzContext) {
		if (mAdapter == null) {
			callback(uzContext, false);
			return;
		}

		if (mRefreshableList != null) {
			mRefreshableList.onRefreshComplete();
		}
		// FIXME :
		if (!uzContext.isNull("data")) {
			JSONArray datas = uzContext.optJSONArray("data");
			for (int i = 0; i < datas.length(); i++) {
				ItemData itemData = new ItemData(datas.optJSONObject(i));
				mAdapter.getItemDatas().add(itemData);
			}
			mAdapter.notifyDataSetChanged();
			mAdapter.notifyDataSetInvalidated();

		}
		
		final SwipeMenuListView list = ((SwipeMenuListView) mRefreshableList.getRefreshableView());
		if(mFirstVisibleItem + 1 < mAdapter.getCount()){
			list.setSelection(mFirstVisibleItem + 1);
		}
		
		callback(uzContext, true);
	}

	private UZModuleContext pullDownContext;
	private UZModuleContext pullUpContext;

	private boolean showHeaderTime = true;
	private boolean showFooterTime = true;

	@SuppressWarnings("deprecation")
	public void jsmethod_setRefreshHeader(UZModuleContext uzContext) {

		pullDownContext = uzContext;

		if (mRefreshableList != null) {
			mRefreshableList.setMode(mRefreshableList.getMode() != Mode.DISABLED ? mRefreshableList.getMode() == Mode.PULL_FROM_START ? Mode.PULL_FROM_START : Mode.BOTH : Mode.PULL_FROM_START);
			String textDown = uzContext.optString("textDown");
			if (TextUtils.isEmpty(textDown)) {
				textDown = TEXT_DOWN;
			}
			String textUp = uzContext.optString("textUp");
			if (TextUtils.isEmpty(textUp)) {
				textUp = TEXT_UP;
			}
			String loadingImg = uzContext.optString("loadingImg");

			if (!uzContext.isNull("showTime")) {
				showHeaderTime = uzContext.optBoolean("showTime");
			}

			String bgColor = "#f5f5f5";

			if (!uzContext.isNull("bgColor")) {
				bgColor = uzContext.optString("bgColor");
			}

			String textColor = "#8e8e8e";

			if (!uzContext.isNull("textColor")) {
				textColor = uzContext.optString("textColor");
			}
			LoadingLayout loadingLayout = mRefreshableList.getHeaderLayout();
			loadingLayout.setPullLabel(textDown);
			loadingLayout.setReleaseLabel(textUp);
			loadingLayout.setRefreshingLabel(REFRESH_LABEL);

			BitmapDrawable bitmapDrawable = new BitmapDrawable(getBitmap(loadingImg));
			bitmapDrawable.setTargetDensity(mContext.getResources().getDisplayMetrics().densityDpi);
			loadingLayout.setLoadingDrawable(bitmapDrawable);

			loadingLayout.setBackgroundColor(UZUtility.parseCssColor(bgColor));
			loadingLayout.getHeaderText().setTextColor(UZUtility.parseCssColor(textColor));
			loadingLayout.getSubHeaderText().setTextColor(UZUtility.parseCssColor(textColor));

			if (refreshListener == null) {
				refreshListener = new OnRefreshListener();
				mRefreshableList.setOnRefreshListener(refreshListener);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void jsmethod_setRefreshFooter(UZModuleContext uzContext) {

		pullUpContext = uzContext;

		if (mRefreshableList != null) {
			mRefreshableList.setMode(mRefreshableList.getMode() != Mode.DISABLED ? mRefreshableList.getMode() == Mode.PULL_FROM_END ? Mode.PULL_FROM_END : Mode.BOTH : Mode.PULL_FROM_END);
			String textDown = uzContext.optString("textDown");
			if (TextUtils.isEmpty(textDown)) {
				textDown = TEXT_DOWN;
			}
			String textUp = uzContext.optString("textUp");
			if (TextUtils.isEmpty(textUp)) {
				textUp = TEXT_UP;
			}
			String loadingImg = uzContext.optString("loadingImg");

			if (!uzContext.isNull("showTime")) {
				showFooterTime = uzContext.optBoolean("showTime");
			}

			String bgColor = "#f5f5f5";

			if (!uzContext.isNull("bgColor")) {
				bgColor = uzContext.optString("bgColor");
			}

			String textColor = "#8e8e8e";

			if (!uzContext.isNull("textColor")) {
				textColor = uzContext.optString("textColor");
			}

			LoadingLayout loadingLayout = mRefreshableList.getFooterLayout();
			loadingLayout.setPullLabel(textUp);
			loadingLayout.setReleaseLabel(textDown);
			loadingLayout.setRefreshingLabel(REFRESH_LABEL);

			BitmapDrawable bitmapDrawable = new BitmapDrawable(getBitmap(loadingImg));
			bitmapDrawable.setTargetDensity(mContext.getResources().getDisplayMetrics().densityDpi);
			loadingLayout.setLoadingDrawable(bitmapDrawable);

			loadingLayout.setBackgroundColor(UZUtility.parseCssColor(bgColor));
			loadingLayout.getHeaderText().setTextColor(UZUtility.parseCssColor(textColor));
			loadingLayout.getSubHeaderText().setTextColor(UZUtility.parseCssColor(textColor));

			if (refreshListener == null) {
				refreshListener = new OnRefreshListener();
				mRefreshableList.setOnRefreshListener(refreshListener);
			}
			// callback(uzContext ,true);
		}
	}

	public void jsmethod_hide(UZModuleContext uzContext) {
		if (mRefreshableList != null) {
			mRefreshableList.setVisibility(View.GONE);
		}
	}

	public void jsmethod_show(UZModuleContext uzContext) {
		if (mRefreshableList != null) {
			mRefreshableList.setVisibility(View.VISIBLE);
		}
	}

	public void jsmethod_close(UZModuleContext moduleContext) {
		removeViewFromCurWindow(mRefreshableList);
		mRefreshableList = null;
	}

	@Override
	protected void onClean() {
		super.onClean();
	}

	public Bitmap getBitmap(String path) {
		InputStream input = null;
		Bitmap mBitmap = null;
		if (!TextUtils.isEmpty(path)) {
			String iconPath = makeRealPath(path);
			try {
				input = UZUtility.guessInputStream(iconPath);
				mBitmap = BitmapFactory.decodeStream(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Android SDK Content Loader
		}
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mBitmap;
	}
}