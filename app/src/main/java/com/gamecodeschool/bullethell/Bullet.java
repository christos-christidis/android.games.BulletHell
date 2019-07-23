package com.gamecodeschool.bullethell;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

class Bullet {

    private final RectF mRect = new RectF();
    private final PointF mVelocity = new PointF();

    private final float mWidth;
    private final float mHeight;

    Bullet(Point screenPixels) {
        mWidth = (float) screenPixels.x / 100;
        mHeight = (float) screenPixels.x / 100;

        mVelocity.set((float) screenPixels.x / 5, (float) screenPixels.x / 5);
    }

    void spawn(Point startingPosition, Point multFactors) {
        mRect.left = startingPosition.x;
        mRect.top = startingPosition.y;
        mRect.right = mRect.left + mWidth;
        mRect.bottom = mRect.top + mHeight;

        mVelocity.x *= multFactors.x;
        mVelocity.y *= multFactors.y;
    }

    void update(long fps) {
        mRect.left += mVelocity.x / fps;
        mRect.top += mVelocity.y / fps;

        mRect.right = mRect.left + mWidth;
        mRect.bottom = mRect.top - mHeight;
    }

    void reverseHorizontalDirection() {
        mVelocity.x = -mVelocity.x;
    }

    void reverseVerticalDirection() {
        mVelocity.y = -mVelocity.y;
    }

    RectF getRect() {
        return mRect;
    }

}
