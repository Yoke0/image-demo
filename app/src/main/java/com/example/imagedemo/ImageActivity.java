package com.example.imagedemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewConfigurationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagedemo.adapter.ImagePagerAdapter;
import com.example.imagedemo.adapter.ImageSelectedAdapter;
import com.example.imagedemo.image.ImageItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TextView textViewNo;
    private int position;
    private int mode;
    private List<ImageItem> imageItems;
    private List<ImageItem> chooseList;
    private boolean full = false;
    private List<Integer> choose = new ArrayList<>();
    private List<ImageItem> chooseImageItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ImageSelectedAdapter selectedAdapter;
    private int index; // selectView 显示下标

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        init();
    }

    public void init() {
        initStatusBar();

        initData();

        initViewPager();

        initSelectView();

        initSelect(true);
    }

    public void initStatusBar() {
        // 设置状态栏
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height","dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        LinearLayout heading = findViewById(R.id.image_heading);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) heading.getLayoutParams();
        layoutParams.setMargins(0,height,0,0);
    }

    public void initData() {
        textViewNo = findViewById(R.id.image_no);

        position = getIntent().getIntExtra("position", 0);
        mode = getIntent().getIntExtra("mode", 0);
        imageItems = (List<ImageItem>) getIntent().getSerializableExtra("imageItems");
        chooseList = (List<ImageItem>) getIntent().getSerializableExtra("chooseList");

        int i = 0;
        for (ImageItem imageItem: chooseList) {
            if (imageItems.contains(imageItem)) {
                choose.add(imageItems.indexOf(imageItem));
            }
            else {
                Log.d("have", "no:" + i);
                choose.add(-1);
            }

            i++;
        }

        index = 0;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initViewPager() {
        viewPager = findViewById(R.id.view_paper_image);

        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(this, imageItems, new ImagePagerAdapter.OnPhotoViewListener() {
            @Override
            public void onClick() {
                if (full) {
                    displayFull(false);
                }
                else {
                    displayFull(true);
                }
            }
        });
        viewPager.setAdapter(imagePagerAdapter);
        viewPager.setCurrentItem(position); //设置初始页面为点击的图片

        String text = (position + 1) + "/" + imageItems.size();
        textViewNo.setText(text); // 初始化图片序号

        // 页面滑动监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {
                position = i;
                initSelect(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void initSelectView() {
        recyclerView = findViewById(R.id.image_selected_view);
        layoutManager = new CenterLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        selectedAdapter = new ImageSelectedAdapter(this, mode, imageItems, chooseList, position, choose, new ImageSelectedAdapter.OnSelectClickListener() {
            @Override
            public void onClick(int choose) {
                position = choose;
                viewPager.setCurrentItem(position);
            }
        });
        recyclerView.setAdapter(selectedAdapter);

        displaySelectView();
    }

    public void displaySelectView() {
        if (choose.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    // 全屏显示图片
    public void displayFull(final boolean is) {
        if (is) { // 全屏
            full = true;
            displayLinearLayout(true);
        }
        else { // 退出全屏
            full = false;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // 延迟隐藏状态栏 延时显示heading和footer
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (is) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                else {
                    displayLinearLayout(false);
                }
            }
        }, 300);
    }

    // 显示或隐藏heading和footer
    public void displayLinearLayout(boolean is) {
        LinearLayout heading = findViewById(R.id.image_heading);
        LinearLayout footer = findViewById(R.id.image_footer);

        TranslateAnimation headingTranslate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, is ? 0.0f : -1.0f, Animation.RELATIVE_TO_SELF, is ? -1.0f : 0.0f);
        headingTranslate.setDuration(200);

        if (is) { // 全屏
            heading.setVisibility(View.GONE);
            footer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
        else { // 退出全屏
            heading.setVisibility(View.VISIBLE);
            footer.setVisibility(View.VISIBLE);
            displaySelectView();
        }

        heading.setAnimation(headingTranslate);
    }

    public void onSelectImage(View view) {
        initSelect(false);
    }

    public void initSelect(boolean is) { // is true表示初始化 false表示选择或取消选择
        String text = (position + 1) + "/" + imageItems.size();
        textViewNo.setText(text);

        ImageView imageView = findViewById(R.id.image_select);

        boolean have = choose.contains(position);
        int size = chooseList.size();

        if (is) {
            displaySelect(have);
        }
        else {
            if (size < 9 && !have) { // 当图片数小于9和当前图片未被选择
                imageView.setImageResource(R.mipmap.box_selected);
                choose.add(position);
                chooseList.add(imageItems.get(position));
                chooseImageItems.add(imageItems.get(position));
            }

            if (have) { // 当前图片已被选择,再次点击取消选择
                imageView.setImageResource(R.mipmap.box_no_select);
                int index = choose.indexOf(position);
                choose.remove(index);
                chooseList.remove(imageItems.get(position));
            }

            if (size == 9 && !have) { // 图片数量已经9张
                Toast.makeText(this, "你最多只能选择9张图片", Toast.LENGTH_LONG).show();
            }
        }

        initFinish();
        initSelectView();
        if (choose.indexOf(position) != -1) {
            index = choose.indexOf(position);
        }
        layoutManager.scrollToPosition(index - 1);
        if (full) {
            recyclerView.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    public void initFinish() {
        TextView button = findViewById(R.id.image_finish);

        int size = chooseList.size();
        if (size > 0) {
            button.setText("完成(" + size + "/9)");
        }
        else {
            button.setText("完成");
        }
    }

    public void displaySelect(boolean is) {
        ImageView imageView = findViewById(R.id.image_select);

        if (is) {
            imageView.setImageResource(R.mipmap.box_selected);
        }
        else {
            imageView.setImageResource(R.mipmap.box_no_select);
        }
    }

    public void onBack(View view) {
        back();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            back();
        }

        return true;
    }

    public void back() {
        Intent intent = getIntent();
        intent.putExtra("chooseList", (Serializable) chooseList);
        setResult(RESULT_OK, intent);
        finish();
    }
}
