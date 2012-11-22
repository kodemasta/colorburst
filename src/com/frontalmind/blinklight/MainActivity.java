package com.frontalmind.blinklight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener{

	public static final String SHARED_PREFS_NAME = "com.frontalmind.colorburst";

	protected PowerManager.WakeLock wakeLock;
	private ColorGridView colorGridView;
	private SharedPreferences mPrefs = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
				"BlinkLight Tag");
		this.wakeLock.acquire();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
        mPrefs = MainActivity.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
        mPrefs.registerOnSharedPreferenceChangeListener(this);

		colorGridView = new ColorGridView(this);
		setContentView(colorGridView);
		colorGridView.model.enableAnimation(true);
		loadPref();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	
	@Override
	protected void onDestroy() {
		this.wakeLock.release();
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
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
		mPrefs.edit().commit();

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
		this.colorGridView.model.enableAnimation(false);
		int animationRate = mPrefs.getInt("pref_rate", 50);
		if (this.colorGridView != null)
			this.colorGridView.model.setRate(animationRate);
		
		int blockSize = mPrefs.getInt("pref_block_size", 50);
		if (this.colorGridView != null)
			this.colorGridView.model.setBlockSize(blockSize);
		
		String colorRange = mPrefs.getString("color_preference", "All");
		if (this.colorGridView != null)
			this.colorGridView.model.setColorRange(colorRange);

		String borderColorRange = mPrefs.getString("border_color_preference", "All");
		if (this.colorGridView != null)
			this.colorGridView.model.setBorderColorRange(borderColorRange);

		int decayStep = mPrefs.getInt("pref_decay", 8);
		if (this.colorGridView != null)
			this.colorGridView.model.setDecayStep(decayStep);
	
		int strokeWidth = mPrefs.getInt("pref_stroke_width", 2);
		if (this.colorGridView != null)
			this.colorGridView.model.setStrokeWidth(strokeWidth);
		
		int threshold = mPrefs.getInt("pref_min_alpha", 0);
		if (this.colorGridView != null)
			this.colorGridView.model.setThreshold(threshold);
		
		int padding = mPrefs.getInt("pref_padding", 2);
		if (this.colorGridView != null)
			this.colorGridView.model.setPadding(padding);
		
		String shape = mPrefs.getString("pref_shape", "hexagon");
		if (this.colorGridView != null)
			this.colorGridView.model.setShape(shape);

		int fillAlpha = mPrefs.getInt("pref_fill_alpha", 0);
		if (this.colorGridView != null)
			this.colorGridView.model.setFillAlpha(fillAlpha);

		int strokeAlpha = mPrefs.getInt("pref_stroke_alpha", 0);
		if (this.colorGridView != null)
			this.colorGridView.model.setStrokeAlpha(strokeAlpha);


		mPrefs.edit().commit();
		this.colorGridView.model.createGrid();
		this.colorGridView.model.enableAnimation(true);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		loadPref();	
	}
}
