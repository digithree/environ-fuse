package com.simonkenny.environfuse;

import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonkenny on 08/03/15.
 */
public class SunGfxElement extends GfxElement {

    private final int NUM_POINTS_CIRCLE = 40;
    private final int NUM_TRIANGLES = 8;
    // modified
    private final float CIRCLE_RADIUS = 0.4f; // TODO : change to 0.3f
    private final float CIRCLE_TRI_DIST = 0.4f;

    SunGfxElement(int col, float scale, float width, float height) {
        super(col, scale, width, height);
    }

    @Override
    public void init(float width, float height) {
        // scale
        float radius = CIRCLE_RADIUS * width * scale;
        //CIRCLE_TRI_DIST *= scale;
        // create points
        // prep
        float centerX = width * 0.5f;
        float centerY = height * 0.5f;
        // circle
        double angle = (Math.PI * 2) / (double)NUM_POINTS_CIRCLE;
        for( int i = 0 ; i < NUM_POINTS_CIRCLE ; i++ ) {
            lines.add(new Line(
                    centerX + (radius * (float)Math.cos(angle*(double)i)),
                    centerY + (radius * (float)Math.sin(angle*(double)i)),
                    centerX + (radius * (float)Math.cos(angle*(double)(i+1))),
                    centerY + (radius * (float)Math.sin(angle*(double)(i+1)))
            ));
        }
        // triangles
        // TODO : do this
    }
}
