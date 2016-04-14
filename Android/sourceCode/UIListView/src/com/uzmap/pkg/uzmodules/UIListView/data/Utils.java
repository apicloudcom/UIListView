/**
 * APICloud Modules
 * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */
package com.uzmap.pkg.uzmodules.UIListView.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;
import com.uzmap.pkg.uzkit.data.UZWidgetInfo;

public class Utils {

	public static ButtonInfo parseBtnInfo(JSONObject btnJson) {

		ButtonInfo btnInfo = new ButtonInfo();

		if (!btnJson.isNull("bgColor") && !TextUtils.isEmpty(btnJson.optString("bgColor"))) {
			btnInfo.bgColor = UZUtility.parseCssColor(btnJson.optString("bgColor"));
		}
		if (!btnJson.isNull("width")) {
			btnInfo.btnWidth = UZUtility.dipToPix(btnJson.optInt("width"));
		}
		if (!btnJson.isNull("title") && !TextUtils.isEmpty(btnJson.optString("title"))) {
			btnInfo.title = btnJson.optString("title");
		}
		if (!btnJson.isNull("titleSize")) {
			btnInfo.titleSize = btnJson.optInt("titleSize");
		}

		if (!btnJson.isNull("titleColor") && !TextUtils.isEmpty(btnJson.optString("titleColor"))) {
			btnInfo.titleColor = UZUtility.parseCssColor(btnJson.optString("titleColor"));
		}

		if (!btnJson.isNull("activeBgColor") && !TextUtils.isEmpty(btnJson.optString("activeBgColor"))) {
			btnInfo.activeBgColor = UZUtility.parseCssColor(btnJson.optString("activeBgColor"));
		}

		if (!btnJson.isNull("icon") && !TextUtils.isEmpty(btnJson.optString("icon"))) {
			btnInfo.icon = btnJson.optString("icon");
		}

		if (!btnJson.isNull("iconWidth")) {
			btnInfo.iconWidth = UZUtility.dipToPix(btnJson.optInt("iconWidth"));
		}

		return btnInfo;
	}

	public static final int PATH_IS_LOCAL = 0x000;
	public static final int PATH_IS_HTTP = 0x001;
	public static final int PATH_INVALIDATE = 0x002;

	public static int checkPath(String path) {

		if (TextUtils.isEmpty(path)) {
			return PATH_INVALIDATE;
		}

		Pattern pattern = Pattern.compile("^(http)://.+\\..+\\..+$");
		Matcher matcher = pattern.matcher(path);

		if (matcher.matches()) {
			return PATH_IS_HTTP;
		}

		pattern = Pattern.compile("^(widget|fs)://.+$");
		matcher = pattern.matcher(path);

		if (matcher.matches()) {
			return PATH_IS_LOCAL;
		}

		pattern = Pattern.compile("^/.+$");
		matcher = pattern.matcher(path);

		if (matcher.matches()) {
			return PATH_IS_LOCAL;
		}
		return PATH_INVALIDATE;
	}

	public static Bitmap getBitmapFromLocal(String path, UZWidgetInfo wInfo) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}

		String realPath = UZUtility.makeRealPath(path, wInfo);
		InputStream input = null;
		Bitmap bitmap = null;
		try {
			input = UZUtility.guessInputStream(realPath);
			bitmap = BitmapFactory.decodeStream(input);
			if (input != null) {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static void callback(UZModuleContext uzContext, String eventType, int index, int btnIndex) {

		JSONObject ret = new JSONObject();
		try {
			ret.put("eventType", eventType);
			if (index >= 0) {
				ret.put("index", index);
			}
			if (btnIndex >= 0) {
				ret.put("btnIndex", btnIndex);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		uzContext.success(ret, false);
	}
}
