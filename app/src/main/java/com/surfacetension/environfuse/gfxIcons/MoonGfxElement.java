package com.surfacetension.environfuse.gfxIcons;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonkenny on 08/03/15.
 */
public class MoonGfxElement extends GfxElement {
    private final int NUM_POINTS_CIRCLE = 40;
    // modified
    private final float CIRCLE_RADIUS = 0.2f; // TODO : change to 0.3f
    private final float CIRCLE_FLIP_POINT = 0.4f;

    public MoonGfxElement(int col, float scale, float width, float height) {
        super(col, scale, width, height);
        init(width,height);
    }

    public void init(float width, float height) {
        // scale
        float radius = CIRCLE_RADIUS * width * scale;
        //CIRCLE_TRI_DIST *= scale;
        // create points
        // prep
        float centerX = width * 0.5f;
        float centerY = height * 0.45f;
        float flipPointX = centerX - (radius*CIRCLE_FLIP_POINT);
        // circle
        double angle = (Math.PI * 2) / (double)NUM_POINTS_CIRCLE;
        List<PointF> pointsf = new ArrayList<>();
        for( int i = 0 ; i < NUM_POINTS_CIRCLE ; i++ ) {
            float p1x = centerX + (radius * (float) Math.cos(angle * (double) i));
            if( p1x < flipPointX ) {
                p1x = flipPointX + (flipPointX - p1x);
            }
            float p2x = centerX + (radius * (float) Math.cos(angle * (double) (i + 1)));
            if( p2x < flipPointX ) {
                p2x = flipPointX + (flipPointX - p2x);
            }
            pointsf.add(new PointF(
                    p1x,
                    centerY + (radius * (float) Math.sin(angle * (double) i))
            ));
        }
        createShapeFromPoints(pointsf);
    }
}

