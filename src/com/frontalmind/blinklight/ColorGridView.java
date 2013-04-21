/**
 * 
 */
package com.frontalmind.blinklight;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;

import com.frontalmind.ColorBurstModel;
import com.frontalmind.IViewUpdate;

/**
 * @author bob
 *
 */
public class ColorGridView extends View implements IViewUpdate {

	public ColorBurstModel model;
	private MediaPlayer mp;
	
	public ColorGridView(Context context) {
		super(context);
		model = new ColorBurstModel(this);
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP){
					//enableDraw = false;
					//enableAnimation(true);
					//colorGrid.setLocked
					//for (StrokeAndFillDrawable shape : shapes)
						//shape.setLock(false);
				}	
				else if (event.getAction() == MotionEvent.ACTION_DOWN) {
					model.onTouch();
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

		model.setBlockSize(50);
	}
	

	 @Override
	 protected void onSizeChanged(int w, int h, int oldw, int oldh)
	 {
		 model.sizeChanged(w,h);
	 }

	@Override
	protected void onDraw(Canvas canvas) {
		 model.draw(canvas);
	}
	
	public void onResume() 
	{
		//enableAnimation(true);
	}

	public void onPause() 
	{
		model.enableAnimation(false);
	}

	@Override
	public void updateView() {
		this.invalidate();
	}


	public void setSoundEffects(MediaPlayer mp) {
		model.setSoundEffects(mp);	
	}


}
