package com.gamecodeschool.bullethell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Locale;
import java.util.Random;

@SuppressWarnings("ViewConstructor")
class BulletHellGame extends SurfaceView implements Runnable {

    @SuppressWarnings("FieldCanBeLocal")
    private final boolean DEBUGGING = false;
    private long mTimeOfLastPrint = System.currentTimeMillis();
    private String mFpsString;

    private Thread mGameThread = null;
    private volatile boolean mPlaying;
    private boolean mPaused = true;

    private final SurfaceHolder mOurHolder;
    private final Paint mPaint = new Paint();

    private final int MILLIS_IN_SECOND = 1000;
    private long mFPS;

    private final Point mScreenPixels;

    private final int mFontSize;
    private final int mFontMargin;

    private final GameResources mGameResources = new GameResources();

    private final Bullet[] mBullets = new Bullet[100];
    private int mNumBullets;

    private final Random mRandom = new Random();

    private final Mario mMario;
    private int mNumHits;
    private final int SHIELD = 10;

    private long mStartGameTime;
    private long mBestGameTime;
    private long mTotalGameTime;

    BulletHellGame(Context context, Point screenPixels) {
        super(context);

        mScreenPixels = screenPixels;
        mFontSize = screenPixels.x / 20;
        mFontMargin = screenPixels.x / 50;

        mOurHolder = getHolder();

        mGameResources.initializeSounds(context);

        for (int i = 0; i < mBullets.length; i++) {
            mBullets[i] = new Bullet(screenPixels);
        }

        mMario = new Mario(context, screenPixels);

        startGame();
    }

    @Override
    public void run() {
        while (mPlaying) {
            long frameStartTime = System.currentTimeMillis();

            if (!mPaused) {
                update();
                detectCollisions();
            }

            draw();

            long timeThisFrame = System.currentTimeMillis() - frameStartTime;
            if (timeThisFrame > 0) {
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }
        }
    }

    void resume() {
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();

    }

