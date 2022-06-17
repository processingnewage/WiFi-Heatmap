package com.example.wifi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wifi.adapter.ListViewAdapter;
import com.example.wifi.collect.CollectUtils;
import com.example.wifi.database.ApData;
import com.example.wifi.database.MyDatabaseHelper;
import com.example.wifi.filter.FilterUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CollectActivity extends AppCompatActivity {
    private final String TAG = "CollectActivity";            // 调试用
    private ArrayList<ApData> mDataDisplay;                  // 展示收集结果
    private final ArrayList<ArrayList<ApData>> mRssList = new ArrayList<>();   // 收集的多组数据
    private RecyclerView mRvScanResult;                     // RV
    private SQLiteDatabase mLocationInfoDb;                 // SQLite数据库对象
    private Timer timer;                                    // 定时器：执行数据收集任务

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_cc);

        // 绑定 EditText控件
        EditText mEtInput = findViewById(R.id.et_input_location);

        // 绑定 RecycleView控件并做相关设置
        mRvScanResult = findViewById(R.id.rv_scan_result);
        //添加 Android自带的分割线
        mRvScanResult.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        // 设置样式，也即是设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvScanResult.setLayoutManager(linearLayoutManager);

        // 实例化数据库帮助对象
        MyDatabaseHelper mDbHelper = new MyDatabaseHelper(this, "LocationInfo.db", null, 1);
        // 通过数据库帮助对象实例化数据库对象：存在则打开，不存在则创建
        mLocationInfoDb = mDbHelper.getWritableDatabase();

        // 设置按钮点击事件，展示扫描结果
        Button mBtnDisplay = findViewById(R.id.btn_to_scan);
        mBtnDisplay.setOnClickListener(v -> {
            // 准备数据
            initData();
            // 创建适配器
            ListViewAdapter adapter = new ListViewAdapter(mDataDisplay);
            // 设置 adapter到 RecycleView
            mRvScanResult.setAdapter(adapter);
        });

        // 设置按钮点击事件，保存数据库
        Button mBtnSave = findViewById(R.id.btn_to_save);
        mBtnSave.setOnClickListener(v -> {
            if(mRssList.size() != 0) {
                Log.d(TAG, String.valueOf(mRssList.size()));
                for (int i = 0; i < mRssList.size(); i++) {
                    for (int j = 0; j < mRssList.get(0).size(); j++) {
                        Log.d(TAG, String.valueOf(mRssList.get(i).get(j).getRss()));
                    }
                }
                ArrayList<ApData> mData = scanAndFilter(mRssList);  // 滤波得到数据
                Log.d(TAG, "-------");
                for (int i = 0; i < mData.size(); i++) {
                    Log.d(TAG + i, String.valueOf(mData.get(i).getRss()));
                }
                mRssList.clear();   // 清空收集结果数组
                Log.d(TAG, String.valueOf(mRssList.size()));

                String inputLocation = mEtInput.getText().toString(); // 获取输入的位置
                mLocationInfoDb.beginTransaction();       // 开始事务（不可分割的操作集合）
                if (!inputLocation.equals("") && Integer.parseInt(inputLocation) > 0 && Integer.parseInt(inputLocation) < 73) {
                    try {
                        ContentValues contentValues = new ContentValues();
                        // 获取位置
                        contentValues.put("location", inputLocation);
                        // 写入 AP节点 01的信息
                        contentValues.put("ap_id_01", mData.get(0).getApId());
                        contentValues.put("ap_mac_01", mData.get(0).getMacAddress());
                        contentValues.put("ap_rss_01", mData.get(0).getRss());
                        // 写入 AP节点 02的信息
                        contentValues.put("ap_id_02", mData.get(1).getApId());
                        contentValues.put("ap_mac_02", mData.get(1).getMacAddress());
                        contentValues.put("ap_rss_02", mData.get(1).getRss());
                        // 写入 AP节点 03的信息
                        contentValues.put("ap_id_03", mData.get(2).getApId());
                        contentValues.put("ap_mac_03", mData.get(2).getMacAddress());
                        contentValues.put("ap_rss_03", mData.get(2).getRss());
                        // 写入 AP节点 04的信息
                        contentValues.put("ap_id_04", mData.get(3).getApId());
                        contentValues.put("ap_mac_04", mData.get(3).getMacAddress());
                        contentValues.put("ap_rss_04", mData.get(3).getRss());
                        // 将以上数据插入对应元组（由于主键索引：存在则更新，不存在则新建）
                        mLocationInfoDb.replace("location_info", null, contentValues);
                        mLocationInfoDb.setTransactionSuccessful();       // 事务已经执行成功
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mLocationInfoDb.endTransaction();     // 结束事务
                        Toast.makeText(CollectActivity.this, "保存完成！！！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CollectActivity.this, "请正确输入数字！！！", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CollectActivity.this, "未收集数据！！！", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置按钮点击事件，收集准备存入数据库的数据
        Button mBtnCollect = findViewById(R.id.btn_to_collect);
        mBtnCollect.setOnClickListener(v -> {
            if (mRssList.size() == 0) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (mRssList.size() < 10) {
                            Log.d(TAG, "timer++!");
                            mRssList.add(scanWifiDataInActivity());
                        } else {
                            timer.cancel();
                            timer.purge();
                            if (Looper.myLooper() == null) {
                                Looper.prepare();
                            }
                            Toast.makeText(CollectActivity.this, "数据收集完成！！！", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }, 2000, 1000);
            } else {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                Toast.makeText(CollectActivity.this, "当前数组不为空！！！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });

        // 设置按钮点击事件，清空数据库
        Button mBtnClean = findViewById(R.id.btn_to_clean);
        mBtnClean.setOnClickListener(v -> {
            // 判断当前数据库是否为空（若不为空则清空数据库）
            Cursor c = mLocationInfoDb.rawQuery("select * from location_info", null);
            int rawCounts = c.getCount();   // 获取表中行数
            c.close();              // 关闭当前cursor

            if (rawCounts != 0) {
                mLocationInfoDb.execSQL("delete from location_info");
                Toast.makeText(CollectActivity.this, "数据库已清空！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CollectActivity.this, "数据库已为空！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 准备要展示的数据
    private void initData() {
        // 1. 模拟数据展示
        // mData = new ArrayList<>();
        // for(int i = 0; i < 4; i++) {
        //     ApData data = new ApData(i + 1, "D10D835390A2", -50);
        //     mData.add(data);
        // }
        // 调用扫描函数收集数据
        mDataDisplay = scanWifiDataInActivity();
    }

    // 扫描和过滤函数：获取扫描过滤后的结果数组
    private ArrayList<ApData> scanAndFilter(ArrayList<ArrayList<ApData>> rssList) {
        return FilterUtils.filterWifiData(FilterUtils.dedupWifiData(rssList));  // 滤波得到一组数据
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
}