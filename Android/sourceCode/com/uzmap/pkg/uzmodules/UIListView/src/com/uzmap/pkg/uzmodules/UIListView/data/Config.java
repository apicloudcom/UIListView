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

import android.content.Context;
import android.text.TextUtils;

import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;
import com.uzmap.pkg.uzmodules.UIListView.ViewUtils.ViewUtil;

public class Config {

	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	
	// Rect_
	public int x = 0;
	public int y = 0;
	public int w = 320;
	public int h = 480;

	public ArrayList<ItemData> itemDatas;

	// styles
	public int leftBgColor = 0xFF5cacee;
	public int rightBgColor = 0xFF6c7b8b;
	public int borderColor = 0xFF696969;
	public int borderWidth = 1;

	// item style
	public int itemBgColor = 0xFFAFEEEE;
	public int itemActiveBgColor = 0xFFF5F5F5;
	public int itemHeight = 55;
	public int itemImgWidth = itemHeight - 10;

	public int itemImgHeight = 40;
	public String itemPlaceholderImg = "";
	public String itemTitleAlign = "left";

	public int itemTitleSize = 12;
	public int itemTitleColor = 0xFF000000;

	public String itemSubTitleAlign = "left";
	public int itemSubTitleSize = 12;
	public int itemSubTitleColor = 0xFF000000;

	public int itemRemarkMargin = 10;
	public int itemRemarkColor = 0xFF000000;

	public int itemRemarkSize = 16;
	public int itemRemarkIconWidth = 30;
	
	public int itemImgCorner = 0;

	public boolean fixed = true;
	public String fixedOn = "";
	
	public ArrayList<ButtonInfo> rightBtns = new ArrayList<ButtonInfo>();

	public Config(UZModuleContext uzContext, Context context) {

		SCREEN_HEIGHT = ViewUtil.getScreenHeight(context);
		SCREEN_WIDTH = ViewUtil.getScreenWidth(context);

		JSONObject rectObj = uzContext.optJSONObject("rect");

		w = ViewUtil.getScreenWidth(context);
		h = ViewUtil.getScreenHeight(context);

		if (rectObj != null) {

			if (!rectObj.isNull("x")) {
				x = rectObj.optInt("x");
			}
			if (!rectObj.isNull("y")) {
				y = rectObj.optInt("y");
			}

			if (!rectObj.isNull("w")) {
				w = rectObj.optInt("w");
			}
			if (!rectObj.isNull("h")) {
				h = rectObj.optInt("h");
			}

		}

		if (!uzContext.isNull("fixedOn")) {
			fixedOn = uzContext.optString("fixedOn");
		}

		ArrayList<ButtonInfo> leftBtns = null;
		if (!uzContext.isNull("leftBtns") && uzContext.optJSONArray("leftBtns").length() > 0) {
			leftBtns = new ArrayList<ButtonInfo>();
			JSONArray array = uzContext.optJSONArray("leftBtns");
			for (int i = 0; i < array.length(); i++) {
				ButtonInfo btnInfo = Utils.parseBtnInfo(array.optJSONObject(i));
				leftBtns.add(btnInfo);
			}
		}

		
		if (!uzContext.isNull("rightBtns") && uzContext.optJSONArray("rightBtns").length() > 0) {
			rightBtns = new ArrayList<ButtonInfo>();
			JSONArray array = uzContext.optJSONArray("rightBtns");
			for (int i = 0; i < array.length(); i++) {
				ButtonInfo btnInfo = Utils.parseBtnInfo(array.optJSONObject(i));
				rightBtns.add(btnInfo);
			}
		}

		JSONArray dataArray = uzContext.optJSONArray("data");
		if (dataArray != null) {
			itemDatas = new ArrayList<ItemData>();
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject itemDataObj = dataArray.optJSONObject(i);
				ItemData itemData = new ItemData(itemDataObj);
				if (itemData != null && itemData.leftBtns.size() == 0) {
					itemData.setLeftBtns(leftBtns);
				}
				if (itemData != null && itemData.rightBtns.size() == 0) {
					itemData.setRightBtns(rightBtns);
				}
				itemDatas.add(itemData);
			}
		}

