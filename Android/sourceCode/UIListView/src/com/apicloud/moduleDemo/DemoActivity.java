package com.apicloud.moduleDemo;

import org.json.JSONException;
import org.json.JSONObject;

import com.uzmap.pkg.uzcore.UZResourcesIDFinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 使用APICloud模块扩展机制进行模块扩展时，不允许直接使用R文件进行资源文件的引用，若想获取资源Id，可通过UZResourcesIDFinder下的函数实现
 *
 */
public class DemoActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//使用UZResourcesIDFinder.get_XXX_Id动态获取资源ID
		int layoutId = UZResourcesIDFinder.getResLayoutID("mo_demo_main_activity");
		if(layoutId > 0){
			setContentView(layoutId);
		}else {
			Toast.makeText(this, "名为：demo_activity_result的布局文件不存在!\n亲检查您的代码", Toast.LENGTH_LONG).show();
			return;
		}
		Intent data = getIntent();
		String appParam = data.getStringExtra("appParam");
		if(null != appParam){
			int textId = UZResourcesIDFinder.getResIdID("text");
			if(textId == 0){
				Toast.makeText(this, "Id为：text的TextView不存在！\n请检查您的代码", Toast.LENGTH_LONG).show();
			}else{
				TextView text = (TextView) findViewById(textId);
				text.setText("JS传入的参数为：" + appParam);
			}
		}
		int btnId = UZResourcesIDFinder.getResIdID("btn");
		if(btnId == 0){
			Toast.makeText(this, "Id为：btn的Button不存在！\n请检查您的代码", Toast.LENGTH_LONG).show();
		}else{
			final boolean needResult = data.getBooleanExtra("needResult", false);
			Button btn = (Button) findViewById(btnId);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(needResult){
						Intent resultData = new Intent();
						JSONObject json = new JSONObject();
						try {
							json.put("key1", "value1");
							json.put("key2", "value2");
							json.put("key3", "value3");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						resultData.putExtra("result", json.toString());
						setResult(RESULT_OK, resultData);
					};
					finish();
				}
			});
		}
	}
}
