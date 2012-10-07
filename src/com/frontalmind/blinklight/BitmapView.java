/**
 * 
 */
package com.frontalmind.blinklight;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author bob
 *
 */
public class BitmapView extends View {

	private Random random;
	private int incr, animationRate, viewWidth, viewHeight, blockSize, 
	numX, numY, offsetX, offsetY, padding, decay, threshold;
	private List<Bitmap> bitmaps;
	private List<Integer> colors;
	private Timer timer;
	private boolean toggleAnimate = true;
	String colorRange;
	
	public BitmapView(Context context) {
		super(context);
		blockSize = 50;
		padding = 2;
		animationRate = 50;
		decay = 8;
		threshold = 0;
		
		this.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
            	if (event.getAction() == MotionEvent.ACTION_DOWN){
               		toggleAnimate = !toggleAnimate;
               	    enableAnimation(toggleAnimate);
            	}
                return true;
            }
       });

		bitmaps = new LinkedList<Bitmap>();
		colors = new LinkedList<Integer>();
		random = new Random();		
	}
	
	public void enableAnimation(boolean enable){
		if (timer != null){
			timer.cancel();
			timer.purge();
		}
		if (enable){
    		timer = new Timer();
    		timer.scheduleAtFixedRate(new ColorTask(), 0, animationRate);
    	}
	}
	
	public void generateColorMap(){
		colors.clear();
		for (int i = 0; i < numX; ++i) {
			for (int j = 0; j < numY; ++j) {
				int c = generateColor();
				colors.add( c);
			}
		}
	}

	private int generateColor() {
		int valR = threshold + random.nextInt(256-threshold);
		int valG = threshold + random.nextInt(256-threshold);
		int valB = threshold + random.nextInt(256-threshold);

		int c = Color.WHITE;
		if (BitmapView.this.colorRange.equals("All"))
			c = Color.rgb(valR, valG, valB);
		else if (BitmapView.this.colorRange.equals("Red"))
			c = Color.rgb(valR, 0, 0);
		else if (BitmapView.this.colorRange.equals("Green"))
			c = Color.rgb(0, valG, 0);
		else if (BitmapView.this.colorRange.equals("Blue"))
			c = Color.rgb(0, 0, valB);
		else if (BitmapView.this.colorRange.equals("Cyan"))
			c = Color.rgb(0, valG, valB);
		else if (BitmapView.this.colorRange.equals("Yellow"))
			c = Color.rgb(valR, valG, 0);
		else if (BitmapView.this.colorRange.equals("Magenta"))
			c = Color.rgb(valR, 0, valB);
		else if (BitmapView.this.colorRange.equals("Gray"))
			c = Color.rgb(valR, valR, valR);
		return c;
	}
		   
	class ColorTask extends TimerTask {
		private Handler updateUI = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				for (int i = 0; i < numX; ++i) {
					for (int j = 0; j < numY; ++j) {
						int c = colors.get(j + i * numY);
						int r = Color.red(c);
						int g = Color.green(c);
						int b = Color.blue(c);
						r -= decay;
						g -= decay;
						b -= decay;

						if (r < threshold)
							r = threshold;
						if (g < threshold)
							g = threshold;
						if (b < threshold)
							b = threshold;
						if (b == threshold && g == threshold && b == threshold)
							c = generateColor();
						else
							c = Color.rgb(r, g, b);
						colors.set(j + i * numY, c);
						BitmapView.this.bitmaps.get(j + i * numY).eraseColor(c);
					}

					BitmapView.this.invalidate();
				}
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
		 createBitmaps(w, h);
	 }

	private void createBitmaps(int w, int h) {
		this.bitmaps.clear();
		this.viewWidth = w;
		this.viewHeight = h;
		this.numX = viewWidth / (blockSize + padding * 2);
		this.numY = viewHeight / (blockSize + padding * 2);
		this.offsetX = padding
				+ (viewWidth % (blockSize + padding * 2)) / 2;
		this.offsetY = padding
				+ (viewHeight % (blockSize + padding * 2)) / 2;
		this.incr = blockSize + padding * 2;

		for (int i = 0; i < numX; ++i) {
			for (int j = 0; j < numY; ++j) {
				Bitmap bm = Bitmap.createBitmap(blockSize, blockSize,
						Bitmap.Config.ARGB_8888);
				//bm.eraseColor(Color.WHITE);
				this.bitmaps.add(bm);
				this.colors.add(Color.WHITE);
			}
		}
		
		generateColorMap();
	}

	@Override
	 protected void onDraw(Canvas canvas) {
		 for (int i = 0; i < numX; ++i){
			 int posX = (i*(incr))+offsetX;
			 for (int j = 0; j < numY; ++j){
				 canvas.drawBitmap(this.bitmaps.get(j + i*numY), 
						 posX, 
						 (j*(incr))+offsetY, null);
			 }
		 }
	  	
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
		this.blockSize = blockSize;
		enableAnimation(false);
		createBitmaps(this.viewWidth, this.viewHeight);
		enableAnimation(true);
	}

	public void setColorRange(String colorRange) {
		this.colorRange = colorRange;
	}

	public void setDecayStep(int decayStep) {
		this.decay = decayStep;
		
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public void setPadding(int padding) {
		this.padding = padding;
		enableAnimation(false);
		createBitmaps(this.viewWidth, this.viewHeight);
		enableAnimation(true);
	}
}
