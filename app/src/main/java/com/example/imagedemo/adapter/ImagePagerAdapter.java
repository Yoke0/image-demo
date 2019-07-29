package com.example.imagedemo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.imagedemo.R;
import com.example.imagedemo.image.ImageItem;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {
    private Context context;
    private List<ImageItem> imageItems;
    private OnPhotoViewListener onPhotoViewListener;

    public ImagePagerAdapter(Context context, List<ImageItem> imageItems, OnPhotoViewListener onPhotoViewListener) {
        this.context = context;
        this.imageItems = imageItems;
        this.onPhotoViewListener  = onPhotoViewListener;
    }

    @Override
    public int getCount() {
        return imageItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.item_view_paper_image,null);
        PhotoView photoView = view.findViewById(R.id.item_view_paper_image_image);

        String path = imageItems.get(position).getPath();
        File file = new File(path);
        Glide.with(context).load(file).into(photoView);

        photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                onPhotoViewListener.onClick();
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public interface OnPhotoViewListener {
        void onClick();
    }
}
