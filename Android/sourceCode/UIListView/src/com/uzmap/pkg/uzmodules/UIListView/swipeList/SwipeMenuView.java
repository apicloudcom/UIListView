package com.uzmap.pkg.uzmodules.UIListView.swipeList;

import java.util.ArrayList;
import java.util.List;

import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzkit.UZUtility;
import com.uzmap.pkg.uzmodules.UIListView.ViewUtils.ViewUtil;
import com.uzmap.pkg.uzmodules.UIListView.constants.Constants;
import com.uzmap.pkg.uzmodules.UIListView.data.ButtonInfo;
import com.uzmap.pkg.uzmodules.UIListView.data.Utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author baoyz
 * @date 2014-8-23
 * 
 */
public class SwipeMenuView extends LinearLayout implements OnClickListener {

	private SwipeMenuLayout mLayout;
	private SwipeMenu mMenu;
	private OnSwipeItemClickListener onItemClickListener;
	private int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public SwipeMenuView(SwipeMenu menu, SwipeMenuListView listView) {
		super(menu.getContext());
		
		mMenu = menu;
		List<SwipeMenuItem> items = menu.getMenuItems();
		int id = 0;
		for (SwipeMenuItem item : items) {
			addItem(item, id++);
		}
	}
	
	public SwipeMenuView(Context context, ArrayList<ButtonInfo> btns) {
		super(context);
		int id = 0;
		
		if(btns == null){
			return;
		}
		
		this.removeAllViews();

		for (ButtonInfo btn : btns) {
			addItem(btn, id++);
		}
	}

	@SuppressWarnings("deprecation")
	public void addItem(ButtonInfo info , int id){
		
		/**
		 * the container layoutParam
		 */
		LinearLayout.LayoutParams containerParam = new LinearLayout.LayoutParams(info.btnWidth ,LayoutParams.MATCH_PARENT);
		
		int containerViewId = UZResourcesIDFinder.getResLayoutID("menu_item_layout");
		View containerView = View.inflate(getContext(),containerViewId , null);
		containerView.setOnClickListener(this);
		containerView.setId(id);
		
		/**
		 * set Bg & layoutParam
		 */
		containerView.setBackgroundDrawable(ViewUtil.addStateDrawable(info.bgColor, info.activeBgColor));
		containerView.setLayoutParams(containerParam);
		
		/**
		 * item icon
		 */
		if(!TextUtils.isEmpty(info.icon)){
			
			int itemIconId = UZResourcesIDFinder.getResIdID("menu_item_icon");
			ImageView itemIcon = (ImageView)containerView.findViewById(itemIconId);
			itemIcon.setVisibility(View.VISIBLE);
			
			LinearLayout.LayoutParams itemIconParams = new LinearLayout.LayoutParams(info.iconWidth ,info.iconWidth);
			itemIconParams.gravity = Gravity.CENTER_VERTICAL;
			itemIconParams.rightMargin = UZUtility.dipToPix(5);
			itemIcon.setLayoutParams(itemIconParams);
			if(Constants.WIDGET_INFO != null){
				itemIcon.setImageBitmap(Utils.getBitmapFromLocal(info.icon, Constants.WIDGET_INFO));
			}
			
		}
		
		/**
		 * item textView
		 */
		if(!TextUtils.isEmpty(info.title)){
			int titleTxtId = UZResourcesIDFinder.getResIdID("menu_item_text");
			TextView itemText = (TextView)containerView.findViewById(titleTxtId);
			itemText.setVisibility(View.VISIBLE);
			itemText.setText(info.title);
			itemText.setTextColor(info.titleColor);
			itemText.setTextSize(info.titleSize);
		}
		
		this.addView(containerView);
		
	}

	@SuppressWarnings("deprecation")
	private void addItem(SwipeMenuItem item, int id) {

		LayoutParams params = new LayoutParams(item.getWidth(), LayoutParams.MATCH_PARENT);
		LinearLayout parent = new LinearLayout(getContext());

		parent.setId(id);

		parent.setGravity(Gravity.CENTER);
		parent.setOrientation(LinearLayout.VERTICAL);
		parent.setLayoutParams(params);
		parent.setBackgroundDrawable(item.getBackground());
		parent.setOnClickListener(this);
		addView(parent);

		if (item.getIcon() != null) {
			parent.addView(createIcon(item));
		}
		if (!TextUtils.isEmpty(item.getTitle())) {
			parent.addView(createTitle(item));
		}

	}

	private ImageView createIcon(SwipeMenuItem item) {
		ImageView iv = new ImageView(getContext());
		iv.setImageDrawable(item.getIcon());
		return iv;
	}

	private TextView createTitle(SwipeMenuItem item) {
		TextView tv = new TextView(getContext());
		tv.setText(item.getTitle());
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(item.getTitleSize());
		tv.setTextColor(item.getTitleColor());
		return tv;
	}
	
	@Override
	public void onClick(View v) {
		if (onItemClickListener != null && mLayout.isOpen()) {
			onItemClickListener.onItemClick(this, mMenu, v.getId());
		}
	}

	public OnSwipeItemClickListener getOnSwipeItemClickListener() {
		return onItemClickListener;
	}

	public void setOnSwipeItemClickListener(OnSwipeItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	
	public void setLayout(SwipeMenuLayout mLayout) {
		this.mLayout = mLayout;
	}

	public static interface OnSwipeItemClickListener {
		void onItemClick(SwipeMenuView view, SwipeMenu menu, int index);
	}
	
}
