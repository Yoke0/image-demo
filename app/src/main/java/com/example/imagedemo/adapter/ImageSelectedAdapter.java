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
    private int mode; // 0 表示点击图片后进图片所在文件夹预览 1 表示预览以选择的图片
    private List<ImageItem> imageItems;
    private List<ImageItem> chooseList;
    private List<Integer> positions;
    private int position;
    private OnSelectClickListener onSelectClickListener;


    public ImageSelectedAdapter(Context context, int mode, List<ImageItem> imageItems, List<ImageItem> chooseList, int position, List<Integer> positions, OnSelectClickListener onSelectClickListener) {
        this.context = context;
        this.mode = mode;
        this.imageItems = imageItems;
        this.chooseList = chooseList;
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
        ImageView imageView = ((AdapterHolder) holder).imageView;
        View boxView = ((AdapterHolder) holder).boxView; // 表示选择的绿色选择框视图
        View selectView = ((AdapterHolder) holder).selectView; // 表示取消选择的白色蒙版

        ImageItem imageItem = imageItems.get(i);
        if (mode == 0) {
            imageItem = chooseList.get(i);
        }
        String path  = imageItem.getPath();
        File file = new File(path);
        RequestOptions options = new RequestOptions().centerCrop();
        Glide.with(context).load(file).apply(options).into(imageView);

        if (mode == 0) {
            if (position == positions.get(i)) {
                boxView.setVisibility(View.VISIBLE);
            } else {
                boxView.setVisibility(View.GONE);
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
        else {
            if (position == i) {
                boxView.setVisibility(View.VISIBLE);
            }
            else {
                boxView.setVisibility(View.GONE);
            }

            if (!chooseList.contains(imageItems.get(i))) {
                selectView.setVisibility(View.VISIBLE);
            }
            else {
                selectView.setVisibility(View.GONE);
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = i;
                    notifyDataSetChanged();
                    onSelectClickListener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mode == 0) {
            return chooseList.size();
        }
        else {
            return imageItems.size();
        }
    }

    public class AdapterHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private View boxView;
        private View selectView;

        public AdapterHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image_select_image);
            boxView = itemView.findViewById(R.id.item_image_select_select);
            selectView = itemView.findViewById(R.id.item_image_select_cancel_select);
        }
    }

    public interface OnSelectClickListener {
        void onClick(int choose);
    }
}
