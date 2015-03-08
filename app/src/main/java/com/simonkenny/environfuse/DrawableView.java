package com.simonkenny.environfuse;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by simonkenny on 08/03/15.
 */
public class DrawableView extends View {

    public DrawableView(Context context) {
        super(context);
        init();
    }

    public DrawableView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.rgb(220,220,220));
    }

    private Paint bgPaint;

    protected void update() {
        if( AppSupport.getInstance().isWeatherChanged() ) {
            // set and translate weather
            // TODO : take this off the UI thread

        }
    }

    protected void onDraw (Canvas canvas) {
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), bgPaint);
    }
}
