/**
 * Game.java
*/
package net.rio.superHedge;

import org.json.*;

import android.graphics.*;
import android.view.*;

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
	 * start next level
	 */
	static final int HEG_NEXT_LEVEL = 1;
	
	/**
	 * game over, retry
	 */
	static final int HEG_DIED = 2;
	
	private static Main main;
	
	private Paint paint;
	private Entity[] ents;
	private Bitmap aplImg, pauseImg;

	/**
	 * the game uses cnt to get stat of game
	 *  cnt > 0	-> starting screen,
	 *  cnt == 0	-> normal,
	 *  cnt == -1	-> die,
	 *  cnt == -2	-> pause,
	 *  cnt == -3	-> next level
	 *  cnt < -3 > -200	-> winning screen
	 *  cnt == -200		-> player won
	 */
	private static int cnt;	//
	
	private int[] ctrl = new int[4];
	private int level;
	private int titSize, txtSize;	//text size of title and text
	private int titY, txtY;				//text Y position of title and text

	public Game(Main main, int level) {
		super(main);
		
		/*setting variables*/
		Game.main = main;
		this.level = level;
		cnt = -1;
		
		titSize =  Main.scrH / 8;
		txtSize = Main.scrH / 18;
		
		titY = Main.scrH / 3;
		txtY = Main.scrH / 2;	
		
		paint = new Paint();
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		
		aplImg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(),
				R.drawable.apple), 30, 40, false);
		pauseImg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(),
				R.drawable.pause), 30, 30, false);
		
		setupEnts(new MapReader(main, level));
		
		GameRule.setEnts(ents);
		
	}
	
	private void setupEnts(MapReader mr) {
		JSONObject[] tmpEnt = mr.getEnts();
		ents = new Entity[tmpEnt.length];
		for(int i = 0; i < tmpEnt.length; i++) {
			try {
				int tmpType = 2;
				int[] tmpPos = {0, 0};
				int[] tmpData = {};
				
				if(tmpEnt[i].has("type"))
					tmpType = tmpEnt[i].getInt("type");
				if(tmpEnt[i].has("pos"))
					tmpPos = getAryFromJSON(tmpEnt[i].getJSONArray("pos"));
				if(tmpEnt[i].has("data"))
					tmpData = getAryFromJSON(tmpEnt[i].getJSONArray("data"));
				
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
		if(stat == 1) {
			if(cnt == -1) {
				main.newGame(Game.HEG_DIED);
				return;
			}
			else if(cnt == -2) {
				resume();
				return;
			}
		}
		ctrl[1] = stat;
	}
	
	/**
	 * to stat the game
	 */
	void start() {
		cnt = 100;
	}
	
	/**
	 *to  pause the game
	 */
	void pause() {
		if(cnt == 0)			//the game is playing
			cnt = -2;
		else if(cnt == -2 || cnt == -1)	 {//the game paused or is over
				Main.playSnd(2);
				main.showMenu();
		}
	}
	
	/**
	 * to resume the game
	 */
	void resume() {
		cnt = 0;
	}
	
	/**
	 * the player won the game
	 */
	void win() {
		cnt = -4;
	}
	
	/**
	 * call this every tick, do everything and repaint
	 */
	void tick() {
		
		if(cnt == 0) {	//the game is running normaly
			
			for(int i = 0; i < ctrl.length; i++) {		//move hedgehog from user's control
				if(ctrl[i] != 0) {
					ents[0].advMove(i, ctrl[i]);
					ctrl[i] = 0;
				}
			}
			
			for(int i = 0; i < ents.length; i++) {
				if(ents[i] == null) continue;
				ents[i].tick();			
			}
			
		} else if(cnt == -3) {	//next level
			main.newGame(HEG_NEXT_LEVEL);
		} else if(cnt < -3 || cnt >= 0) {	//the game is in winning screen or starting screen
			cnt--;
		}
		
		if(cnt == -200) {	//player won
			main.showMenu();
		}
		
		invalidate();
		
	}
	
	@Override
	protected void onDraw(Canvas can) {
		can.drawColor(Color.parseColor("#808080"));
		
		can.drawBitmap(pauseImg, 20, 10, paint);
			
		if(cnt == 0) {			//the game is playing
			drawImgs(can);
		} else if(cnt > 0) {		//the game is in start screen

			setTextFont(0);
			paint.setTextAlign(Paint.Align.CENTER);
			
			can.drawText(main.getString(R.string.level) + (level + 1), Main.scrW / 2, titY, paint);

			setTextFont(1);
			paint.setTextAlign(Paint.Align.LEFT);
			
		} else if(cnt == -1) {	//the game is in dieing screen

			setTextFont(0);
			paint.setTextAlign(Paint.Align.CENTER);
			
			can.drawText(main.getString(R.string.game_over), Main.scrW / 2, titY, paint);

			setTextFont(1);
			
			can.drawText(main.getString(R.string.contin), Main.scrW / 2, txtY, paint);
			can.drawText(main.getString(R.string.quit), Main.scrW / 2, txtY + txtSize, paint);
			
			paint.setTextAlign(Paint.Align.LEFT);
			
		} else if(cnt == -2) {	//the game is in pause
			drawImgs(can);

			setTextFont(0);
			
			paint.setTextAlign(Paint.Align.CENTER);
			
			can.drawText(main.getString(R.string.paus), Main.scrW / 2, titY, paint);
			
			setTextFont(1);
			
			can.drawText(main.getString(R.string.contin), Main.scrW / 2, txtY, paint);
			can.drawText(main.getString(R.string.quit), Main.scrW / 2, txtY + txtSize, paint);
			
			paint.setTextAlign(Paint.Align.LEFT);
			
		} else if(cnt < -3) {	//player won
			
			setTextFont(0);
			paint.setTextAlign(Paint.Align.CENTER);
			
			can.drawText(main.getString(R.string.win), Main.scrW / 2, titY, paint);

			setTextFont(1);
			
			can.drawText(main.getString(R.string.score) + GameRule.getApls(), Main.scrW / 2, txtY, paint);
			
		}
	}
	
	/**
	 * set color and font in game
	 * @param type 0 = title, 1 = text
	 */
	private void setTextFont(int type) {
		switch(type) {
		case 0:
			paint.setColor(Color.YELLOW);
			paint.setTextSize(titSize);
			break;
		case 1:
			paint.setColor(Color.BLACK);
			paint.setTextSize(txtSize);
			break;
		}
	}
	
	/**
	 * draw images of entities and icons
	 * @param can
	 */
	private void drawImgs(Canvas can) {
		
		for(int i = 0; i < ents.length; i++) {
			if(ents[i] == null) continue;
			can.drawBitmap(ents[i].getImg(), (int) (ents[i].pos[0] * (Main.scrW / 750f)), (int) (ents[i].pos[1] * (Main.scrH / 500f)), paint);
		}
		
		can.drawBitmap(aplImg, Main.scrW - 120, 10, paint);
		can.drawText(" = " + GameRule.getApls() , Main.scrW - 90, 50, paint);
		
	}
	
	/**
	 * start a new game
	 */
	static void newGame(int stat) {
		switch(stat) {
		case Game.HEG_NEWGAME:
		case Game.HEG_NEXT_LEVEL:
			cnt = -3;
			break;
		case Game.HEG_DIED:
			cnt = -1;
			Main.playSnd(2);
			break;
		}
	}

}
