package com.surfacetension.environfuse;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonkenny on 08/03/15.
 */
public class GfxElement {

    protected final float scale;
    protected final Paint paintStroke;
    protected final Paint paintFill;
    // points
    protected float []points = null;
    protected List<Path> paths = new ArrayList<Path>();

    GfxElement(int col, float scale, float width, float height) {
        this.scale = scale;
        paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintStroke.setStrokeCap(Paint.Cap.ROUND);
        paintStroke.setStrokeJoin(Paint.Join.BEVEL);
        paintStroke.setColor(col);
        paintStroke.setStrokeWidth(Utils.mapRange(Constants.MIN_STROKE_WIDTH,Constants.MAX_STROKE_WIDTH,scale));
        paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFill.setStyle(Paint.Style.FILL_AND_STROKE);
        paintFill.setStrokeCap(Paint.Cap.ROUND);
        paintFill.setStrokeJoin(Paint.Join.BEVEL);
        paintFill.setColor(col);
        paintFill.setStrokeWidth(Utils.mapRange(Constants.MIN_STROKE_WIDTH,Constants.MAX_STROKE_WIDTH,scale));
    }

    // DON'T override this
    public void draw(Canvas canvas) {
        if( points != null ) {
            canvas.drawLines(points, paintStroke);
        }
        if( !paths.isEmpty() ) {
            for( Path path : paths ) {
                canvas.drawPath(path, paintFill);
            }
        }
    }

    protected void createPointsFromLines(List<Line> lines) {
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

    protected void createShapeFromPoints(List<PointF> pointsf) {
        Path path = new Path();
        boolean firstPoint = true;
        for( PointF p : pointsf ) {
            if( firstPoint ) {
                path.moveTo(p.x, p.y);
                firstPoint = false;
            } else {
                path.lineTo(p.x,p.y);
            }
        }
        path.lineTo(pointsf.get(0).x,pointsf.get(0).y);
        paths.add(path);
    }
}
