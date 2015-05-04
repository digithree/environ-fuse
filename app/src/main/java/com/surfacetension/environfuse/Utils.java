package com.surfacetension.environfuse;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by simonkenny on 22/01/15.
 */
public class Utils {
    public static int safeParseInt(String str) {
        int result = 0;
        try {
            result = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            result = 0;
        }
        return result;
    }

    public static float safeParseFloat(String str) {
        float result = 0.f;
        try {
            result = Float.parseFloat(str);
        } catch (NumberFormatException e) {
            result = 0.f;
        }
        return result;
    }

    public static int getAppVersionCode(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionCode;
    }

    public static String getAppVersionName(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionName;
    }

    public static float mapRange( float start, float end, float pos ) {
        if( start < end ) {
            return ((end - start) * pos) + start;
        } else if( start > end ) {
            return ((start - end) * (1.f-pos)) + end;
        }
        return start;
    }

    public static float reverseMapRange( float start, float end, float pos ) {
        float retVal = 0.f;
        if( start < end ) {
            retVal = (pos - start) / (end - start);
        } else if( start > end ) {
            retVal = (pos - end) / (start - end);
        }
        if( retVal < 0.f ) {
            retVal = 0.f;
        } else if( retVal > 1.f ) {
            retVal =  1.f;
        }
        return retVal;
    }

    public static PointF rotatePoint( PointF old, PointF center, float angle ) {
        PointF trans = new PointF(old.x - center.x, old.y - center.y);
        PointF ret = new PointF(
                (trans.x * (float)Math.cos(angle)) - (trans.y * (float)Math.sin(angle)),
                (trans.x * (float)Math.sin(angle)) + (trans.y * (float)Math.cos(angle))
        );
        ret.x += center.x;
        ret.y += center.y;
        return ret;
    }

    public static float logScale(float val) {
        return (float)Math.log10((val*0.9)+0.1)+1.f;
    }
}
