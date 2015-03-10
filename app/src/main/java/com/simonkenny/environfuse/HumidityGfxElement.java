package com.simonkenny.environfuse;

/**
 * Created by simonkenny on 09/03/15.
 */
public class HumidityGfxElement extends GfxElement {
    HumidityGfxElement(int col, float scale, float width, float height) {
        super(col, scale, width, height);
    }

    private final float DROP_SCALE = 0.05f;
    private final float DROP_FORMULA_COEF = 7.f;
    private final int NUM_DROP_POINTS = 40;

    @Override
    public void init(float width, float height) {
        double dropScale = DROP_SCALE * height * scale;
        // create points
        // prep
        float centerX = width * 0.4f;
        float centerY = height * 0.18f;
        // drop1
        for( int i = 0 ; i < NUM_DROP_POINTS ; i++ ) {
            double coef1 = (DROP_FORMULA_COEF / NUM_DROP_POINTS) * (double)i;
            double coef2 = (DROP_FORMULA_COEF / NUM_DROP_POINTS) * (double)(i+1);
            lines.add(new Line(
                    centerX + (float)((Math.sin(coef1)*sinToPower2(coef1/2.0))*dropScale),
                    centerY + (float)(-Math.cos(coef1)*dropScale),
                    centerX + (float)((Math.sin(coef2)*sinToPower2(coef2/2.0))*dropScale),
                    centerY + (float)(-Math.cos(coef2)*dropScale)
            ));
        }
        // prep
        centerX = width * 0.6f;
        // drop2
        for( int i = 0 ; i < NUM_DROP_POINTS ; i++ ) {
            double coef1 = (DROP_FORMULA_COEF / NUM_DROP_POINTS) * (double)i;
            double coef2 = (DROP_FORMULA_COEF / NUM_DROP_POINTS) * (double)(i+1);
            lines.add(new Line(
                    centerX + (float)((Math.sin(coef1)*sinToPower2(coef1/2.0))*dropScale),
                    centerY + (float)(-Math.cos(coef1)*dropScale),
                    centerX + (float)((Math.sin(coef2)*sinToPower2(coef2/2.0))*dropScale),
                    centerY + (float)(-Math.cos(coef2)*dropScale)
            ));
        }
        centerX = width * 0.5f;
        centerY = height * 0.1f;
        // drop1
        for( int i = 0 ; i < NUM_DROP_POINTS ; i++ ) {
            double coef1 = (DROP_FORMULA_COEF / NUM_DROP_POINTS) * (double)i;
            double coef2 = (DROP_FORMULA_COEF / NUM_DROP_POINTS) * (double)(i+1);
            lines.add(new Line(
                    centerX + (float)((Math.sin(coef1)*sinToPower2(coef1/2.0))*dropScale),
                    centerY + (float)(-Math.cos(coef1)*dropScale),
                    centerX + (float)((Math.sin(coef2)*sinToPower2(coef2/2.0))*dropScale),
                    centerY + (float)(-Math.cos(coef2)*dropScale)
            ));
        }
    }

    private double sinToPower7(double x) {
        return (1.0/64.0)*(
                (35.0*Math.sin(x))
                        -(21.0*Math.sin(3.0*x))
                        +(7.0*Math.sin(5.0*x))
                        -Math.sin(7.0*x)
        );
    }

    private double sinToPower2(double x) {
        return 0.5*(1.0 - Math.cos(2.0*x));
    }
}
