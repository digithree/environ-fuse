package com.surfacetension.environfuse.gfxParticles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.widget.Space;

import com.surfacetension.environfuse.AppSupport;
import com.surfacetension.environfuse.gfx.ColorUtils;
import com.surfacetension.environfuse.gfx.SpaceUtils;

/**
 * Created by simonkenny on 12/05/15.
 */
public class ParticleSun extends GfxParticle {

    private float size;

    private final float MOVE_FACTOR = 1.f;

    public ParticleSun() {
        super();
    }

    protected void init() {
        super.init();
        this.pos = new PointF(0.f * canvasWidth, 0.5f * canvasHeight);
        this.targetPos = new PointF(0.5f * canvasWidth, 0.5f * canvasHeight);
        this.size = 0.2f * (canvasWidth < canvasHeight ? canvasWidth : canvasHeight);
        this.mainPaint = new Paint();
        this.mainPaint.setColor(Color.argb(255, 255, 0, 0));
        state = STATE_MOVING_TO_TARGET;
    }

    public void draw(Canvas canvas) {
        if( !firstDraw ) {
            canvasWidth = canvas.getWidth();
            canvasHeight = canvas.getHeight();
            firstDraw = true;
            init();
        }
        canvas.drawRect(bounds, mainPaint);
    }


    public void update(double deltaTime) {
        if( !firstDraw ) {
            return;
        }
        mainPaint.setColor(ColorUtils.getColForNormVal(ColorUtils.daylightCols,
                AppSupport.getInstance().getWeatherInfo().getDaylight()));
        if( state == STATE_MOVING_TO_TARGET ) {
            if( SpaceUtils.hasMinDistance(pos, targetPos)  ) {
                moveVec = new PointF();
                state = STATE_STILL;
                Log.d("ParticleSun", "STATE BECOMING STILL");
            } else {
                PointF newMoveVec = SpaceUtils.axisDifference(pos, targetPos);
                //float len = moveVec.length();
                //moveVec.x = moveVec.x / len;
                //moveVec.y = moveVec.y / len;
                moveVec.x += newMoveVec.x * MOVE_FACTOR;
                moveVec.y += newMoveVec.y * MOVE_FACTOR;
                applyMoveVec(deltaTime);
            }
        }
        // update position
        float adjustedHalfSize = size * 0.5f;
        this.bounds = new RectF(
                pos.x - adjustedHalfSize,
                pos.y - adjustedHalfSize,
                pos.x + adjustedHalfSize,
                pos.y + adjustedHalfSize
        );
    }
}
