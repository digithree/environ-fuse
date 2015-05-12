package com.surfacetension.environfuse.gfxParticles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.surfacetension.environfuse.AppSupport;
import com.surfacetension.environfuse.WeatherInfo;
import com.surfacetension.environfuse.gfx.ColorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonkenny on 12/05/15.
 *
 * Need to use this to keep particles information separate from the actual view
 * in which they are rendered
 */
public class ParticlesContainer {

    private List<GfxParticle> gfxParticles;
    private GfxParticle particleSun;

    private Paint bgPaint;

    // clouds
    private boolean cloudReleaseActive = true;
    private final float CLOUD_RELEASE_TIME = 10.f;
    private float curCloudReleaseTime = CLOUD_RELEASE_TIME;
    private final float CLOUD_STOP_RADIUS = 0.25f;
    private final int MAX_CLOUD_PARTICLES = 100;

    public ParticlesContainer() {
        // create list
        gfxParticles = new ArrayList<GfxParticle>();
        init();
    }

    public List<GfxParticle> getGfxParticles() {
        return gfxParticles;
    }

    private void init() {
        // background
        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.rgb(220, 220, 220));
        // sun
        particleSun = new ParticleSun();
        // add partiles to list
        gfxParticles.add(particleSun);
    }

    // DRAWING

    public void draw(Canvas canvas) {
        // bg
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), bgPaint);
        // fg
        for (GfxParticle particle : gfxParticles) {
            particle.draw(canvas);
        }
    }

    // LOGIC

    // update based on weather
    public void update(double deltaTime) {
        // weather update
        WeatherInfo weatherInfo = AppSupport.getInstance().getWeatherInfo();
        if (weatherInfo != null) {
            // bg
            bgPaint.setColor(ColorUtils.getColForNormVal(ColorUtils.tempCols, weatherInfo.getTemp()));
        }
        // release cloud particles
        // TODO : make this adjusted by raininess
        if( cloudReleaseActive ) {
            curCloudReleaseTime -= deltaTime;
            if (curCloudReleaseTime <= 0.f) {
                curCloudReleaseTime += CLOUD_RELEASE_TIME;
                int numClouds = (int)((float)MAX_CLOUD_PARTICLES * weatherInfo.getCloudiness());
                for( int i = 0 ; i < numClouds ; i++ ) {
                    gfxParticles.add(new ParticleCloud(particleSun.getPos(), CLOUD_STOP_RADIUS));
                }
                cloudReleaseActive = false;
            }
        }
        // update particles
        for (GfxParticle particle : gfxParticles) {
            particle.update(deltaTime);
        }
    }
}
