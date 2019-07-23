package com.gamecodeschool.bullethell;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

class Mario {
    private final RectF mRect;
    private final float mWidth;
    private final float mHeight;
    private boolean mTeleporting = false;

    private final Bitmap mBitmap;

    Mario(Context context, Point screenPixels) {
        mHeight = (float) screenPixels.y / 10;
        mWidth = mHeight / 1.55f;

        int marioLeft = screenPixels.x / 2;
        int marioTop = screenPixels.y / 2;
        mRect = new RectF(marioLeft, marioTop, marioLeft + mWidth, marioTop + mHeight);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mario);
        mBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(mWidth), Math.round(mHeight), false);
    }

    boolean teleport(PointF newPosition, Bullet[] bullets) {
        if (!mTeleporting) {
            RectF rect = new RectF();
            rect.left = newPosition.x - mWidth / 2;
            rect.top = newPosition.y - mHeight / 2;
            rect.right = rect.left + mWidth;
            rect.bottom = rect.top + mHeight;

            for (Bullet bullet: bullets) {
                if (RectF.intersects(bullet.getRect(), rect)) {
                    return false;
                }
            }

            mRect.set(rect);
            mTeleporting = true;
            return true;
        }

        return false;
    }

    void setTeleportAvailable() {
        mTeleporting = false;
    }

    RectF getRect() {
        return mRect;
    }

    Bitmap getBitmap() {
        return mBitmap;
    }
}
