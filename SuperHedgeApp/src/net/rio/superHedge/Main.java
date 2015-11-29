/*
 * Main.java
 */
package net.rio.superHedge;

import android.app.Activity;
import android.hardware.*;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.view.View.OnTouchListener;

/**
 * Main class in SuperHedge
 * @author rio
 *
 */
public class Main extends Activity implements SensorEventListener, OnTouchListener {
	
	private SensorManager mgr;
	private Sensor sr;
	private Game game;
	
	private int curLevel;
	private boolean isRunning;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		sr = mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		curLevel = 0;
		newGame(Game.HEG_NEWGAME);
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		game.phoneMoved(((int) event.values[1]) * 2);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	@Override
	protected void onResume() {
		mgr.registerListener(this, sr, SensorManager.SENSOR_DELAY_FASTEST);
		
		isRunning = true;
		final Handler han =  new Handler();
		han.postDelayed(new Runnable() {
			public void run() {
				int stat = game.tick();
				if(stat == Game.HEG_FINISHED || stat == Game.HEG_DIED)
					newGame(stat);
				if(isRunning)
					han.postDelayed(this, 10);
			}
		}, 10);
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		mgr.unregisterListener(this);
		
		isRunning = false;
		
		super.onPause();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			game.screenTouched(1);
		} else if(event.getAction() == MotionEvent.ACTION_UP) {
			game.screenTouched(0);
		}
	    return true;
	}
	
	private void newGame(int stat) {
		int level = 0;
		
		/*switch(stat){
		case Game.HEG_NEWGAME:
			curLevel = 0;
			level = curLevel;
			break;
		case Game.HEG_FINISHED:
			curLevel++;
			level = curLevel;
			break;
		case Game.HEG_DIED:
			level = curLevel;
			break;
		default:
			level = 0;
		
		}*/
		
		if(level > 0)
			level = 0;
		
		game = new Game(this, level);
		game.setOnTouchListener(this);
		setContentView(game);
	}

}
