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

    private enum GameStat {	//stats of the game
        NOTHING, START, NORMAL, DIED, PAUSE, NEXTLEVEL, WIN
    };

    static GameStat gameStat;	//to get the stats of the game

    private static Main main;

    private Paint paint;
    private Entity[] ents;
    private Bitmap aplImg, pauseImg;

    private static int cnt;	//counting animation

    private int[] ctrl = new int[4];
    private int level;
    private int hegInd;
    private int titSize, txtSize;	//text size of title and text
    private int titY, txtY;				//text Y position of title and text

    /** 
     * @param main the main class of game
     * @param level 0~numbers of level -1
     */
    public Game(Main main, int level) {
        super(main);

        /*setting variables*/
        Game.main = main;
        this.level = level;
        gameStat = GameStat.NOTHING;
        cnt = 100;
        hegInd = -1;

        titSize =  Main.scrH / 8;
        txtSize = Main.scrH / 18;

        titY = Main.scrH / 3;
        txtY = Main.scrH / 2;	

        paint = new Paint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        aplImg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(),
                    R.drawable.apple), (int) (30 * (Main.scrW / 750f)), (int) (40 * (Main.scrH / 500f)), false);
        pauseImg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(),
                    R.drawable.pause), (int) (25 * (Main.scrW / 750f)), (int) (30 * (Main.scrH / 500f)), false);

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
                if(tmpType == 0)
                    hegInd = i;
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
            if(gameStat == GameStat.DIED) {	//resume the dieing screen
                main.newGame(Game.HEG_DIED);
                return;
            }
            else if(gameStat == GameStat.PAUSE) {
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
        gameStat = GameStat.START;
        cnt = 100;
    }

    /**
     *to  pause the game
     */
    void pause() {
        switch(gameStat) {
            case NORMAL:
                main.pauseTime();
                gameStat = GameStat.PAUSE;
                cnt = 100;
                break;
            case PAUSE:
            case DIED:
                gameStat = GameStat.NOTHING;
                Main.playSnd(2);
                main.showMenu();
                cnt = 100;
                break;
            default:
                //doNothing
        }
    }

    /**
     * to resume the game
     */
    void resume() {
        gameStat = GameStat.NORMAL;
        main.resumeTime();
    }

    /**
     * the player won the game
     */
    void win() {
        gameStat = GameStat.WIN;
        cnt = 100;
        main.pauseTime();
    }

    /**
     * call this every tick, do everything and repaint
     */
    void tick() {

        if(gameStat == GameStat.NORMAL) {	//the game is running normaly

            for(int i = 0; i < ctrl.length; i++) {		//move hedgehog from user's control
                if(ctrl[i] != 0 && hegInd != -1) {
                    ents[hegInd].advMove(i, ctrl[i]);
                    ctrl[i] = 0;
                }
            }

            for(int i = 0; i < ents.length; i++) {
                if(ents[i] == null) continue;
                ents[i].tick();			
            }

        } else if(gameStat == GameStat.NEXTLEVEL) {	//next level
            main.pauseTime();
            main.newGame(HEG_NEXT_LEVEL);
            return;
        }

        if(cnt > 0)
            cnt--;
        else if(cnt == 0) {
            if(gameStat == GameStat.START) {
                gameStat = GameStat.NORMAL;
                main.resumeTime();
            } else if(gameStat == GameStat.WIN) {
                main.showMenu();
            }
        }

        invalidate();

    }

    @Override
    protected void onDraw(Canvas can) {
        can.drawColor(Color.parseColor("#808080"));

        if(gameStat == GameStat.NORMAL) {			//the game is playing
            drawImgs(can);
        } else if(gameStat == GameStat.START) {		//the game is in start screen

            drawImgs(can);

            setTextFont(0);
            paint.setTextAlign(Paint.Align.CENTER);

            can.drawText(main.getString(R.string.level) + (level + 1), Main.scrW / 2, titY, paint);

        } else if(gameStat == GameStat.DIED) {	//the game is in dieing screen
            drawImgs(can);

            setTextFont(0);
            paint.setTextAlign(Paint.Align.CENTER);

            can.drawText(main.getString(R.string.game_over), Main.scrW / 2, titY, paint);

            setTextFont(1);

            can.drawText(main.getString(R.string.contin), Main.scrW / 2, txtY, paint);
            can.drawText(main.getString(R.string.quit), Main.scrW / 2, txtY + txtSize, paint);

        } else if(gameStat == GameStat.PAUSE) {	//the game is in pause
            drawImgs(can);

            setTextFont(0);

            paint.setTextAlign(Paint.Align.CENTER);

            can.drawText(main.getString(R.string.paus), Main.scrW / 2, titY, paint);

            setTextFont(1);

            can.drawText(main.getString(R.string.contin), Main.scrW / 2, txtY, paint);
            can.drawText(main.getString(R.string.quit), Main.scrW / 2, txtY + txtSize, paint);


        } else if(gameStat == GameStat.WIN) {	//player won

            setTextFont(0);
            paint.setTextAlign(Paint.Align.CENTER);

            can.drawText(main.getString(R.string.win), Main.scrW / 2, titY, paint);

            setTextFont(1);

            can.drawText(main.getString(R.string.score) + GameRule.getApls(), Main.scrW / 2, txtY, paint);
            can.drawText(main.getString(R.string.time) + main.getTime(), Main.scrW / 2, txtY + txtSize, paint);

        }
    }

    /**
     * draw images of entities and icons
     * @param can
     */
    private void drawImgs(Canvas can) {
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(aplImg.getWidth());

        for(int i = 0; i < ents.length; i++) {
            if(ents[i] == null) continue;
            can.drawBitmap(ents[i].getImg(), (int) (ents[i].pos[0] * (Main.scrW / 750f)), (int) (ents[i].pos[1] * (Main.scrH / 500f)), paint);
        }

        can.drawBitmap(aplImg, Main.scrW / 20 * 17, aplImg.getHeight() / 3, paint);
        can.drawText(" = " + GameRule.getApls() , Main.scrW / 20 * 17 + aplImg.getWidth(), aplImg.getHeight(), paint);
        can.drawBitmap(pauseImg, pauseImg.getWidth() / 3, pauseImg.getHeight() / 3, paint);
        can.drawText(main.getTime(), pauseImg.getWidth() * 2, aplImg.getHeight(), paint);

    }

    /**
     * set color and font in game
     * @param type 0 = title, 1 = text
     */
    private void setTextFont(int type) {
        int s;
        switch(type) {
            case 0:
                s = (int) (titSize / 5f * (cnt > 95 ? 100 - cnt : 5));
                paint.setColor(Color.YELLOW);
                paint.setTextSize(s);
                break;
            case 1:
                s = (int) (txtSize / 5f * (cnt > 95 ? 100 - cnt : 5));
                paint.setColor(Color.BLACK);
                paint.setTextSize(s);
                break;
        }
    }

    /**
     * start a new game
     */
    static void newGame(int stat) {
        switch(stat) {
            case Game.HEG_NEXT_LEVEL:
                gameStat = GameStat.NEXTLEVEL;
                break;
            case Game.HEG_DIED:
                gameStat = GameStat.DIED;
                cnt = 100;
                Main.playSnd(2);
                main.pauseTime();
                break;
        }
    }

}
