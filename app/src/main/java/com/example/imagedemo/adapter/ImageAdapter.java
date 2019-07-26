package com.example.imagedemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imagedemo.ImageActivity;
import com.example.imagedemo.MainActivity;
import com.example.imagedemo.R;
import com.example.imagedemo.image.ImageItem;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ImageItem> imageItems;
    private List<ImageItem> chooseList;
    private OnSelectImageListener onSelectImageListener;

    public ImageAdapter(@NonNull Context context, List<ImageItem> imageItems, List<ImageItem> chooseList, OnSelectImageListener onSelectImageListener) {
        this.context = context;
        this.imageItems = imageItems;
        this.chooseList = chooseList;
        this.onSelectImageListener = onSelectImageListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new adapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ImageItem imageItem = imageItems.get(position);
        String path = imageItem.getPath();

        ImageView imageView = ((adapterHolder) holder).imageView;

        File file = new File(path);
        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.image_loading).centerCrop();
        Glide.with(context).load(file).apply(options).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectImageListener.onStart(position, imageItems, chooseList);
            }
        });

        final ImageView select = ((adapterHolder) holder).select;
        final View view = ((adapterHolder) holder).view;

        boolean have = chooseList.contains(imageItem);
        if (!have) { // 初始化图标
            select.setImageResource(R.mipmap.box_no_select_white);
            view.setBackgroundColor(Color.parseColor("#10000000"));
        }
		else{
			select.setImageResource(R.mipmap.box_selected);
            view.setBackgroundColor(Color.parseColor("#80000000"));
		}

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            boolean have = chooseList.contains(imageItem);
            int size = chooseList.size();

            if (size < 9 && !have) {
                chooseList.add(imageItem);
                select.setImageResource(R.mipmap.box_selected);
                view.setBackgroundColor(Color.parseColor("#80000000"));
                onSelectImageListener.onSelect(chooseList);
            }

             if (have){
                 chooseList.remove(chooseList.indexOf(imageItem));
                 select.setImageResource(R.mipmap.box_no_select_white);
                 view.setBackgroundColor(Color.parseColor("#10000000"));
                 onSelectImageListener.onSelect(chooseList);
            }

            if (size == 9 && !have) { // 图片数量已经9张
                 Toast.makeText(context, "你最多只能选择9张图片", Toast.LENGTH_LONG).show();
            }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    public void notifyView(List<ImageItem> chooseList) {
        this.chooseList = chooseList;
        notifyDataSetChanged();
    }

    private class adapterHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private View view;
        private ImageView select;

        public adapterHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image_image);
            view = itemView.findViewById(R.id.item_image_view);
            select = itemView.findViewById(R.id.item_image_select);
        }
    }

    public interface OnSelectImageListener {
        void onSelect(List<ImageItem> chooseList);

        void onStart(int position, List<ImageItem> imageItems, List<ImageItem> chooseList);
    }
}

