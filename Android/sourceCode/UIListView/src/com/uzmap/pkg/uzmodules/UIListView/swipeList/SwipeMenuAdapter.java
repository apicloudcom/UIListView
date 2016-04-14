package com.uzmap.pkg.uzmodules.UIListView.swipeList;

import java.util.ArrayList;

import com.uzmap.pkg.uzmodules.UIListView.data.ItemData;
import com.uzmap.pkg.uzmodules.UIListView.swipeList.SwipeMenuListView.OnMenuItemClickListener;
import com.uzmap.pkg.uzmodules.UIListView.swipeList.SwipeMenuView.OnSwipeItemClickListener;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

/**
 * @author baoyz
 * @date 2014-8-24
 */
public class SwipeMenuAdapter implements WrapperListAdapter, OnSwipeItemClickListener {

	private ListAdapter mAdapter;
	private Context mContext;
	private OnMenuItemClickListener onMenuItemClickListener;

	public SwipeMenuAdapter(Context context, ListAdapter adapter) {
		mAdapter = adapter;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mAdapter.getCount();
	}

	@Override
	public Object getItem(int position) {
		return mAdapter.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return mAdapter.getItemId(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SwipeMenuLayout layout = null;
		// if (convertView == null) {
		
			Log.i("UIListView", "SwipeMenuAdapter getView excute ... ");

			View contentView = mAdapter.getView(position, convertView, parent);
			SwipeMenu menu = new SwipeMenu(mContext);
			menu.setViewType(mAdapter.getItemViewType(position));

			createMenu(menu);

			com.uzmap.pkg.uzmodules.UIListView.adapter.ListAdapter adapter = (com.uzmap.pkg.uzmodules.UIListView.adapter.ListAdapter) mAdapter;
			ArrayList<ItemData> itemDatas = adapter.getItemDatas();

			SwipeMenuView menuView = null;
			if (itemDatas != null && itemDatas.size() > 0 && itemDatas.get(position) != null) {
				menuView = new SwipeMenuView(mContext, itemDatas.get(position).rightBtns);
				menuView.setOnSwipeItemClickListener(this);
			}

			/**
			 * new added code
			 */
			SwipeMenu leftMenu = new SwipeMenu(mContext);
			createLeftMenu(leftMenu);
			SwipeMenuView leftMenuView = new SwipeMenuView(leftMenu, (SwipeMenuListView) parent);
			leftMenuView.setOnSwipeItemClickListener(this);

			SwipeMenuListView listView = (SwipeMenuListView) parent;
			layout = new SwipeMenuLayout(contentView, menuView, leftMenuView, listView.getCloseInterpolator(), listView.getOpenInterpolator());

			layout.setPosition(position);
			
		/*} else {
			
			layout = (SwipeMenuLayout) convertView;
			layout.closeMenu();
			layout.setPosition(position);
			View view = mAdapter.getView(position, layout.getContentView(), parent);
		}*/
		return layout;
	}

	public void createMenu(SwipeMenu menu, ItemData itemData) {
		
	}

	public void createMenu(SwipeMenu menu) {
		// Test Code
		SwipeMenuItem item = new SwipeMenuItem(mContext);
		item.setTitle("Item 1");
		item.setBackground(new ColorDrawable(Color.GRAY));
		item.setWidth(300);
		menu.addMenuItem(item);

		item = new SwipeMenuItem(mContext);
		item.setTitle("Item 2");
		item.setBackground(new ColorDrawable(Color.RED));
		item.setWidth(300);
		menu.addMenuItem(item);
	}

	public void createLeftMenu(SwipeMenu menu) {
		// Test Code
		SwipeMenuItem item = new SwipeMenuItem(mContext);
		item.setTitle("Item 1");
		item.setBackground(new ColorDrawable(Color.GRAY));
		item.setWidth(300);
		menu.addMenuItem(item);

		item = new SwipeMenuItem(mContext);
		item.setTitle("Item 2");
		item.setBackground(new ColorDrawable(Color.RED));
		item.setWidth(300);
		menu.addMenuItem(item);
	}

	@Override
	public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
		if (onMenuItemClickListener != null) {
			onMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
		}
	}

	public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
		this.onMenuItemClickListener = onMenuItemClickListener;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		mAdapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mAdapter.unregisterDataSetObserver(observer);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return mAdapter.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(int position) {
		return mAdapter.isEnabled(position);
	}

	@Override
	public boolean hasStableIds() {
		return mAdapter.hasStableIds();
	}

	@Override
	public int getItemViewType(int position) {
		return mAdapter.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		return mAdapter.getViewTypeCount();
	}

	@Override
	public boolean isEmpty() {
		return mAdapter.isEmpty();
	}

	@Override
	public ListAdapter getWrappedAdapter() {
		return mAdapter;
	}

}
