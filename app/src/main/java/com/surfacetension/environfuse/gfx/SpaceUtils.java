package com.surfacetension.environfuse.gfx;

import android.graphics.PointF;

/**
 * Created by simonkenny on 12/05/15.
 */
public class SpaceUtils {

    private static float MIN_TARGET_DISTANCE = 10.f;

    public static boolean hasMinDistance(PointF p1, PointF p2) {
        return (distance(p1,p2) < MIN_TARGET_DISTANCE);
    }

    public static double distance(PointF p1, PointF p2) {
        return axisDifference(p1,p2).length();
    }

    public static PointF axisDifference(PointF p1, PointF p2) {
        return new PointF(
                p2.x-p1.x,
                p2.y-p1.y
        );
    }
}
