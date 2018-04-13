/**
 * APICloud Modules
 * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */
package com.uzmap.pkg.uzmodules.UIListView.data;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class ItemData {

	public String imgPath;
	public String title;
	public String subTitle;
	public String remark;
	public String icon;
	
	public boolean forbidden;

	public ArrayList<ButtonInfo> leftBtns = new ArrayList<ButtonInfo>();
	public ArrayList<ButtonInfo> rightBtns = new ArrayList<ButtonInfo>();

	public JSONObject itemObj;

	public ItemData(JSONObject itemObj) {

		if (itemObj == null) {
			return;
		}

		this.itemObj = itemObj;

		if (!itemObj.isNull("imgPath")) {
			imgPath = itemObj.optString("imgPath");
		}

		if (!itemObj.isNull("title")) {
			title = itemObj.optString("title");
		}

		if (!itemObj.isNull("subTitle")) {
			subTitle = itemObj.optString("subTitle");
		}

		if (!itemObj.isNull("remark")) {
			remark = itemObj.optString("remark");
		}

		if (!itemObj.isNull("icon")) {
			icon = itemObj.optString("icon");
		}
		
		if(!itemObj.isNull("forbidden")){
			forbidden = itemObj.optBoolean("forbidden");
		}

		if (!itemObj.isNull("leftBtns") && itemObj.optJSONArray("leftBtns").length() > 0) {
			JSONArray array = itemObj.optJSONArray("leftBtns");
			for (int i = 0; i < array.length(); i++) {
				ButtonInfo btnInfo = Utils.parseBtnInfo(array.optJSONObject(i));
				leftBtns.add(btnInfo);
			}
		}

		if (!itemObj.isNull("rightBtns") && itemObj.optJSONArray("rightBtns").length() > 0) {
			JSONArray array = itemObj.optJSONArray("rightBtns");
			for (int i = 0; i < array.length(); i++) {
				ButtonInfo btnInfo = Utils.parseBtnInfo(array.optJSONObject(i));
				rightBtns.add(btnInfo);
			}
		}

	}

	public void setLeftBtns(ArrayList<ButtonInfo> leftBtns) {
		this.leftBtns = leftBtns;
	}

	public void setRightBtns(ArrayList<ButtonInfo> rightBtns) {
		this.rightBtns = rightBtns;
	}
}
