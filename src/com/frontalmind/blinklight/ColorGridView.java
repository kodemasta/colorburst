/**
 * 
 */
package com.frontalmind.blinklight;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author bob
 *
 */
public class ColorGridView extends View {

	private Random random;
	private int incr, animationRate, viewWidth, viewHeight, blockSize, 
	numX, numY, offsetX, offsetY, padding, decay, threshold, strokeWidth;
	Vector<StrokeAndFillDrawable> shapes;
	private Timer timer;
	String colorRange;
	String shape = "rect";
	Paint paint = new Paint();
	float[] outerR = new float[] { 3, 3, 3, 3, 3, 3, 3, 3 };

	
	public ColorGridView(Context context) {
		super(context);
		padding = 2;
		animationRate = 50;
		decay = 8;
		threshold = 0;
		strokeWidth = 3;

		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (timer == null)
						enableAnimation(true);
					else
						enableAnimation(false);
				}
				return true;
			}
		});

		random = new Random();
		shapes = new Vector<StrokeAndFillDrawable>();
		
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

	private int generateColor() {
		int valR = threshold + random.nextInt(256-threshold);
		int valG = threshold + random.nextInt(256-threshold);
		int valB = threshold + random.nextInt(256-threshold);

		int c = Color.WHITE;
		if (ColorGridView.this.colorRange.equals("All"))
			c = Color.rgb(valR, valG, valB);
		else if (ColorGridView.this.colorRange.equals("Red"))
			c = Color.rgb(valR, 0, 0);
		else if (ColorGridView.this.colorRange.equals("Green"))
			c = Color.rgb(0, valG, 0);
		else if (ColorGridView.this.colorRange.equals("Blue"))
			c = Color.rgb(0, 0, valB);
		else if (ColorGridView.this.colorRange.equals("Cyan"))
			c = Color.rgb(0, valG, valB);
		else if (ColorGridView.this.colorRange.equals("Yellow"))
			c = Color.rgb(valR, valG, 0);
		else if (ColorGridView.this.colorRange.equals("Magenta"))
			c = Color.rgb(valR, 0, valB);
		else if (ColorGridView.this.colorRange.equals("Gray"))
			c = Color.rgb(valR, valR, valR);
		return c;
	}
		   
	class ColorTask extends TimerTask {
		private Handler updateUI = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				for (StrokeAndFillDrawable shape : shapes) {
					
					//fill
					int c = shape.getPaint().getColor();
					c = updateColor(c);
					shape.getPaint().setColor(c);
					
					//stroke
					if (shape.isEnableStroke()){
						c = shape.getStrokeColor();
						int w = shape.getStrokeWidth();
						c = updateColor(c);
						shape.setStrokeColor(c, w);
					}
				}
				ColorGridView.this.invalidate();
			}

			private int updateColor(int c) {
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
				return c;
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
		 createGrid(w, h);
	 }

	private void createGrid(int w, int h) {
		int totalCellWidth = blockSize + padding*2 ;
		shapes.clear();
		this.viewWidth = w;
		this.viewHeight = h;
		this.numX = viewWidth / (totalCellWidth + strokeWidth);
		this.numY = viewHeight / (totalCellWidth + strokeWidth);
		this.offsetX = padding + strokeWidth/2
				+ (viewWidth % (totalCellWidth+ strokeWidth)) / 2;
		this.offsetY = padding + + strokeWidth/2
				+ (viewHeight % (totalCellWidth+ strokeWidth)) / 2;
		this.incr = totalCellWidth + strokeWidth;

		Rect bounds = new Rect();;
		for (int i = 0; i < numX; ++i) {
			 int posX = (i*(incr))+offsetX;
			for (int j = 0; j < numY; ++j) {
				int posY = (j*(incr))+offsetY;
				bounds.left = posX;
				bounds.right = posX + blockSize;
				bounds.top = posY;
				bounds.bottom = posY + blockSize;
				if (shape.equals("circle")){		
					StrokeAndFillDrawable shape = new StrokeAndFillDrawable(new OvalShape());
					shape.setBounds(bounds);
					shape.getPaint().setColor(generateColor());
					shape.setStrokeColor(generateColor(), strokeWidth);
					shapes.add(shape);
				}else if (shape.equals("rect")){
					StrokeAndFillDrawable shape = new StrokeAndFillDrawable(new RectShape());
					shape.setBounds(bounds);
					shape.getPaint().setColor(generateColor());
					shape.setStrokeColor(generateColor(), strokeWidth);
					shapes.add(shape);
				} else if (shape.equals("round_rect")){
					StrokeAndFillDrawable shape = new StrokeAndFillDrawable(new RoundRectShape(outerR, null, null));
					shape.setBounds(bounds);
					shape.getPaint().setColor(generateColor());
					shape.setStrokeColor(generateColor(), strokeWidth);
					shapes.add(shape);
				}
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (ShapeDrawable shape : shapes)
			shape.draw(canvas);
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
		float cornerRadius = blockSize / 10;
		outerR = new float[] { cornerRadius, cornerRadius, cornerRadius,
				cornerRadius, cornerRadius, cornerRadius, cornerRadius,
				cornerRadius };

		enableAnimation(false);
		createGrid(this.viewWidth, this.viewHeight);
		enableAnimation(true);
	}

	public void setColorRange(String colorRange) {
		this.colorRange = colorRange;
		enableAnimation(false);
		createGrid(this.viewWidth, this.viewHeight);
		enableAnimation(true);
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
		createGrid(this.viewWidth, this.viewHeight);
		enableAnimation(true);
	}

	public void setShape(String shape) {
		this.shape = shape;	
		enableAnimation(false);
		createGrid(this.viewWidth, this.viewHeight);
		enableAnimation(true);
	}

	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
		enableAnimation(false);
		createGrid(this.viewWidth, this.viewHeight);
		enableAnimation(true);
	}
}
