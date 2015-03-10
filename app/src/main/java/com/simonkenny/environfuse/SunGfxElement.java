package com.simonkenny.environfuse;

import android.graphics.Canvas;
import android.graphics.PointF;
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
    private final float CIRCLE_RADIUS = 0.13f; // TODO : change to 0.3f
    private final float CIRCLE_TRI_DIST = CIRCLE_RADIUS * 1.4f;
    private final float CIRCLE_TRI_PART = 0.05f;

    SunGfxElement(int col, float scale, float width, float height) {
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
        // circle
        double angle = (Math.PI * 2) / (double)NUM_POINTS_CIRCLE;
        List<PointF> pointsf = new ArrayList<>();
        for( int i = 0 ; i < NUM_POINTS_CIRCLE ; i++ ) {
            pointsf.add(new PointF(
                    centerX + (radius * (float)Math.cos(angle*(double)i)),
                    centerY + (radius * (float)Math.sin(angle*(double)i))
            ));
        }
        createShapeFromPoints(pointsf);
        pointsf.clear();
        // triangles
        // make original points
        float triDist = CIRCLE_TRI_DIST * width * scale;
        float triPart = CIRCLE_TRI_PART * width * scale;
        PointF p1 = new PointF(centerX, centerY - triDist - triPart);
        PointF p2 = new PointF(centerX - triPart, centerY - triDist);
        PointF p3 = new PointF(centerX + triPart, centerY - triDist);
        PointF center = new PointF(centerX, centerY);
        angle = (Math.PI * 2) / (double)NUM_TRIANGLES;
        for( int i = 0 ; i < NUM_TRIANGLES ; i++ ) {
            PointF p1_new = Utils.rotatePoint(p1,center,(float)(angle*(float)i));
            PointF p2_new = Utils.rotatePoint(p2,center,(float)(angle*(float)i));
            PointF p3_new = Utils.rotatePoint(p3,center,(float)(angle*(float)i));
            pointsf.add(new PointF(p1_new.x, p1_new.y));
            pointsf.add(new PointF(p2_new.x, p2_new.y));
            pointsf.add(new PointF(p3_new.x, p3_new.y));
            createShapeFromPoints(pointsf);
            pointsf.clear();
        }
    }
}
