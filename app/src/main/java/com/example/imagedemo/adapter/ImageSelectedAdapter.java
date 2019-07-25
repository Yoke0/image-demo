package com.example.imagedemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imagedemo.R;
import com.example.imagedemo.image.ImageItem;

import java.io.File;
import java.util.List;

public class ImageSelectedAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ImageItem> imageItems;
    private List<Integer> positions;
    private int position;
    private OnSelectClickListener onSelectClickListener;

    public ImageSelectedAdapter(Context context, List<ImageItem> imageItems, int position, List<Integer> positions, OnSelectClickListener onSelectClickListener) {
        this.context = context;
        this.imageItems = imageItems;
        this.position = position;
        this.positions = positions;
        this.onSelectClickListener = onSelectClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_selected, parent,false);
        return new AdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int i) {
        String path = imageItems.get(i).getPath();
        ImageView imageView = ((AdapterHolder) holder).imageView;
        File file = new File(path);
        RequestOptions options = new RequestOptions().centerCrop();
        Glide.with(context).load(file).apply(options).into(imageView);

        View view = ((AdapterHolder) holder).view;
        if (position == positions.get(i)) {
            view.setVisibility(View.VISIBLE);
        }
        else {
            view.setVisibility(View.GONE);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (positions.get(i) != -1) {
                    position = positions.get(i);
                }
                notifyDataSetChanged();
                onSelectClickListener.onClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    public class AdapterHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private View view;

        public AdapterHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image_select_image);
            view = itemView.findViewById(R.id.item_image_select_select);
        }
    }

    public interface OnSelectClickListener {
        void onClick(int choose);
    }
}
