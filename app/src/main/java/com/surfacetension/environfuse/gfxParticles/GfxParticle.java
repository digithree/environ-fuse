package com.surfacetension.environfuse.gfxParticles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by simonkenny on 12/05/15.
 */
public class GfxParticle {

    public static int STATE_STILL = 0;
    public static int STATE_MOVING_TO_TARGET = 1;
    public static int STATE_MOVING_IN_ROTATION_AROUND_TARGET = 2;

    private final float VECTOR_DECAY_RATE = 0.5f;
    private float MAX_MOVE_VECTOR = 0.9f;

    protected boolean firstDraw = false;
    protected float canvasWidth, canvasHeight;

    protected PointF pos;
    protected float moveWeight;
    protected PointF moveVec;
    protected PointF targetPos;

    protected Paint mainPaint;
    protected RectF bounds;

    protected int state;

    public GfxParticle() {
    }

    protected void init() {
        pos = new PointF(0.5f * canvasWidth, 0.5f * canvasHeight);
        moveWeight = 1.f;
        moveVec = new PointF(0.f, 0.f);
        targetPos = new PointF(pos.x, pos.y);
        mainPaint = null;
        bounds = new RectF(0.f, 0.f, canvasWidth, canvasHeight);
        MAX_MOVE_VECTOR *= (canvasWidth < canvasHeight ? canvasWidth : canvasHeight);
        state = STATE_STILL;
    }

    public PointF getPos() {
        return pos;
    }

    public void draw(Canvas canvas) {
        if( !firstDraw ) {
            canvasWidth = canvas.getWidth();
            canvasHeight = canvas.getHeight();
            firstDraw = true;
            init();
        }
    }

    public void update(double deltaTime) {
        // update
    }

    // tools
    protected void applyMoveVec(double deltaTime) {
        if( moveVec.length() > MAX_MOVE_VECTOR ) {
            float len = moveVec.length();
            moveVec.x = (moveVec.x / len) * MAX_MOVE_VECTOR;
            moveVec.y = (moveVec.y / len) * MAX_MOVE_VECTOR;
        }
        pos.x += deltaTime * moveVec.x;
        pos.y += deltaTime * moveVec.y;
        moveVec.x *= VECTOR_DECAY_RATE;
        moveVec.y *= VECTOR_DECAY_RATE;
    }
}
