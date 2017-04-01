package com.uzmap.pkg.uzmodules.UIListView.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 自定义的圆角矩形ImageView，可以直接当组件在布局中使用。
 * 
 * @author caizhiming
 * 
 */
public class CircleImageView extends ImageView {

	private Paint paint;

	private Bitmap roundedBitmap;
	private int coner = 20;

	public CircleImageView(Context context) {
		this(context, null);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		paint = new Paint();
	}

	/**
	 * 绘制圆角矩形图片
	 * 
	 * @author caizhiming
	 */
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		if (null != roundedBitmap) {

			Bitmap b = getRoundBitmap(roundedBitmap, coner);
			paint.reset();
			canvas.drawBitmap(b, 0, 0, paint);

		} else {
			super.onDraw(canvas);
		}
	}

	/**
	 * 获取圆角矩形图片方法
	 * 
	 * @param bitmap
	 * @param roundPx
	 *            ,一般设置成14
	 * @return Bitmap
	 * @author caizhiming
	 */
	private Bitmap getRoundBitmap(Bitmap bitmap, int roundPx) {

		Bitmap output = Bitmap.createBitmap(getWidth(), getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;

		final Rect srcRect = new Rect(0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		final Rect descRect = new Rect(0, 0, getWidth(), getHeight());

		final Rect rect = new Rect(0, 0, getWidth(), getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, srcRect, descRect, paint);

		return output;
	}

	public void setWillRoundedBitmap(Bitmap roundedBitmap, int coner) {
		this.roundedBitmap = roundedBitmap;
		this.coner = coner;
		this.invalidate();
	}
}