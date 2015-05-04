package com.surfacetension.environfuse;

import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonkenny on 09/03/15.
 */
public class WindSpeedAndDirGfxElement extends GfxElement {

    private final float LINE_LENGTH = 0.2f;
    private final float LINE_ARROW_OFFSET = 0.08f;

    private final float rot;

    WindSpeedAndDirGfxElement(int col, float scale, float rot, float width, float height ) {
        super(col, scale, width, height);
        this.rot = rot;
        init(width,height);
    }

    public void init(float width, float height) {
        // scale
        float halfLength = LINE_LENGTH * 0.5f * width * scale;
        //CIRCLE_TRI_DIST *= scale;
        // create points
        // prep
        float centerX = width * 0.5f;
        float centerY = height * 0.2f;
        float xOffset = width * 0.3f;
        float yOffset = height * 0.6f;
        List<Line> lines = new ArrayList<>();
        // arrow
        // make original points
        float arrowOffset = LINE_ARROW_OFFSET * width * scale;
        PointF linep1 = new PointF(centerX, centerY - halfLength);
        PointF linep2 = new PointF(centerX, centerY + halfLength);
        PointF arrowp1 = new PointF(linep1.x-arrowOffset, linep1.y+arrowOffset);
        PointF arrowp2 = new PointF(linep1.x+arrowOffset, linep1.y+arrowOffset);
        PointF center = new PointF(centerX, centerY);
        // get angle
        float angle = ((float)Math.PI * 2.f) * (rot/360.f);
        // rotate points
        PointF linep1_new = Utils.rotatePoint(linep1,center,angle);
        PointF linep2_new = Utils.rotatePoint(linep2,center,angle);
        PointF arrowp1_new = Utils.rotatePoint(arrowp1,center,angle);
        PointF arrowp2_new = Utils.rotatePoint(arrowp2,center,angle);
        // create lines
        // TOP
        // left
        lines.add(new Line(linep1_new.x-xOffset, linep1_new.y, linep2_new.x-xOffset, linep2_new.y) );
        lines.add(new Line(linep1_new.x-xOffset, linep1_new.y, arrowp1_new.x-xOffset, arrowp1_new.y) );
        lines.add(new Line(linep1_new.x-xOffset, linep1_new.y, arrowp2_new.x-xOffset, arrowp2_new.y) );
        // right
        lines.add(new Line(linep1_new.x+xOffset, linep1_new.y, linep2_new.x+xOffset, linep2_new.y) );
        lines.add(new Line(linep1_new.x+xOffset, linep1_new.y, arrowp1_new.x+xOffset, arrowp1_new.y) );
        lines.add(new Line(linep1_new.x+xOffset, linep1_new.y, arrowp2_new.x+xOffset, arrowp2_new.y) );
        // BOTTOM
        // left
        lines.add(new Line(linep1_new.x-xOffset, linep1_new.y+yOffset, linep2_new.x-xOffset, linep2_new.y+yOffset) );
        lines.add(new Line(linep1_new.x-xOffset, linep1_new.y+yOffset, arrowp1_new.x-xOffset, arrowp1_new.y+yOffset) );
        lines.add(new Line(linep1_new.x-xOffset, linep1_new.y+yOffset, arrowp2_new.x-xOffset, arrowp2_new.y+yOffset) );
        // right
        lines.add(new Line(linep1_new.x+xOffset, linep1_new.y+yOffset, linep2_new.x+xOffset, linep2_new.y+yOffset) );
        lines.add(new Line(linep1_new.x+xOffset, linep1_new.y+yOffset, arrowp1_new.x+xOffset, arrowp1_new.y+yOffset) );
        lines.add(new Line(linep1_new.x+xOffset, linep1_new.y+yOffset, arrowp2_new.x+xOffset, arrowp2_new.y+yOffset) );
        // add lines
        createPointsFromLines(lines);
    }
}
