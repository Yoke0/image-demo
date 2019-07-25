package com.example.imagedemo;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space; // 边距
    private int spanCount; // 每行显示数量

    public SpaceItemDecoration(int space, int spanCount) {
        this.space = space / spanCount;
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space * 4; // outRect recycleView每一个item的padding

        int position = parent.getChildLayoutPosition(view) % spanCount;

        for (int i = 0; i < spanCount; i++) {
            if (position == i) {
                outRect.left = space * i;
                outRect.right = space * (spanCount - i - 1);
            }
        }
    }
}
