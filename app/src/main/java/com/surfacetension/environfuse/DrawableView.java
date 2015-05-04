package com.surfacetension.environfuse;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

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

    // Raw data
    private float [][]daylightCols = {
            {0.f, 0.2f, 20.f, 9.f, 60.f},       //norm daylight val, section size, r, g, b
            {0.2f, 0.4f, 171.f, 255.f, 255.f},
            {0.6f, 0.2f, 255.f, 252.f, 143.f},
            {0.8f, 0.1f, 255.f, 209.f, 192.f},
            {0.9f, 0.1f, 128.f, 78.f, 242.f},
            {1.f, 0.f, 20.f, 9.f, 60.f}         // section size not used in final entry
    };
    private float [][]tempCols = {
            {0.f, 0.5f, 169.f, 237.f, 255.f},
            {0.5f, 0.15f, 5.f, 43.f, 232.f},
            {0.65f, 0.12f, 157.f, 243.f, 135.f},
            {0.77f, 0.23f, 253.f, 154.f, 91.f},
            {1.f, 0.f, 238.f, 36.f, 15.f}
    };

    // Consts
    private int CANVAS_WIDTH = 200;
    private int CANVAS_HEIGHT = 200;
    private boolean canvasDimSet = false;
    // Variables
    private WeatherInfo weatherInfo = null;
    private int weatherRequestNumCopy = 0;
    private Paint bgPaint;
    // drawables
    private List<GfxElement> gfxElements = new ArrayList<GfxElement>();

    private void update() {
        Log.d("DrawableView", "Updating weather gfx from info");
        // set and translate weather
        // TODO : take this off the UI thread
        weatherInfo = AppSupport.getInstance().getWeatherInfo();
        if( weatherInfo != null ) {
            createBackground();
            createForeground();
        }
    }

    private void createBackground() {
        bgPaint.setColor(getColForNormVal(daylightCols, weatherInfo.getDaylight()));
    }

    private void createForeground() {
        int fgCol = getColForNormVal(tempCols, weatherInfo.getTemp());
        gfxElements.clear();
        if( weatherInfo.getDaylight() == 0.f || weatherInfo.getDaylight() == 1.f ) {
            gfxElements.add(
                    (GfxElement)new MoonGfxElement(fgCol, weatherInfo.getSunniness(), CANVAS_WIDTH, CANVAS_HEIGHT)
            );
        } else {
            gfxElements.add(
                    (GfxElement) new SunGfxElement(fgCol, weatherInfo.getSunniness(), CANVAS_WIDTH, CANVAS_HEIGHT)
            );
        }
        gfxElements.add(
                (GfxElement)new RainGfxElement(fgCol, weatherInfo.getRainfall(), CANVAS_WIDTH, CANVAS_HEIGHT)
        );
        gfxElements.add(
                (GfxElement)new HumidityGfxElement(fgCol, weatherInfo.getHumidity(), CANVAS_WIDTH, CANVAS_HEIGHT)
        );
        Log.d("DrawableView","Winddir: "+weatherInfo.getWindDirection());
        gfxElements.add(
                (GfxElement)new WindSpeedAndDirGfxElement(fgCol, weatherInfo.getWindSpeed(), weatherInfo.getWindDirection(),
                        CANVAS_WIDTH, CANVAS_HEIGHT)
        );
    }

    protected void onDraw (Canvas canvas) {
        // first time
        if( !canvasDimSet ) {
            CANVAS_WIDTH = canvas.getWidth();
            CANVAS_HEIGHT = canvas.getHeight();
            canvasDimSet = true;
        }
        // update first
        update();
        // bg
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), bgPaint);
        // fg
        for( GfxElement el : gfxElements ) {
            el.draw(canvas);
        }
    }

    private int getColForNormVal( float [][]colArray, float val ) {
        if( val == 0.f ) {
            return Color.rgb((int)colArray[0][2], (int)colArray[0][3], (int)colArray[0][4]);
        } else if( val == 1.f ) {
            return Color.rgb((int)colArray[colArray.length-1][2],
                    (int)colArray[colArray.length-1][3], (int)colArray[colArray.length-1][4]);
        }
        for( int i = 0 ; i < (colArray.length-1) ; i++ ) {
            if( val >= colArray[i][0] && val < colArray[i+1][0] ) {
                float rescaledVal = (val - colArray[i][0]) * (1.f/colArray[i][1]);
                return Color.rgb(
                        (int)Utils.mapRange(colArray[i][2], colArray[i + 1][2], rescaledVal),
                        (int)Utils.mapRange(colArray[i][3], colArray[i + 1][3], rescaledVal),
                        (int)Utils.mapRange(colArray[i][4], colArray[i + 1][4], rescaledVal)
                );
            }
        }
        return 0;
    }
}
