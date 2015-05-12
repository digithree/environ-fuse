package com.surfacetension.environfuse.gfxParticles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.surfacetension.environfuse.AppSupport;
import com.surfacetension.environfuse.gfx.ColorUtils;
import com.surfacetension.environfuse.gfx.SpaceUtils;

/**
 * Created by simonkenny on 12/05/15.
 */
public class ParticleCloud extends GfxParticle {

    private boolean firstUpdate = false;
    private float size;
    private float radius;
    private PointF sunPos;

    private final float MOVE_FACTOR = 1.f;
    private final int ALPHA = 150;

    private float ROTATION_SPEED_MARGIN = 0.1f;
    private float ROTATION_SPEED = 0.15f;

    private float MOVE_WEIGHT_MARGIN = 0.1f;

    private float RADIUS_MARGIN = 0.01f;
    private final float RADIUS_MARGIN_ADD_WIND = 0.05f;

    public ParticleCloud(PointF sunPos, float radius) {
        super();
        this.radius = radius;
        this.sunPos = new PointF(sunPos.x, sunPos.y);
    }

    protected void init() {
        super.init();
        float randomNumber = (float)Math.random();
        if( randomNumber < 0.25f ) {
            this.pos = new PointF(randomNumber * 4.f * canvasWidth, 0.f);
        } else if( randomNumber < 0.5f ) {
            this.pos = new PointF(1.f * canvasWidth, (randomNumber-0.25f) * 4.f * canvasHeight);
        } else if( randomNumber < 0.75f ) {
            this.pos = new PointF((randomNumber-0.5f) * 4.f * canvasWidth, 1.f * canvasHeight);
        } else {
            this.pos = new PointF(0.f, (randomNumber-0.75f) * 4.f * canvasHeight);
        }
        // mod radius margin by cloudiness
        RADIUS_MARGIN += (RADIUS_MARGIN_ADD_WIND * AppSupport.getInstance().getWeatherInfo().getCloudiness());
        this.radius += -RADIUS_MARGIN + ((float)Math.random() * RADIUS_MARGIN * 2.f);
        this.radius *= (canvasWidth < canvasHeight ? canvasWidth : canvasHeight);
        // set targetPos in first update
        this.size = 0.05f * (canvasWidth < canvasHeight ? canvasWidth : canvasHeight);
        this.mainPaint = new Paint();
        this.mainPaint.setColor(Color.argb(ALPHA, 100, 100, 255));
        state = STATE_MOVING_TO_TARGET;
        float adjustedHalfSize = size * 0.5f;
        this.bounds = new RectF(
                pos.x - adjustedHalfSize,
                pos.y - adjustedHalfSize,
                pos.x + adjustedHalfSize,
                pos.y + adjustedHalfSize
        );
        //ROTATION_SPEED += -ROTATION_SPEED_MARGIN + ((float)Math.random() * ROTATION_SPEED_MARGIN * 2.f);
        moveWeight += -MOVE_WEIGHT_MARGIN + ((float)Math.random() * MOVE_WEIGHT_MARGIN * 2.f);
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
        if( firstDraw && !firstUpdate ) {
            double theta = Math.atan2((double)(this.pos.y-sunPos.y),
                    (double)(this.pos.x-sunPos.x));
            this.targetPos.x = sunPos.x + ((float)Math.cos(theta) * radius);
            this.targetPos.y = sunPos.y + ((float)Math.sin(theta) * radius);
            firstUpdate = true;
        }
        mainPaint.setColor(ColorUtils.getColForNormVal(ColorUtils.rainCols,
                AppSupport.getInstance().getWeatherInfo().getRainfall()));
        mainPaint.setAlpha(ALPHA);
        if( state == STATE_MOVING_TO_TARGET ) {
            if( SpaceUtils.hasMinDistance(pos, targetPos)  ) {
                moveVec = new PointF();
                state = STATE_MOVING_IN_ROTATION_AROUND_TARGET;
                Log.d("ParticleCloud", "STATE BECOMING STILL");
            } else {
                PointF newMoveVec = SpaceUtils.axisDifference(pos, targetPos);
                moveVec.x += newMoveVec.x * moveWeight * MOVE_FACTOR;
                moveVec.y += newMoveVec.y * moveWeight * MOVE_FACTOR;
                applyMoveVec(deltaTime);
            }
        } else if( state == STATE_MOVING_IN_ROTATION_AROUND_TARGET ) {
            float windSpeedFactor = 1.f + AppSupport.getInstance().getWeatherInfo().getWindSpeed();
            double theta = ROTATION_SPEED * moveWeight * windSpeedFactor * deltaTime;
            PointF newPos = new PointF();
            newPos.x = (float)((pos.x-sunPos.x) * Math.cos(theta)) - (float)((pos.y-sunPos.y) * Math.sin(theta));
            newPos.y = (float)((pos.y-sunPos.y) * Math.cos(theta)) + (float)((pos.x-sunPos.x) * Math.sin(theta));
            pos.x = newPos.x + sunPos.x;
            pos.y = newPos.y + sunPos.y;
            //state = STATE_STILL;
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
