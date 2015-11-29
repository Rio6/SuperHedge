/*
 * Game.java
*/
package net.rio.superHedge;

import org.json.*;

import android.content.Context;
import android.graphics.*;
import android.view.*;

/**
 * game manage of Super Hedge
 * @author rio
 *
 */
class Game extends View {
	
	/**
	 * normal running
	 */
	static final int HEG_NORMAL = 0;
	
	/**
	 * start a new game
	 */
	static final int HEG_NEWGAME = 1;
	
	/**
	 * level finished
	 */
	static final int HEG_FINISHED = 2;
	
	/**
	 * game over, retry
	 */
	static final int HEG_DIED = 3;
	
	static int scrW = 0, scrH = 0;
	
	private Paint paint;
	private Entity[] ents;
	private Context con;
	private Bitmap aplImg;
	private GameRule rule;
	
	private int[] ctrl = {0, 0, 0};
	private int cnt = 0;
	
	private static int stat;

	public Game(Context con, int level) {
		super(con);
		
		/*getting the screen size*/
		Display display = ((WindowManager) con.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		scrW = size.x;
		scrH = size.y;
		
		/*setting variables*/
		this.con = con;
		stat = Game.HEG_NORMAL;
		
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(40);
		
		aplImg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(con.getResources(),
				R.drawable.apple), 30, 40, false);
		
		setupEnts(new MapReader(con, level));
		
		rule = new GameRule(-1);
		rule.setEnts(ents);
		
	}
	
	private void setupEnts(MapReader mr) {
		JSONObject[] tmpEnt = mr.getEnts();
		ents = new Entity[tmpEnt.length];
		for(int i = 0; i < tmpEnt.length; i++) {
			try {
				int tmpType = tmpEnt[i].getInt("type");
				int[] tmpPos = getAryFromJSON(tmpEnt[i].getJSONArray("pos"));
				int[] tmpData = getAryFromJSON(tmpEnt[i].getJSONArray("data"));
				
				ents[i] = getEntFromType(tmpType, tmpPos, i, tmpData);
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	private int[] getAryFromJSON(JSONArray src) throws JSONException {
		int len = src.length();
		int[] rst = new int[len];
		for(int i = 0; i < len; i++) {
			rst[i] = src.getInt(i);
		}
		return rst;
	}
	
	private Entity getEntFromType(int type, int[] pos, int id, int[] data) {
		switch(type) {
		case 0:
			return new Hedgehog(con, pos, id, data);
		case 1:
			return new Portal(con, pos, id, data);
		case 2:
			return new Apple(con, pos, id, data);
		case 3:
			return new Dog(con, pos, id, data);
		case 4:
			return null;
		case 5:
			return new Wall(con, pos, id, data);
		default:
			return null;
		}
	}
	
	void phoneMoved(int stat) {
		if(stat < 0) {
			ctrl[0] = Math.abs(stat);
			ctrl[2] = 0;
		} else if(stat > 0) {
			ctrl[2] = stat;
			ctrl[0] = 0;
		} else {
			ctrl[0] = 0;
			ctrl[2] = 0;
		}
	}
	
	/**
	 * listen for touch event
	 * @param stat 1 if down, 0 if up
	 */
	void screenTouched(int stat) {
		ctrl[1] = stat;
	}
	
	/**
	 * call this every tick
	 * @return status of game
	 * @see Game.HEG_NORMAL
	 * @see Game.HEG_NEWGAME
	 * @see Game.HEG_FINISH
	 * @see Game.HEG_DIE
	 */
	int tick() {
		
		for(int i = 0; i < ents.length; i++) {
			if(ents[i] == null) continue;
			ents[i].tick();			
		}
		
		for(int i = 0; i < ctrl.length; i++) {
			if(ctrl[i] != 0) {
				ents[0].advMove(i, ctrl[i]);
			}
		}
		
		invalidate();
		
		return stat;
	}
	
	@Override
	protected void onDraw(Canvas can) {
		can.drawColor(Color.parseColor("#808080"));
		
		for(int i = 0; i < ents.length; i++) {
			if(ents[i] == null) continue;
			can.drawBitmap(ents[i].getImg(), (int) (ents[i].pos[0] * (scrW / 750f)), (int) (ents[i].pos[1] * (scrH / 500f)), paint);
		}
		
		can.drawBitmap(aplImg, 10, 10, paint);
		can.drawText(" = " + rule.getApls() , 40, 50, paint);
		
	}
	
	/**
	 * start a new game
	 * 
	 */
	static void newGame(int stat) {
		Game.stat = stat;
	}

}
