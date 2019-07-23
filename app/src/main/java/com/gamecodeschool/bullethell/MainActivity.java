package com.gamecodeschool.bullethell;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends Activity {

    private BulletHellGame mBHGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VisibilityManager.hideSystemUI(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getRealSize(screenSize);

        mBHGame = new BulletHellGame(this, screenSize);
        setContentView(mBHGame);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            VisibilityManager.hideSystemUI(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBHGame.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBHGame.pause();
    }
}
