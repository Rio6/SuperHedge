/**
 * Game.java
*/
package net.rio.superHedge;

import org.json.*;

import android.graphics.*;
import android.view.View;

/**
 * game manage of Super Hedge
 * @author rio
 *
 */
class Game extends View {
	
	/**
	 * start a new game
	 */
	static final int HEG_NEWGAME = 0;
	
	/**
	 * game over, retry
	 */
	static final int HEG_DIED = 1;
	
	private static Main main;
	
	private Paint paint;
	private Entity[] ents;
	private Bitmap aplImg, pauseImg;
	private GameRule rule;

	private static int cnt = 0;
	
	private int[] ctrl = {0, 0, 0};
	private int level;

	public Game(Main main, int level) {
		super(main);
		
		/*setting variables*/
		Game.main = main;
		this.level = level;
		cnt = -1;
		
		paint = new Paint();
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		
		aplImg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(),
				R.drawable.apple), 30, 40, false);
		pauseImg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(),
				R.drawable.pause), 30, 30, false);
		
		setupEnts(new MapReader(main, level));
		
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
			return new Hedgehog(main, pos, id, data);
		case 1:
			return new Portal(main, pos, id, data);
		case 2:
			return new Apple(main, pos, id, data);
		case 3:
			return new Dog(main, pos, id, data);
		case 4:
			return new Wall(main, pos, id, data);
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
	
	void start() {
		cnt = 200;
	}
	
	/**
	 * call this every tick
	 * @return status of game
	 * @see Game.HEG_NORMAL
	 * @see Game.HEG_NEWGAME
	 * @see Game.HEG_FINISH
	 * @see Game.HEG_DIE
	 */
	void tick() {
		
		if(cnt == 0) {
			for(int i = 0; i < ents.length; i++) {
				if(ents[i] == null) continue;
				ents[i].tick();			
			}
			
			for(int i = 0; i < ctrl.length; i++) {
				if(ctrl[i] != 0) {
					ents[0].advMove(i, ctrl[i]);
				}
			}
		} else {
			if(cnt < -200) {
				main.newGame(Game.HEG_DIED);
			}
			cnt--;
		}
		
		invalidate();
		
	}
	
	@Override
	protected void onDraw(Canvas can) {
		can.drawColor(Color.parseColor("#808080"));
		
		if(cnt == 0) {		//the game is running normally
			
			for(int i = 0; i < ents.length; i++) {
				if(ents[i] == null) continue;
				can.drawBitmap(ents[i].getImg(), (int) (ents[i].pos[0] * (Main.scrW / 750f)), (int) (ents[i].pos[1] * (Main.scrH / 500f)), paint);
			}
			
			can.drawBitmap(aplImg, Main.scrW - 120, 10, paint);
			can.drawText(" = " + rule.getApls() , Main.scrW - 90, 50, paint);
			
			can.drawBitmap(pauseImg, 20, 10, paint);
			
		} else if(cnt > 0) {		//the game is in start screen
			
			paint.setColor(Color.YELLOW);
			paint.setTextSize(100);
			paint.setTextAlign(Paint.Align.CENTER);
			
			can.drawText(main.getString(R.string.level) + (level + 1), Main.scrW / 2, 300, paint);
			
			paint.setColor(Color.BLACK);
			paint.setTextSize(40);
			paint.setTextAlign(Paint.Align.LEFT);
			
		} else if(cnt < 0) {		//the game is in dieing screen
			
			paint.setColor(Color.YELLOW);
			paint.setTextSize(100);
			paint.setTextAlign(Paint.Align.CENTER);
			
			can.drawText(main.getString(R.string.game_over), Main.scrW / 2, 300, paint);
			
			paint.setColor(Color.BLACK);
			paint.setTextSize(40);
			paint.setTextAlign(Paint.Align.LEFT);
			
		}
		
	}
	
	/**
	 * start a new game
	 * change stat and tick() will return it to main
	 */
	static void newGame(int stat) {
		if(stat == Game.HEG_NEWGAME)
			main.newGame(Game.HEG_NEWGAME);
		
		else if(stat == Game.HEG_DIED)
			cnt = -1;
	}

}
