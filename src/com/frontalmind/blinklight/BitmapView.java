/**
 * 
 */
package com.frontalmind.blinklight;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
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
	Vector<Point> grid;
	private List<Integer> colors;
	private Timer timer;
	private boolean toggleAnimate = true;
	String colorRange;
	String shape = "rect";
	Paint paint = new Paint();
	RectF rect = new RectF();
	int cornerRadius = 3;

	
	public BitmapView(Context context) {
		super(context);
		blockSize = 50;
		padding = 2;
		animationRate = 50;
		decay = 8;
		threshold = 0;
		cornerRadius = blockSize/10;

		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					toggleAnimate = !toggleAnimate;
					enableAnimation(toggleAnimate);
				}
				return true;
			}
		});

		colors = new LinkedList<Integer>();
		grid = new Vector<Point>();
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
						if (r == threshold && g == threshold && b == threshold)
							c = generateColor();
						else
							c = Color.rgb(r, g, b);
						colors.set(j + i * numY, c);
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
		grid.clear();
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
			 int posX = (i*(incr))+offsetX;

			for (int j = 0; j < numY; ++j) {
				int posY = (j*(incr))+offsetY;
				grid.add(new Point(posX, posY));
			}
		}
		
		generateColorMap();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (int i = 0; i < numX; ++i) {
			for (int j = 0; j < numY; ++j) {
				int incY = i * numY;
				Point pnt = grid.get(incY + j);

				paint.setColor(this.colors.get(j + incY));
				if (shape.equals("circle")){
					int radius = blockSize / 2;
					canvas.drawCircle(pnt.x + radius, 
							pnt.y + radius, radius,
							paint);
				}else if (shape.equals("rect")){
					rect.left = pnt.x;
					rect.right = pnt.x + blockSize;
					rect.top = pnt.y;
					rect.bottom = pnt.y + blockSize;
					canvas.drawRect(rect, paint);
				} else if (shape.equals("round_rect")){
					rect.left = pnt.x;
					rect.right = pnt.x + blockSize;
					rect.top = pnt.y;
					rect.bottom = pnt.y + blockSize;					
					canvas.drawRoundRect(rect, this.cornerRadius, this.cornerRadius, paint);
				}
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
		this.cornerRadius = blockSize/10;

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

	public void setShape(String shape) {
		this.shape = shape;	
	}
}
