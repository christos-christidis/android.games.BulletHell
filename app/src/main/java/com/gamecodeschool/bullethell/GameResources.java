package com.gamecodeschool.bullethell;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

class GameResources {
    private SoundPool mSoundPool;
    private int mPainID = -1;
    private int mWoohooID = -1;
    private int mTeleportID = -1;
    private int mCantMoveID = -1;

    void initializeSounds(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(5).setAudioAttributes(audioAttributes).build();
        } else {
            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("mario-pain.wav");
            mPainID = mSoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("mario-woohoo.wav");
            mWoohooID = mSoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("teleport.ogg");
            mTeleportID = mSoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("mario-cant-move.ogg");
            mCantMoveID = mSoundPool.load(descriptor, 0);
        } catch (IOException e) {
            Log.e("error", "failed to load sound files");
        }
    }

    void playPainSound() {
        mSoundPool.play(mPainID, 1, 1, 0, 0, 1);
    }

    void playWoohooSound() {
        mSoundPool.play(mWoohooID, 1, 1, 0, 0, 1);
    }

    void playTeleportSound() {
        mSoundPool.play(mTeleportID, 0.3f, 0.3f, 0, 0, 1);
    }

    void playCantMoveSound() {
        mSoundPool.play(mCantMoveID, 1, 1, 0, 0, 1);
    }
}
