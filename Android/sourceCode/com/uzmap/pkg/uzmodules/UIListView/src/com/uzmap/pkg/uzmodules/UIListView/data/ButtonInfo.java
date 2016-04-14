/**
 * APICloud Modules
 * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */
package com.uzmap.pkg.uzmodules.UIListView.data;

import com.uzmap.pkg.uzkit.UZUtility;

public class ButtonInfo {

	/**
	 * the background of button
	 */
	public int bgColor = 0xFF388e8e;

	/**
	 * the button width
	 */
	public int btnWidth = Config.SCREEN_WIDTH / 4;

	/**
	 * the title of the button
	 */
	public String title;

	/**
	 * the title size of button
	 */
	public int titleSize = 12;

	/**
	 * the tile color of the button
	 */
	public int titleColor = 0xFFFFFFFF;

	/**
	 * the button selected color
	 */
	public int activeBgColor = 0xFFFFFFFF;

	/**
	 * the icon path of the title left
	 */
	public String icon;

	public int iconWidth = UZUtility.dipToPix(20);

}
