/**
 * Main.java
 */
package net.rio.superHedge;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.hardware.*;
import android.media.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.util.DisplayMetrics;

/**
 * Main class in SuperHedge
 * @author rio
 *
 */
public class Main extends Activity implements SensorEventListener, View.OnTouchListener {

    static final int STAT_MENU = 0; 
    static final int STAT_GAME = 1;

    public static int scrW, scrH;

    private SensorManager mgr;
    private Sensor sr;
    private FrameLayout lay;
    private Game game;
    private Menu menu;
    private static SoundPool snds;
    private final Handler han =  new Handler();

    private Runnable run;

    private static int[] sndId;

    private int curLevel;
    private int gameStat;
    private int levelCnt;
    private boolean isRunning;

    private DateFormat formatter = new SimpleDateFormat("mm:ss");
    private long startTime;
    private long pauseTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*getting screen size*/
        DisplayMetrics dm = new DisplayMetrics();
        if(Build.VERSION.SDK_INT < 17)
            getWindowManager().getDefaultDisplay().getMetrics(dm);
        else
            getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        scrW = dm.widthPixels;
        scrH = dm.heightPixels;

        /*setting variables*/

        snds = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        sndId = new int[4];

        sndId[0] = snds.load(this, R.raw.start, 1);
        sndId[1] = snds.load(this, R.raw.apple, 1);
        sndId[2] = snds.load(this, R.raw.die, 1);
        sndId[3] = snds.load(this, R.raw.win, 1);

        lay = new FrameLayout(this);
        setContentView(lay);

        menu = new Menu(this);

        try {
            levelCnt = getAssets().list("levels").length;
        } catch (IOException e) {
            levelCnt = 0;
            e.printStackTrace();
        }

        /*setting seneor*/
        mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sr = mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        /*setting runnable*/
        run = new Runnable() {
            public void run() {
                game.tick();
                if(isRunning)
                    han.postDelayed(this, 10);
            }
        };

        showMenu();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(gameStat == Main.STAT_GAME) {
            int t = ((int) event.values[1]) * 2;
            game.phoneMoved(t > 16 ? 16 : t < -16 ? -16 : t);   
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        mgr.registerListener(this, sr, SensorManager.SENSOR_DELAY_FASTEST);

        final View decorView = getWindow().getDecorView();
        final int uiOptions =
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        if(Build.VERSION.SDK_INT >= 11) {
            decorView.setSystemUiVisibility(uiOptions);
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(uiOptions);
                    }
                }
            });
        }

        strtTick();
        resumeTime();

        super.onResume();
    }

    @Override
    protected void onPause() {
        mgr.unregisterListener(this);

        isRunning = false;
        pauseTime();

        super.onPause();
    }

    /**
     * start the ticking of the game
     */
    private void strtTick() {

        if(gameStat == Main.STAT_GAME && !isRunning) {
            isRunning = true;
            han.postDelayed(run, 10);
        }       

    }

    @Override
    public boolean onTouch(View v, MotionEvent eve) {

        if(eve.getAction() == MotionEvent.ACTION_DOWN) {
            if(eve.getX() < 60 && eve.getY() < 60)  //touched at top left corner
                game.pause();
            else
                game.screenTouched(1);
        } else if(eve.getAction() == MotionEvent.ACTION_UP) {
            game.screenTouched(0);
        }
        return true;

    }

    @Override
    public void onBackPressed() {
        if(gameStat == STAT_GAME)
            game.pause();
        else if(gameStat == STAT_MENU)
            finish();
    }

    /**
     * create a new level
     * @param stat there are died, newgame, nextlevel
     * @see Game#HEG_NEWGAME
     * @see Game#HEG_NEXT_LEVEL
     * @see Game#HEG_DIED
     */
    void newGame(int stat) {

        switch(stat) {
            case Game.HEG_DIED:
                GameRule.loseApl();
                break;
            case Game.HEG_NEWGAME:
                curLevel = 0;
                GameRule.resetApl();
                startTime = System.currentTimeMillis();
                pauseTime = startTime;
                break;
            case Game.HEG_NEXT_LEVEL:
                curLevel++;
                GameRule.saveApl();
                break;
        }

        if(curLevel == levelCnt) {
            game.win();
            return;
        }

        Runnable sGame = new Runnable() {
            public void run() {
                gameStat = Main.STAT_GAME;
                strtTick();
                game.start();
            }
        };

        game = new Game(this, curLevel);
        game.setOnTouchListener(this);

        lay.removeAllViews();
        lay.addView(game);

        if(gameStat == Main.STAT_MENU) {
            hideMenu(sGame);            
        } else {
            sGame.run();
        }
    }

    /**
     * show the start menu of the game
     */
    void showMenu() {
        gameStat = Main.STAT_MENU;
        isRunning = false;

        lay.removeView(menu);
        lay.addView(menu);

        if(Build.VERSION.SDK_INT < 12) {
            if(lay.getChildCount() > 1)
                lay.removeView(game);
        } else {
            menu.animate().y(0).withEndAction(new Runnable() {
                public void run() {
                    if(lay.getChildCount() > 1)
                        lay.removeView(game);               
                }
            });
        }
    }
    /**
     * hide the start menu 
     * @param run action after hiding
     */
    private void hideMenu(Runnable run) {
        lay.addView(menu);
        if(Build.VERSION.SDK_INT >= 12) {
            menu.animate().y(-scrH).withEndAction(run);
        } else {
            lay.removeView(menu);
            run.run();
        }
    }

    /**
     * play sound
     * @param snd 0 = start, 1 = apple, 2 = die, 3 = win
     */
    static void playSnd(int snd) {
        snds.play(sndId[snd], 1.0f, 1.0f, 0, 0, 1.0f);
    }

    String getTime() {
        long time = pauseTime > 0 ? pauseTime : System.currentTimeMillis();
        return formatter.format(time - startTime);
    }

    void pauseTime() {
        pauseTime = System.currentTimeMillis();
    }

    void resumeTime() {
        if(pauseTime > 0) {
            startTime += System.currentTimeMillis() - pauseTime;
            pauseTime = 0;
        }
    }
}
