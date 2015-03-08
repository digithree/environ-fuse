package com.simonkenny.environfuse;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonkenny on 08/03/15.
 */
public class GfxElement {

    protected final float scale;
    protected final Paint paint;
    // points
    protected float []points = null;
    List<Line> lines = new ArrayList<Line>();

    GfxElement(int col, float scale, float width, float height) {
        this.scale = scale;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        paint.setColor(col);
        paint.setStrokeWidth(Utils.mapRange(Constants.MIN_STROKE_WIDTH,Constants.MAX_STROKE_WIDTH,scale));
        init(width, height);
        createPointsFromLines();
    }

    // DON'T override this
    public void draw(Canvas canvas) {
        if( points != null ) {
            canvas.drawLines(points, paint);
        }
    }

    protected void createPointsFromLines() {
        if( !lines.isEmpty() ) {
            points = new float[lines.size() * 4];
            int count = 0;
            for (Line line : lines) {
                points[(count * 4) + 0] = line.x1;
                points[(count * 4) + 1] = line.y1;
                points[(count * 4) + 2] = line.x2;
                points[(count * 4) + 3] = line.y2;
                count++;
            }
        }
    }

    // override this
    public void init(float width, float height) {}    // add points
}
