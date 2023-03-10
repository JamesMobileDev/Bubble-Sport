package com.BibaSpirt.bubble;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.BibaSpirt.engine.GameEngine;
import com.BibaSpirt.engine.GameEvent;
import com.BibaSpirt.engine.GameObject;
import com.BibaSpirt.engine.Sprite;
import com.BibaSpirt.fragment.Observers;

import java.util.Random;


public class Player extends Sprite {

    public BubbleColor mBubbleColor;
    private final BubbleManager mBubbleManager;

    private final float mStartX, mStartY;
    private final float mMaxX;
    private final float mSpeed;
    private float mSpeedX, mSpeedY;
    private boolean mShoot = false;
    private int pos = 0;
    Observers gameOver;


    private final Random mRandom = new Random();

    public Player(GameEngine gameEngine, BubbleManager bubbleManager, Observers gameOver) {
        super(gameEngine, BubbleColor.BLUE.getImageResId());
        this.gameOver = gameOver;

        mBubbleColor = BubbleColor.BLUE;
        mBubbleManager = bubbleManager;

        mStartX = mScreenWidth / 2f;
        mStartY = mScreenHeight - 85f;

        mMaxX = gameEngine.mScreenWidth - mWidth;

        mSpeed = gameEngine.mPixelFactor * 3000 / 1000;   // We want to move at 3000px per second

        gameEngine.addGameObject(new BubblePath(), 2);
    }

    @Override
    public void startGame() {
        mX = mStartX - mWidth / 2f;
        mY = mStartY - mHeight / 2f;
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        // We shoot the bubble one time
        if (mShoot) {
            // We convert angle to x speed and y speed
            float sideX = gameEngine.mInputController.mXUp - mStartX;
            float sideY = gameEngine.mInputController.mYUp - mStartY;
            float angle = (float) Math.atan2(sideY, sideX);

            mSpeedX = (float) (mSpeed * Math.cos(angle));
            mSpeedY = (float) (mSpeed * Math.sin(angle));

            mShoot = false;
        }

        // Update position
        mX += mSpeedX * elapsedMillis;
        if (mX <= 0) {
            mX = 0;
            mSpeedX = -mSpeedX;
        }
        if (mX >= mMaxX) {
            mX = mMaxX;
            mSpeedX = -mSpeedX;
        }

        mY += mSpeedY * elapsedMillis;
        if (mY <= -mHeight || mY >= mScreenHeight) {   // Player out of screen
            setNextBubble();
        }
    }

    @Override
    public void onGameEvent(GameEvent gameEvent) {
        if (gameEvent == GameEvent.SHOOT) {
            mShoot = true;
        }
    }

    @Override
    public void onCollision(GameEngine gameEngine, Sprite otherObject) {
        if (otherObject instanceof Bubble) {
            Bubble bubble = (Bubble) otherObject;
            if (bubble.mBubbleColor != BubbleColor.BLANK && mY >= bubble.mY) {
                try {
                    mBubbleManager.addBubble(this, bubble);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    if (e.getMessage().equals("length=9; index=9")) {
                        gameOver.observeGameOver(true);
                    }
                }
                setNextBubble();
            }
        }
    }

    private void setNextBubble() {
        Log.d("@@@", "setNextBubble: " + pos);

        if (pos > 5) {
            pos = 0;
        }
        // Set random color to next bubble
//        BubbleColor color = BubbleColor.values()[mRandom.nextInt(BubbleColor.values().length - 1)];
        BubbleColor color = BubbleColor.values()[pos];
        pos++;
        mBubbleColor = color;
        mBitmap = getDefaultBitmap(mResources.getDrawable(color.getImageResId()));

        // Reset Position
        mSpeedX = 0;
        mSpeedY = 0;
        mX = mStartX - mWidth / 2f;
        mY = mStartY - mHeight / 2f;
    }

    class BubblePath extends GameObject {

        private final float mMaxX, mMinX;   // Border of the path
        private final float mRadius;   // Radius of the path

        private float mReflectX, mReflectY;   // Path reflection point
        private float mEndX, mEndY;   // Path end point

        private boolean mDraw = false;
        private final Paint mPaint = new Paint();

        public BubblePath() {
            mMaxX = mScreenWidth - mWidth / 2f;
            mMinX = mWidth / 2f;
            mRadius = mScreenWidth / 2f - mWidth / 2f;
        }

        @Override
        public void startGame() {
        }

        @Override
        public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
            if (gameEngine.mInputController.mAiming) {
                // We calculate ratio of two side
                float sideX = gameEngine.mInputController.mXDown - mStartX;
                float sideY = gameEngine.mInputController.mYDown - mStartY;
                float ratio = Math.abs(sideY / sideX);
                if (sideY >= 0) {
                    ratio = -ratio;
                }

                // Update reflection point and end point position
                mReflectX = sideX > 0 ? mMaxX : mMinX;
                mReflectY = mStartY - mRadius * ratio;

                mEndX = sideX > 0 ? mMinX : mMaxX;
                mEndY = mReflectY - mRadius * ratio * 2;

                mDraw = true;
            } else {
                mDraw = false;
            }
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (!mDraw) {
                return;
            }

//            Log.d("@@@", "mEndX: " + mEndX + " ");
//            Log.d("@@@", "mEndY: " + mEndY + " ");
////            Log.d("@@@", "mMaxX: " + mMaxX + " ");
////            Log.d("@@@", "mMinX: " + mMinX + " ");
////            Log.d("@@@", "mX: " + mX + " ");
////            Log.d("@@@", "mY: " + mY + " ");
////            Log.d("@@@", "mStartX: " + mStartX + " ");
////            Log.d("@@@", "mStartY: " + mStartY + " ");
////            Log.d("@@@", "mScreenHeight: " + mScreenHeight + " ");
////            Log.d("@@@", "mScreenWidth: " + mScreenWidth + " ");
//
//            Bitmap background = BitmapFactory.decodeResource(mResources, R.drawable.cursor);
//            Matrix matrix = new Matrix();
//            matrix.postTranslate(mX - 70, mY - 305);
////            matrix.postRotate(-25, mEndX, mEndY - 85);
//            canvas.drawBitmap(background, matrix, mPaint);

            mPaint.setColor(Color.parseColor("#01defe"));
            mPaint.setStrokeWidth(1);
            canvas.drawLine(mStartX, mStartY, mReflectX, mReflectY, mPaint);
            canvas.drawLine(mReflectX, mReflectY, mEndX, mEndY, mPaint);
        }

    }

}