    void pause() {
        mPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("error", "Joinging thread");
        }
    }

    private void startGame() {
        mNumHits = 0;
        mNumBullets = 0;

        mBestGameTime = Math.max(mBestGameTime, mTotalGameTime);
    }

    private void spawnBullet() {
        mNumBullets++;
        Bullet bullet = mBullets[mNumBullets - 1];

        Point newPosition = new Point();
        int velocityX;
        int velocityY;

        // if Mario is in left area, spawn bullet in right area and make it move right (and vice-versa)
        if (mMario.getRect().centerX() < mScreenPixels.x / 2) {
            newPosition.x = mRandom.nextInt(mScreenPixels.x / 2) + mScreenPixels.x / 2;
            velocityX = 1;
        } else {
            newPosition.x = mRandom.nextInt(mScreenPixels.x / 2);
            velocityX = -1;
        }

        // same for Y coordinate
        if (mMario.getRect().centerY() < mScreenPixels.y / 2) {
            newPosition.y = mRandom.nextInt(mScreenPixels.y / 2) + mScreenPixels.y / 2;
            velocityY = 1;
        } else {
            newPosition.y = mRandom.nextInt(mScreenPixels.y / 2);
            velocityY = -1;
        }

        bullet.spawn(newPosition, new Point(velocityX, velocityY));
    }

    private void update() {
        for (int i = 0; i < mNumBullets; i++) {
            mBullets[i].update(mFPS);
        }
    }

    private void detectCollisions() {
        for (int i = 0; i < mNumBullets; i++) {
            Bullet bullet = mBullets[i];
            if (bullet.getRect().bottom > mScreenPixels.y) {
                bullet.reverseVerticalDirection();
            } else if (bullet.getRect().top < 0) {
                bullet.reverseVerticalDirection();
            } else if (bullet.getRect().left < 0) {
                bullet.reverseHorizontalDirection();
            } else if (bullet.getRect().right > mScreenPixels.x) {
                bullet.reverseHorizontalDirection();
            }
        }

        // Has a bullet hit Mario
        for (int i = 0; i < mNumBullets; i++) {
            Bullet bullet = mBullets[i];
            if (RectF.intersects(bullet.getRect(), mMario.getRect())) {
                mGameResources.playPainSound();
                bullet.reverseHorizontalDirection();
                bullet.reverseVerticalDirection();

                mNumHits++;
                if (mNumHits == SHIELD) {

                    mPaused = true;
                    mTotalGameTime = System.currentTimeMillis() - mStartGameTime;

                    startGame();
                }
            }
        }
    }

    private void draw() {
        if (mOurHolder.getSurface().isValid()) {
            Canvas canvas = mOurHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 243, 111, 36));

            mPaint.setColor(Color.WHITE);

            if (DEBUGGING) {
                printDebuggingText(canvas);
            }

            for (int i = 0; i < mNumBullets; i++) {
                canvas.drawRect(mBullets[i].getRect(), mPaint);
            }

            canvas.drawBitmap(mMario.getBitmap(), mMario.getRect().left, mMario.getRect().top, mPaint);

            mPaint.setTextSize(mFontSize);
            canvas.drawText(String.format(Locale.getDefault(),
                    "Bullets: %d  Shield: %d  Best Time: %d",
                    mNumBullets, SHIELD - mNumHits, mBestGameTime / MILLIS_IN_SECOND),
                    mFontMargin, mFontSize, mPaint);

            // don't draw current time when paused
            if (!mPaused) {
                long secondsSurvived = (System.currentTimeMillis() - mStartGameTime) / MILLIS_IN_SECOND;
                canvas.drawText("Seconds survived: " + secondsSurvived,
                        mFontMargin, mFontMargin * 30, mPaint);
            }

            mOurHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mPaused) {
                    mStartGameTime = System.currentTimeMillis();
                    mPaused = false;
                }

                PointF newPosition = new PointF(event.getX(), event.getY());
                if (mMario.teleport(newPosition, mBullets)) {
                    mGameResources.playTeleportSound();
                    mGameResources.playWoohooSound();
                } else {
                    mGameResources.playCantMoveSound();
                }
                break;
            case MotionEvent.ACTION_UP:
                mMario.setTeleportAvailable();
                if (mNumBullets < 100) {
                    spawnBullet();
                }
                break;
        }

        return true;
    }

    // same as Pong Game
    private void printDebuggingText(Canvas canvas) {
        long timeSinceLastPrint = System.currentTimeMillis() - mTimeOfLastPrint;
        if (timeSinceLastPrint > 50) {
            mFpsString = "FPS: " + mFPS;
            mTimeOfLastPrint = System.currentTimeMillis();
        }

        int debugSize = mFontSize / 2;
        mPaint.setTextSize(debugSize);

        // SOS: contrary to documentation, the Rect returned by getTextBounds may NOT start at (0,0),
        // thus I should not use absolute values like rect.right, but rect.width() and rect.height()
        Rect bounds = new Rect();
        mPaint.getTextBounds(mFpsString, 0, mFpsString.length(), bounds);

        canvas.drawText(mFpsString, mScreenPixels.x - bounds.width() - mFontMargin,
                bounds.height() + mFontMargin, mPaint);

        int debugStart = 150;
        canvas.drawText("Mario left: " + mMario.getRect().left, 10, debugStart + debugSize * 2, mPaint);
        canvas.drawText("Mario top: " + mMario.getRect().right, 10, debugStart + debugSize * 3, mPaint);
        canvas.drawText("Mario right: " + mMario.getRect().top, 10, debugStart + debugSize * 4, mPaint);
        canvas.drawText("Mario bottom: " + mMario.getRect().bottom, 10, debugStart + debugSize * 5, mPaint);
        canvas.drawText("Mario centerX: " + mMario.getRect().centerX(), 10, debugStart + debugSize * 6, mPaint);
        canvas.drawText("Mario bottom: " + mMario.getRect().centerY(), 10, debugStart + debugSize * 7, mPaint);
    }
}
