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
	private Context con;
	
	private String txt;
	private int BColor, FColor;	//backgrounf color and foregroung color
	private int width, height;
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
		this.con = con;
		this.width = width;
		this.height = height;
		this.txt = txt;
		
		setLayoutParams(new ViewGroup.LayoutParams(width, height));
		setOnTouchListener(this);
		
		BColor = con.getResources().getColor(R.color.light_gray);

		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		paint.setTextSize(height);
		paint.setTextScaleX(height / (paint.measureText(txt) / (width / (float) height)));
	}
	
	@Override
	protected void onDraw(Canvas can) {
		can.drawColor(BColor);
		can.drawText(txt, width / 2, 2 * height - paint.getTextSize() * 1.1f, paint);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent eve) {
		
		if(eve.getX() > getWidth() || eve.getX() < 0 || eve.getY() > getHeight() || eve.getY() < 0) {
			BColor = con.getResources().getColor(R.color.light_gray);
			FColor = Color.BLACK;
			preDwn = false;
		} else	if(eve.getAction() == MotionEvent.ACTION_DOWN) {
			BColor = con.getResources().getColor(R.color.dark_gray);
			FColor = Color.YELLOW;
			preDwn = true;
		} else if(eve.getAction() == MotionEvent.ACTION_UP && preDwn) {
			BColor = con.getResources().getColor(R.color.light_gray);
			FColor = Color.BLACK;
			menu.onClick(v);
			preDwn = false;
		}
		
		paint.setColor(FColor);
		invalidate();
		
		return true;
	}
	
}