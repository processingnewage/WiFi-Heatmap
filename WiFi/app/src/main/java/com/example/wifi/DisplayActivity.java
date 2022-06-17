package com.example.wifi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wifi.calculate.CalculateUtils;
import com.example.wifi.collect.CollectUtils;
import com.example.wifi.coordinate.ReturnCoordinates;
import com.example.wifi.database.ApData;
import com.example.wifi.database.MyDatabaseHelper;
import com.example.wifi.filter.FilterUtils;
import com.example.wifi.mqtt.MyMqtt;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DisplayActivity extends AppCompatActivity {
    private final String TAG = "DisplayActivity";         // 调试用
    private ImageView mIvDisplay;
    private MyMqtt mMyMqttClient;
    private final Timer timer = new Timer();    // 定时器：定时执行 MQTT Pub任务
    private ArrayList<ArrayList<Integer>> rssListOfLocationList;    // 数据库中的 Rss数值
    private final Handler uiHandler = new Handler();      // uiHandler在主线程中创建，所以自动绑定主线程

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // 创建 Mqtt client对象
        String serverUrl = "tcp://broker-cn.emqx.io:1883";
        String clientId = "MqttPubClient01";
        mMyMqttClient = new MyMqtt(serverUrl, clientId);
        mMyMqttClient.connect();

        // 启动定时器
        timer.schedule(timerTask, 1000, 3000);

        // 绑定控件并为 ImageView设置默认图片
        mIvDisplay = findViewById(R.id.iv_to_display);
        setImageByMinIndex(new ReturnCoordinates(0.0, 0.0, 0));

        // 实例化 SQLite数据库帮助对象
        MyDatabaseHelper mDbHelper = new MyDatabaseHelper(this, "LocationInfo.db", null, 1);
        SQLiteDatabase mLocationInfoDb = mDbHelper.getWritableDatabase();

        // 2. 调用函数从数据库中分别获取所有位置的 Rss数组
        ArrayList<Integer> rssListFromDataBase = CalculateUtils.getRssArrayFromDB(mLocationInfoDb);
        Log.d(TAG, rssListFromDataBase.toString());

        // 3. 将上述所有位置的 Rss数组拆分并重新组装为根据位置划分的二维数组
        rssListOfLocationList = CalculateUtils.oneDiDivideToTwoDi(rssListFromDataBase);
        Log.d(TAG, rssListOfLocationList.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        Log.d(TAG, "timer has canceled!");
        mMyMqttClient.disconnect();
        mMyMqttClient.close();
        Log.d(TAG, "mqtt client has closed!");
    }

    // 扫描和过滤函数：获取扫描过滤后的结果数组
    private ArrayList<ApData> scan() {
        return scanWifiDataInActivity();  // 滤波得到一组数据
    }

    // 扫描函数：解决调用的上下文问题
    private ArrayList<ApData> scanWifiDataInActivity() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
            }, 1);
        }
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return CollectUtils.scanWifiData(wifiManager);
    }

    // 根据传入的最小位置选择展示图片  0: 原始图片; 1, 2, 3, 4, ..... : 定位结果展示图片
    private void setImageByMinIndex(ReturnCoordinates minPoint) {
        int minDistanceIndex = minPoint.getId();
        Toast.makeText(DisplayActivity.this,
                "定位坐标: (" + minPoint.getX() + "," + minPoint.getY() + ")->" + minDistanceIndex,
                Toast.LENGTH_SHORT).show();
        if (0 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_0);
        } else if (1 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_1);
        } else if (2 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_2);
        } else if (3 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_3);
        } else if (4 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_4);
        } else if (5 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_5);
        } else if (6 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_6);
        } else if (7 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_7);
        } else if (8 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_8);
        } else if (9 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_9);
        } else if (10 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_10);
        } else if (11 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_11);
        } else if (12 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_12);
        } else if (13 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_13);
        } else if (14 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_14);
        } else if (15 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_15);
        } else if (16 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_16);
        } else if (17 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_17);
        } else if (18 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_18);
        } else if (19 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_19);
        } else if (20 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_20);
        } else if (21 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_21);
        } else if (22 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_22);
        } else if (23 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_23);
        } else if (24 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_24);
        } else if (25 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_25);
        } else if (26 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_26);
        } else if (27 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_27);
        } else if (28 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_28);
        } else if (29 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_29);
        } else if (30 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_30);
        } else if (31 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_31);
        } else if (32 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_32);
        } else if (33 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_33);
        } else if (34 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_34);
        } else if (35 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_35);
        } else if (36 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_36);
        } else if (37 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_37);
        } else if (38 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_38);
        } else if (39 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_39);
        } else if (40 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_40);
        } else if (41 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_41);
        } else if (42 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_42);
        } else if (43 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_43);
        } else if (44 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_44);
        } else if (45 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_45);
        } else if (46 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_46);
        } else if (47 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_47);
        } else if (48 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_48);
        } else if (49 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_49);
        } else if (50 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_50);
        } else if (51 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_51);
        } else if (52 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_52);
        } else if (53 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_53);
        } else if (54 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_54);
        } else if (55 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_55);
        } else if (56 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_56);
        } else if (57 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_57);
        } else if (58 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_58);
        } else if (59 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_59);
        } else if (60 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_60);
        } else if (61 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_61);
        } else if (62 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_62);
        } else if (63 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_63);
        } else if (64 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_64);
        } else if (65 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_65);
        } else if (66 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_66);
        } else if (67 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_67);
        } else if (68 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_68);
        } else if (69 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_69);
        } else if (70 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_70);
        } else if (71 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_71);
        } else if (72 == minDistanceIndex) {
            mIvDisplay.setImageResource(R.drawable.location_72);
        }
    }

    private final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            // 1. 扫描收集并对数据提取：得到 Rss数值数组
            ArrayList<Integer> rssListFromScan = CalculateUtils.getRssArrayFromScan(scan());
            Log.d(TAG, "收集到的数据: " + rssListFromScan);

            // 2. 3. 从数据库中获取 Rss数值数组（在界面创建时进行可以节约执行时间）

            // 只有当 4个 AP能收到 Rss数值时，才计算匹配结果并更新 UI和上传服务器
            if (rssListFromScan.get(0) != -100 || rssListFromScan.get(1) != -100
                    || rssListFromScan.get(2) != -100 || rssListFromScan.get(3) != -100) {
                // 4. 计算匹配结果
                ReturnCoordinates minPoint = CalculateUtils.calculateMatchResult(rssListFromScan, rssListOfLocationList);

                // 5. 更新 UI：在子线程中无法更新，使用 handler
                Runnable runnable = () -> DisplayActivity.this.setImageByMinIndex(minPoint);
                uiHandler.post(runnable);

                // 6. 将计算得到的定位点编号通过 MQTT协议发送到服务器
                String msg = "A1-" + minPoint.getId();
                Log.d(TAG, msg);
                mMyMqttClient.send("LocationUpload", 0, false, msg);
            }
        }
    };
}