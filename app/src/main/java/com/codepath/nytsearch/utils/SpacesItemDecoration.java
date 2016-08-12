package com.codepath.nytsearch.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int mSpace;
    private final int mNumCols;

    public SpacesItemDecoration(int numCols, int space) {
        this.mSpace = space;
        this.mNumCols = numCols;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;
        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) < mNumCols)
            outRect.top = mSpace;
    }
}