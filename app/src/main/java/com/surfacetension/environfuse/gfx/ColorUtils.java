package com.surfacetension.environfuse.gfx;

import com.surfacetension.environfuse.Utils;

/**
 * Created by simonkenny on 12/05/15.
 */
public class ColorUtils {
    // Raw data
    public static float[][] daylightCols = {
            {0.f, 0.2f, 20.f, 9.f, 60.f},       //norm daylight val, section size, r, g, b
            {0.2f, 0.4f, 171.f, 255.f, 255.f},
            {0.6f, 0.2f, 255.f, 252.f, 143.f},
            {0.8f, 0.1f, 255.f, 209.f, 192.f},
            {0.9f, 0.1f, 128.f, 78.f, 242.f},
            {1.f, 0.f, 20.f, 9.f, 60.f}         // section size not used in final entry
    };
    public static float[][] tempCols = {
            {0.f, 0.5f, 169.f, 237.f, 255.f},
            {0.5f, 0.15f, 5.f, 43.f, 232.f},
            {0.65f, 0.12f, 157.f, 243.f, 135.f},
            {0.77f, 0.23f, 253.f, 154.f, 91.f},
            {1.f, 0.f, 238.f, 36.f, 15.f}
    };

    public static float[][] rainCols = {
            {0.f, 0.3f, 255.f, 255.f, 255.f},
            {0.3f, 0.3f, 180.f, 30.f, 170.f},
            {0.6f, 0.4f, 100.f, 0.f, 255.f},
            {1.f, 0.f, 0.f, 0.f, 255.f}
    };

    public static int getColForNormVal( float [][]colArray, float val ) {
        if( val == 0.f ) {
            return android.graphics.Color.rgb((int) colArray[0][2], (int) colArray[0][3], (int) colArray[0][4]);
        } else if( val == 1.f ) {
            return android.graphics.Color.rgb((int) colArray[colArray.length - 1][2],
                    (int) colArray[colArray.length - 1][3], (int) colArray[colArray.length - 1][4]);
        }
        for( int i = 0 ; i < (colArray.length-1) ; i++ ) {
            if( val >= colArray[i][0] && val < colArray[i+1][0] ) {
                float rescaledVal = (val - colArray[i][0]) * (1.f/colArray[i][1]);
                return android.graphics.Color.rgb(
                        (int) Utils.mapRange(colArray[i][2], colArray[i + 1][2], rescaledVal),
                        (int) Utils.mapRange(colArray[i][3], colArray[i + 1][3], rescaledVal),
                        (int) Utils.mapRange(colArray[i][4], colArray[i + 1][4], rescaledVal)
                );
            }
        }
        return 0;
    }
}
