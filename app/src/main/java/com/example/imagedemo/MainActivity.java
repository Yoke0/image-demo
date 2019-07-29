package com.example.imagedemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagedemo.adapter.FolderAdapter;
import com.example.imagedemo.adapter.ImageAdapter;
import com.example.imagedemo.image.ImageFolder;
import com.example.imagedemo.image.ImageItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<ImageFolder> imageFolders = new ArrayList<>();
    private List<ImageItem> imageAllItems = new ArrayList<>();
    private List<ImageItem> imageItems = new ArrayList<>();
    private List<ImageItem> chooseList = new ArrayList<>();
    private RecyclerView imageRecyclerView;
    private ImageAdapter imageAdapter;
    private RecyclerView folderRecyclerView;
    private FolderAdapter folderAdapter;
    private boolean onFolder = false; // 相册选择界面是否显示
    private int choose = 0; // 选择的相册 0为所有图片
    private TextView textViewFolder;
    private View drakeView; // 蒙版
    private long exitTime = 0;
    String[] permissions = new String[]{ // 需要的权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            chooseList = (List<ImageItem>) data.getSerializableExtra("chooseList");
            imageAdapter.notifyView(chooseList);
            initFinish();
        }
    }

    public void init() {
        initPermission();

        initStatusBar();
        drakeView = findViewById(R.id.image_all_drake);

        getAllImage();
        // 将所有图片添加到文件夹中
        ImageFolder imageFolder = new ImageFolder("所有图片", imageAllItems);
        imageFolders.add(0, imageFolder);

        textViewFolder = findViewById(R.id.textView_folder);
        textViewFolder.setText(imageFolders.get(choose).getName());

        initFolderRecycleView();
        initImageRecyclerView();

        initFinish();
    }

    public void initPermission() {
        List<String> mPermissionList = new ArrayList<>(); //未授权的权限列表

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }

        if (!mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
    }

    public void initStatusBar() {
        // 设置状态栏
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public void initImageRecyclerView() {
        imageRecyclerView = findViewById(R.id.image_all);
        // 每行显示数
        int spanCount = 4;
        GridLayoutManager manager = new GridLayoutManager(this, spanCount);
        // 设置布局管理器
        imageRecyclerView.setLayoutManager(manager);
        // 设置adapter
        imageItems = imageFolders.get(choose).getImageItems();
        // 间距
        int px = 1;
        int space = spanCount * px; // spanCount
        imageAdapter = new ImageAdapter(this, imageItems, chooseList, new ImageAdapter.OnSelectImageListener() {
            @Override
            public void onSelect(List<ImageItem> choose) {
                chooseList = choose;
                initFinish();
            }

            @Override
            public void onStart(int position, List<ImageItem> imageItems, List<ImageItem> chooseList) {
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("mode", 0);
                intent.putExtra("imageItems", (Serializable) imageItems);
                intent.putExtra("chooseList", (Serializable) chooseList);
                startActivityForResult(intent, 1);
            }
        });
        if (imageRecyclerView.getItemDecorationCount() == 0) { // 判断是否以设置边距
            imageRecyclerView.addItemDecoration(new SpaceItemDecoration(space, spanCount));
        }
        imageRecyclerView.setAdapter(imageAdapter);
    }

    public void initFolderRecycleView() {
        folderRecyclerView = findViewById(R.id.folder_all);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        // 设置布局管理器
        folderRecyclerView.setLayoutManager(manager);
        // 设置adapter
        folderAdapter = new FolderAdapter(this, imageFolders, choose, new FolderAdapter.OnItemClickListener() {
            @Override
            public void onClick(int c) {
                choose = c;
                displayFolder(false);
                textViewFolder.setText(imageFolders.get(choose).getName());
                initImageRecyclerView();
            }
        });
        folderRecyclerView.setAdapter(folderAdapter);
        folderRecyclerView.getLayoutManager().scrollToPosition(choose);
    }

    // 返回
    public void onBack(View view) {
        if (onFolder) {
            displayFolder(false);
        }
        else {
            finish();
        }
    }

    public void onSelectFolder(View view) {
        displayFolder(!onFolder);
    }

    public void onCloseFolder(View view) {
        displayFolder(false);
    }

    public void displayFolder(boolean is) {
        LinearLayout linearLayout = findViewById(R.id.footer);
        linearLayout.bringToFront();

        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, is ? 1.0f : 0.0f, Animation.RELATIVE_TO_SELF, is ? 0.0f : 1.0f);
        translateAnimation.setDuration(300);

        if (is) {
            onFolder = true;
            drakeView.setVisibility(View.VISIBLE);
            folderRecyclerView.setVisibility(View.VISIBLE);
        }
        else {
            onFolder = false;
            drakeView.setVisibility(View.GONE);
            folderRecyclerView.setVisibility(View.INVISIBLE);
        }

        folderRecyclerView.startAnimation(translateAnimation);
    }

    private boolean getAllImage() {
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.DATE_ADDED
        };
        Cursor cursor = contentResolver.query(imageUri, projection, null, null,
                MediaStore.Images.Media.DATE_ADDED + " desc");

        if (cursor == null) {
            return false;
        }

        if (cursor.getCount() != 0) {
            int count = 0;
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
//                String fileName =
//                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
//                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));

                getFolder(count, path, date);

                ImageItem imageItem = new ImageItem(count, path, date);
                imageAllItems.add(imageItem);

                count++;
            }
        }

        cursor.close();

        return true;
    }

    // 将图片以文件夹名分类
    public void getFolder(int count, String imagePath, String date) {
        ImageItem imageItem = new ImageItem(count, imagePath, date);

        String folderPath = imagePath.substring(0, imagePath.lastIndexOf("/") + 1);

        int end_index = imagePath.lastIndexOf("/");
        int start_index = imagePath.lastIndexOf("/", end_index - 1);
        String folderName = folderPath.substring(start_index + 1, end_index);

        int have = 0;
        for (ImageFolder imageFolder: imageFolders) {
            if (imageFolder.getName().equals(folderName)) {
                have = 1;
                List<ImageItem> imageItems = imageFolder.getImageItems();
                imageItems.add(imageItem);
                imageFolder.setImageItems(imageItems);
                break;
            }
        }
        if (have == 0) {
            List<ImageItem> imageItems = new ArrayList<>();
            imageItems.add(imageItem);
            ImageFolder imageFolder = new ImageFolder(folderName, imageItems);
            imageFolders.add(imageFolder);
        }
    }

    public void initFinish() {
        TextView finish = findViewById(R.id.main_finish);
        TextView preview = findViewById(R.id.main_button_preview);

        int size = chooseList.size();
        if (size > 0) {
            finish.setText("完成(" + size + "/9)");
            preview.setText("预览(" + size + ")");
        }
        else {
            finish.setText("完成");
            preview.setText("预览");
        }
    }

    public void onPreview(View view) {
        if (chooseList.size() > 0) {
            Intent intent = new Intent(this, ImageActivity.class);
            intent.putExtra("position", 0);
            intent.putExtra("mode", 1);
            intent.putExtra("imageItems", (Serializable) chooseList);
            intent.putExtra("chooseList", (Serializable) chooseList);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else {
                finish();
            }
        }

        return true;
    }
}