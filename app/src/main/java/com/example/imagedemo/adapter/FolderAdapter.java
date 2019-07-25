package com.example.imagedemo.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imagedemo.R;
import com.example.imagedemo.image.ImageFolder;
import com.example.imagedemo.image.ImageItem;

import java.io.File;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ImageFolder> imageFolders;
    private int choose;
    private OnItemClickListener onItemClickListener;

    public FolderAdapter(@NonNull Context context, List<ImageFolder> imageFolders, int choose, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.imageFolders = imageFolders;
        this.choose = choose;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false);
        return new adapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ImageFolder imageFolder = imageFolders.get(position);
        List<ImageItem> imageItems = imageFolder.getImageItems();

        String name = imageFolder.getName();
        String path = imageItems.get(0).getPath();
        int size = imageFolder.getImageItems().size();

        ImageView imageView = ((adapterHolder) holder).imageView;
        File file = new File(path);
        RequestOptions options = new RequestOptions()
                .centerCrop();
        Glide.with(context).load(file).apply(options).into(imageView);

        ((adapterHolder) holder).textViewName.setText(name);

        if (position == 0) {
            ((adapterHolder) holder).textViewSize.setText("");
        }
        else {
            ((adapterHolder) holder).textViewSize.setText(size + "张");
        }

        if (choose == position) {
            ((adapterHolder) holder).imageViewChoose.setImageResource(R.mipmap.folder_choose);
        }
        else {
            ((adapterHolder) holder).imageViewChoose.setImageDrawable(null);
        }

        ((adapterHolder) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose = position;
                notifyDataSetChanged();
                onItemClickListener.onClick(choose);
            }
        });
    }

    public interface OnItemClickListener {
        void onClick(int choose);
    }

    @Override
    public int getItemCount() {
        return imageFolders.size();
    }

    private class adapterHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private ImageView imageView;
        private TextView textViewName;
        private TextView textViewSize;
        private ImageView imageViewChoose;

        public adapterHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.item_folder);
            imageView = itemView.findViewById(R.id.item_folder_image);

            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels;
            int imageWidth = width / 5;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageWidth, imageWidth);
            imageView.setLayoutParams(layoutParams);

            textViewName = itemView.findViewById(R.id.item_folder_name);
            textViewSize = itemView.findViewById(R.id.item_folder_size);
            imageViewChoose = itemView.findViewById(R.id.item_folder_choose);
        }
    }
}
