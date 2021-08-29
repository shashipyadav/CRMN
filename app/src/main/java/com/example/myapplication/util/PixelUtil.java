package com.example.myapplication.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class PixelUtil {

    public static int convertDpToPixel(Context context, float dip) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        int mpx = (int) px;
        return mpx;
    }
}
