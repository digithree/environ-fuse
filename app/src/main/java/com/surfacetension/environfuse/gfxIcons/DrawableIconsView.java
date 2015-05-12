package com.surfacetension.environfuse.gfxIcons;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.surfacetension.environfuse.AppSupport;
import com.surfacetension.environfuse.Utils;
import com.surfacetension.environfuse.WeatherInfo;
import com.surfacetension.environfuse.gfx.ColorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonkenny on 08/03/15.
 */
public class DrawableIconsView extends View {

    public DrawableIconsView(Context context) {
        super(context);
        init();
    }

    public DrawableIconsView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.rgb(220, 220, 220));
    }

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
        bgPaint.setColor(ColorUtils.getColForNormVal(ColorUtils.daylightCols, weatherInfo.getDaylight()));
    }

    private void createForeground() {
        int fgCol = ColorUtils.getColForNormVal(ColorUtils.tempCols, weatherInfo.getTemp());
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
                (GfxElement) new HumidityGfxElement(fgCol, weatherInfo.getHumidity(), CANVAS_WIDTH, CANVAS_HEIGHT)
        );
        Log.d("DrawableView", "Winddir: " + weatherInfo.getWindDirection());
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
}
