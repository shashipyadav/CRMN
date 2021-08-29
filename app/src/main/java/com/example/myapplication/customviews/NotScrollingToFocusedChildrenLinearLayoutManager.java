package com.example.myapplication.customviews;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotScrollingToFocusedChildrenLinearLayoutManager extends LinearLayoutManager {

    public NotScrollingToFocusedChildrenLinearLayoutManager(Context context) {
        super(context);
    }

    public NotScrollingToFocusedChildrenLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public NotScrollingToFocusedChildrenLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

//    @Override
//    public boolean onRequestChildFocus(RecyclerView parent, RecyclerView.State state, View child, View focused) {
//        //return super.onRequestChildFocus(parent, state, child, focused);
//        return true;
//    }

//    @Override
//    public boolean onRequestChildFocus(RecyclerView parent, View child, View focused) {
//        //return super.onRequestChildFocus(parent, child, focused);
//        return true;
//    }

    @Override
    public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull  View child, @NonNull  Rect rect, boolean immediate, boolean focusedChildVisible) {
      //  return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
    return  false;
    }
}
