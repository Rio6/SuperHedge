/**
 * Main.java
 */
package net.rio.superHedge;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

/**
 * Main class in SuperHedge
 * @author rio
 *
 */
public class Main extends Activity implements SensorEventListener, OnTouchListener {
	
	static final int STAT_MENU = 0;	
	static final int STAT_GAME = 1;
	
	static int scrW = 0, scrH = 0;
	
	private SensorManager mgr;
	private Sensor sr;
	private Game game;
	private Menu menu;
	private final Handler han =  new Handler();
	
	private Runnable run;
	
	private int curLevel;
	private boolean isRunning;
	private int gameStat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		/*getting the screen size*/
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		scrW = size.x;
		scrH = size.y;
		
		/*setting seneor*/
		mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		sr = mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		/*setting runnable*/
		run = new Runnable() {
			public void run() {
				int stat = game.tick();
				if(stat == Game.HEG_FINISHED || stat == Game.HEG_DIED)
					newGame(stat);
				if(isRunning)
					han.postDelayed(this, 10);
			}
		};
		
		showMenu();
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(gameStat == Main.STAT_GAME) 
			game.phoneMoved(((int) event.values[1]) * 2);	
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	@Override
	protected void onResume() {
		mgr.registerListener(this, sr, SensorManager.SENSOR_DELAY_FASTEST);
		
		strtTick();
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		mgr.unregisterListener(this);
		
		isRunning = false;
		
		super.onPause();
	}
	
	private void strtTick() {
		
		if(gameStat == Main.STAT_GAME && !isRunning) {
			isRunning = true;
			han.postDelayed(run, 10);
		}		
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent eve) {
		
		if(eve.getAction() == MotionEvent.ACTION_DOWN) {
			if(eve.getX() < 60 && eve.getY() < 60)
				showMenu();
			else
				game.screenTouched(1);
		} else if(eve.getAction() == MotionEvent.ACTION_UP) {
			game.screenTouched(0);
		}
	    return true;
	    
	}
	
	void newGame(int stat) {
		gameStat = Main.STAT_GAME;
		strtTick();
		
		int level = 0;
		
		game = new Game(this, level);
		game.setOnTouchListener(this);
		setContentView(game);
		
		game.start();
	}

	private void showMenu() {
		gameStat = Main.STAT_MENU;
		isRunning = false;
		menu = new Menu(this);
		setContentView(menu);
	}

}
