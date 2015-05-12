package com.surfacetension.environfuse.gfxParticles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.surfacetension.environfuse.AppSupport;
import com.surfacetension.environfuse.WeatherInfo;
import com.surfacetension.environfuse.gfx.ColorUtils;
import com.surfacetension.environfuse.gfxIcons.GfxElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonkenny on 12/05/15.
 */
public class DrawableParticlesView extends View {

    public DrawableParticlesView(Context context) {
        super(context);
    }

    public DrawableParticlesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    // threading
    private AnimationThread animationThread;
    private Object drawLock = new Object();


    protected void onDraw (Canvas canvas) {
        synchronized (drawLock) {
            AppSupport.getInstance().getParticlesContainer().draw(canvas);
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d("DrawableParticlesView", "onAttachedToWindow");
        // animation
        if( animationThread != null ) {
            if( !animationThread.isRunning() ) {
                Log.d("DrawableParticlesView","Creating animation thread...");
                animationThread = new AnimationThread();
                animationThread.setRunning(true);
                animationThread.start();
            }
        } else {
            Log.d("DrawableParticlesView","Creating animation thread...");
            animationThread = new AnimationThread();
            animationThread.setRunning(true);
            animationThread.start();
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d("DrawableParticlesView","onDetachedFromWindow");
        if( animationThread != null ) {
            Log.d("DrawableParticlesView","animation stopping");
            boolean retry = true;
            animationThread.setRunning(false);
            while (retry) {
                try {
                    animationThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                }
            }
            Log.d("DrawableParticlesView","animation stopped by request");
        } else {
            Log.d("DrawableParticlesView","animation stopped by system (thread is null)");
        }
    }

    public class AnimationThread extends Thread {
        private boolean run = false;
        private boolean firstTime = true;

        private double lastDrawTime;

        public boolean isRunning() {
            return run;
        }

        public void run() {
            while (run) {
                synchronized (drawLock) {
                    doAnimation();
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setRunning(boolean b) {
            run = b;
        }

        private void doAnimation() {
            if( firstTime ) {
                lastDrawTime = ((double) SystemClock.uptimeMillis()) / 1000.f;
                firstTime = false;
            }

            double thisTime = ((double)SystemClock.uptimeMillis()) / 1000.f;
            double deltaTime = thisTime - lastDrawTime;

            // do updates
            AppSupport.getInstance().getParticlesContainer().update(deltaTime);


            postInvalidate();

            lastDrawTime = thisTime;
        }
    }
}