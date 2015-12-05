/**
 * Manu.java
 */
package net.rio.superHedge;

import android.content.Context;
import android.graphics.*;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

/**
 * @author rio
 *
 */
class Menu extends RelativeLayout {

	/**
	 * 
	 */
	public Menu(Context con) {
		super(con);
		setBackground(con.getResources().getDrawable(R.drawable.background));
		setGravity(Gravity.CENTER);
		
		MenuItem btn = new MenuItem(con, 500, 200);
		addView(btn);
	}
	
}


class MenuItem extends View {
	
	Paint paint = new Paint();

	public MenuItem(Context context, int width, int height) {
		super(context);
		setLayoutParams(new LayoutParams(width, height));
	}
	
	@Override
	protected void onDraw(Canvas can) {
		can.drawColor(Color.rgb(80, 80, 80));
		can.drawText("test", 100, 100, paint);
	}
	
}