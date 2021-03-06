/**
 * Manu.java
 */
package net.rio.superHedge;

import android.os.Build;
import android.view.*;
import android.widget.*;

/**
 * this class is the start menu 
 * @author rio
 *
 */
class Menu extends RelativeLayout {

    Main main;

    /**
     * @param main the main class of game
     */
    public Menu(Main main) {
        super(main);
        this.main = main;

        if(Build.VERSION.SDK_INT < 16)
            setBackgroundDrawable(main.getResources().getDrawable(R.drawable.background));
        else
            setBackground(main.getResources().getDrawable(R.drawable.background));
        setGravity(Gravity.CENTER);

        MenuItem playBtn = new MenuItem(main, this, Main.scrW / 3, Main.scrH / 5, main.getString(R.string.play));

        addView(playBtn);
    }

    /**
     * the MenuItem has been clicked
     */
    void onClick(final View v) {
        Main.playSnd(0);

        main.newGame(Game.HEG_NEWGAME);
    }

}
