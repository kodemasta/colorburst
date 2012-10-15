package com.frontalmind.blinklight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	protected PowerManager.WakeLock wakeLock;
	private ColorGridView colorGridView;
	private ColorShapeView colorShapeView;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
				"BlinkLight Tag");
		this.wakeLock.acquire();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    

		colorGridView = new ColorGridView(this);
		colorShapeView = new ColorShapeView(this);
		//setContentView(colorShapeView);
		setContentView(colorGridView);
		colorGridView.enableAnimation(true);
		
		loadPref();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	
	@Override
	protected void onDestroy() {
		this.wakeLock.release();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadPref();
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.edit().commit();

		if (this.colorGridView != null)
			this.colorGridView.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		/*
		 * Because it's onlt ONE option in the menu. In order to make it simple,
		 * We always start SetPreferenceActivity without checking.
		 */

		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SettingsActivity.class);
		startActivityForResult(intent, 0);

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		/*
		 * To make it simple, always re-load Preference setting.
		 */

		//loadPref();
	}

	private void loadPref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		int animationRate = sharedPreferences.getInt("pref_rate", 50);
		if (this.colorGridView != null)
		this.colorGridView.setRate(animationRate);
		
		int blockSize = sharedPreferences.getInt("pref_block_size", 50);
		if (this.colorGridView != null)
		this.colorGridView.setBlockSize(blockSize);
		
		String colorRange = sharedPreferences.getString("color_preference", "All");
		if (this.colorGridView != null)
			this.colorGridView.setColorRange(colorRange);
		
		int decayStep = sharedPreferences.getInt("pref_decay", 8);
		if (this.colorGridView != null)
			this.colorGridView.setDecayStep(decayStep);
	
		int strokeWidth = sharedPreferences.getInt("pref_stroke_width", 3);
		if (this.colorGridView != null)
			this.colorGridView.setStrokeWidth(strokeWidth);
		
		int threshold = sharedPreferences.getInt("pref_threshold", 0);
		if (this.colorGridView != null)
			this.colorGridView.setThreshold(threshold);
		
		int padding = sharedPreferences.getInt("pref_padding", 2);
		if (this.colorGridView != null)
			this.colorGridView.setPadding(padding);
		
		String shape = sharedPreferences.getString("pref_shape", "rect");
		if (this.colorGridView != null)
			this.colorGridView.setShape(shape);
		
		sharedPreferences.edit().commit();
	}
}
