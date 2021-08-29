package com.example.myapplication.customviews;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;


public class CustomItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // only for the last one
        if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
            outRect.right = 50/* set your margin here */;
        }
    }
}