		JSONObject stylesObj = uzContext.optJSONObject("styles");
		if (stylesObj != null) {

			if (!stylesObj.isNull("leftBgColor") && !TextUtils.isEmpty(stylesObj.optString("leftBgColor"))) {
				leftBgColor = UZUtility.parseCssColor(stylesObj.optString("leftBgColor"));
			}

			if (!stylesObj.isNull("rightBgColor") && !TextUtils.isEmpty(stylesObj.optString("rightBgColor"))) {
				rightBgColor = UZUtility.parseCssColor(stylesObj.optString("rightBgColor"));
			}

			if (!stylesObj.isNull("borderColor") && !TextUtils.isEmpty(stylesObj.optString("borderColor"))) {
				borderColor = UZUtility.parseCssColor(stylesObj.optString("borderColor"));
			}

			if (!stylesObj.isNull("borderWidth")) {
				borderWidth = stylesObj.optInt("borderWidth");
			}

			JSONObject itemStyleObj = stylesObj.optJSONObject("item");
			if (itemStyleObj != null) {

				if (!itemStyleObj.isNull("bgColor") && !TextUtils.isEmpty(itemStyleObj.optString("bgColor"))) {
					itemBgColor = UZUtility.parseCssColor(itemStyleObj.optString("bgColor"));
				}
				itemActiveBgColor = itemBgColor;
				if (!itemStyleObj.isNull("activeBgColor") && !TextUtils.isEmpty(itemStyleObj.optString("activeBgColor"))) {
					itemActiveBgColor = UZUtility.parseCssColor(itemStyleObj.optString("activeBgColor"));
				}

				if (!itemStyleObj.isNull("height")) {
					itemHeight = itemStyleObj.optInt("height");
				}

				if (!itemStyleObj.isNull("imgWidth")) {
					itemImgWidth = itemStyleObj.optInt("imgWidth");
				}

				if (!itemStyleObj.isNull("imgHeight")) {
					itemImgHeight = itemStyleObj.optInt("imgHeight");
				}
				
				if(!itemStyleObj.isNull("imgCorner")){
					itemImgCorner = UZUtility.dipToPix(itemStyleObj.optInt("imgCorner"));
				}

				if (!itemStyleObj.isNull("placeholderImg") && !TextUtils.isEmpty(itemStyleObj.optString("placeholderImg"))) {
					itemPlaceholderImg = itemStyleObj.optString("placeholderImg");
				}

				if (!itemStyleObj.isNull("titleAlign") && !TextUtils.isEmpty(itemStyleObj.optString("titleAlign"))) {
					itemTitleAlign = itemStyleObj.optString("titleAlign");
				}

				if (!itemStyleObj.isNull("titleSize")) {
					itemTitleSize = itemStyleObj.optInt("titleSize");
				}

				if (!itemStyleObj.isNull("titleColor") && !TextUtils.isEmpty(itemStyleObj.optString("titleColor"))) {
					itemTitleColor = UZUtility.parseCssColor(itemStyleObj.optString("titleColor"));
				}

				if (!itemStyleObj.isNull("subTitleAlign") && !TextUtils.isEmpty(itemStyleObj.optString("subTitleAlign"))) {
					itemSubTitleAlign = itemStyleObj.optString("subTitleAlign");
				}

				if (!itemStyleObj.isNull("subTitleSize")) {
					itemSubTitleSize = itemStyleObj.optInt("subTitleSize");
				}

				if (!itemStyleObj.isNull("subTitleColor") && !TextUtils.isEmpty(itemStyleObj.optString("subTitleColor"))) {
					itemSubTitleColor = UZUtility.parseCssColor(itemStyleObj.optString("subTitleColor"));
				}

				if (!itemStyleObj.isNull("remarkMargin")) {
					itemRemarkMargin = itemStyleObj.optInt("remarkMargin");
				}

				if (!itemStyleObj.isNull("remarkColor") && !TextUtils.isEmpty(itemStyleObj.optString("remarkColor"))) {
					itemRemarkColor = UZUtility.parseCssColor(itemStyleObj.optString("remarkColor"));
				}

				if (!itemStyleObj.isNull("remarkSize")) {
					itemRemarkSize = itemStyleObj.optInt("remarkSize");
				}

				if (!itemStyleObj.isNull("remarkIconWidth")) {
					itemRemarkIconWidth = itemStyleObj.optInt("remarkIconWidth");
				}
			}

		}

	}
}
