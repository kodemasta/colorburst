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
	private BitmapView view;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
				"BlinkLight Tag");
		this.wakeLock.acquire();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    

		view = new BitmapView(this);
		setContentView(view);
		view.enableAnimation(true);
		
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

		this.view.onPause();
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
		this.view.setRate(animationRate);
		
		int blockSize = sharedPreferences.getInt("pref_block_size", 50);
		this.view.setBlockSize(blockSize);
		
		String colorRange = sharedPreferences.getString("color_preference", "All");
		this.view.setColorRange(colorRange);
		
		int decayStep = sharedPreferences.getInt("pref_decay", 8);
		this.view.setDecayStep(decayStep);
		
		int threshold = sharedPreferences.getInt("pref_threshold", 0);
		this.view.setThreshold(threshold);
		
		int padding = sharedPreferences.getInt("pref_padding", 2);
		this.view.setPadding(padding);
		
		String shape = sharedPreferences.getString("pref_shape", "rect");
		this.view.setShape(shape);
		
		sharedPreferences.edit().commit();
	}
}
