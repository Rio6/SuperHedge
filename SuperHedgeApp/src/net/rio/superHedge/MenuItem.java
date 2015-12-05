package net.rio.superHedge;

import android.content.Context;
import android.graphics.*;
import android.view.*;

/**
 * the item in the menu
 * @author rio
 *
 */
class MenuItem extends View implements View.OnTouchListener {
	
	private Paint paint = new Paint();
	private Menu menu;
	
	private String txt;
	private int color;
	private boolean preDwn;

	/**
	 * 
	 * @param con Context from main activity
	 * @param menu the start menu who called this
	 * @param width width of MenuItem
	 * @param height height of MenuItem
	 * @param txt text to display
	 */
	public MenuItem(Context con, Menu menu, int width, int height, String txt) {
		super(con);
		this.menu = menu;
		
		setLayoutParams(new ViewGroup.LayoutParams(width, height));
		setOnTouchListener(this);
		
		this.txt = txt;
		color = Color.parseColor("#808080");

		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
	}
	
	@Override
	protected void onDraw(Canvas can) {
		can.drawColor(color);
		
		paint.setTextSize(getHeight() - 10);
		
		can.drawText(txt, getWidth() / 2, getHeight() - 10, paint);
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent eve) {
		
		if(eve.getX() > getWidth() || eve.getX() < 0 || eve.getY() > getHeight() || eve.getY() < 0) {
			color = Color.parseColor("#808080");
			paint.setColor(Color.BLACK);
			preDwn = false;
		} else	if(eve.getAction() == MotionEvent.ACTION_DOWN) {
			color = Color.parseColor("#202020");
			paint.setColor(Color.YELLOW);
			preDwn = true;
		} else if(eve.getAction() == MotionEvent.ACTION_UP && preDwn) {
			color = Color.parseColor("#808080");
			paint.setColor(Color.BLACK);
			menu.onClick(v);
			preDwn = false;
		}
		
		invalidate();
		
		return true;
	}
	
}