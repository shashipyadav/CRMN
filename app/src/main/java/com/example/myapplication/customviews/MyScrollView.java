package com.example.myapplication.customviews;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

    public MyScrollView(Context context) {    super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {    super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {    super(context, attrs, defStyle);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        return true;
    }

}
