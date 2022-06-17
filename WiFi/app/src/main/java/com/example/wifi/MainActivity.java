package com.example.wifi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 点击按钮跳转展示界面
        Button mBtnDisplay = findViewById(R.id.btn_to_enter);
        mBtnDisplay.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
            startActivity(intent);
        });

        // 点击按钮跳转收集界面
        Button mBtnCollect = findViewById(R.id.btn_to_collect);
        mBtnCollect.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CollectActivity.class);
            startActivity(intent);
        });
    }
}