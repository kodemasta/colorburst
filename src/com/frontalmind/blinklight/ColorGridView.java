/**
 * 
 */
package com.frontalmind.blinklight;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import com.frontalmind.ColorGrid;

/**
 * @author bob
 *
 */
public class ColorGridView extends View {

	private ColorGrid colorGrid;
	
	private int  animationRate;
	private Timer timer;
	private int viewWidth, viewHeight;
	private boolean enableDraw = false;


	
	public ColorGridView(Context context) {
		super(context);
		
		colorGrid = new ColorGrid();
		animationRate = 50;

		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP){
					enableDraw = false;
					enableAnimation(true);
					//colorGrid.setLocked
					//for (StrokeAndFillDrawable shape : shapes)
						//shape.setLock(false);
				}	
				else if (event.getAction() == MotionEvent.ACTION_DOWN) {
					enableDraw = true;
					enableAnimation(false);
				}
				else if (event.getAction() == MotionEvent.ACTION_MOVE) {
// drawing code on hold for now
//					float posX = event.getX();
//					float posY = event.getY();
//					
//					int i = (int)((posX - (float)offsetX)/(float)incr);
//					int j = (int)((posY - (float)offsetY)/(float)incr);
//					
//					int index = j + i*numY;
//					StrokeAndFillDrawable shape = shapes.get(index);
//					shape.setEnable(true);
//					shape.setLock(true);

				}
				return true;
			}
		});

		setBlockSize(50);

	}
	
	public void enableAnimation(boolean enable){
		if (timer != null){
			timer.cancel();
			timer.purge();
			timer = null;
		}
		if (enable){
    		timer = new Timer();
    		timer.scheduleAtFixedRate(new ColorTask(), 0, animationRate);
    	}
	}

	   
	class ColorTask extends TimerTask {
		private Handler updateUI = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				colorGrid.updateColors();
				ColorGridView.this.invalidate();
			}	
		};

		@Override
		public void run() {
			try {
				updateUI.sendEmptyMessage(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	 };

	 @Override
	 protected void onSizeChanged(int w, int h, int oldw, int oldh)
	 {
		 this.viewWidth = w;
		 this.viewHeight = h;
		 colorGrid.createGrid(w,h);
	 }

	@Override
	protected void onDraw(Canvas canvas) {
		 colorGrid.draw(canvas);
	}
	
	public void onResume() 
	{
		//enableAnimation(true);
	}


	public void onPause() 
	{
		enableAnimation(false);
	}

	public void setRate(int animationRate) {
		this.animationRate = animationRate;
		enableAnimation(true);
		
	}

	public void setBlockSize(int blockSize) {
		colorGrid.setBlockSize(blockSize);
	}
	
	public void createGrid() {
		colorGrid.createGrid(this.viewWidth, this.viewHeight);
	}

	public void setColorRange(String colorRange) {
		colorGrid.setColorRange(colorRange);
	}

	public void setDecayStep(int decayStep) {
		colorGrid.setDecayStep(decayStep);
	}

	public void setThreshold(int threshold) {
		colorGrid.setThreshold(threshold);
	}

	public void setPadding(int padding) {
		colorGrid.setPadding(padding);
	}

	public void setShape(String shape) {
		colorGrid.setShape(shape);
	}

	public void setStrokeWidth(int strokeWidth) {
		colorGrid.setStrokeWidth(strokeWidth);
	}

	public void setFillAlpha(int fillAlpha) {
		colorGrid.setFillAlpha(fillAlpha);
	}
	
	public void setStrokeAlpha(int strokeAlpha) {
		colorGrid.setStrokeAlpha(strokeAlpha);
	}
}
