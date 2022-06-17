package com.example.wifi.collect;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.wifi.database.ApData;

import java.util.ArrayList;
import java.util.List;

// 对周边 AP的扫描和数据的收集
public class CollectUtils {
    // WiFi 扫描函数（返回的数组每个编号的 ApData对象应该只有一份，且按照顺序排列）
    public static ArrayList<ApData> scanWifiData(WifiManager wifiManager) {
        ArrayList<ApData> rssList = new ArrayList<>();  // 扫描结果保存
        int[] rssArray = new int[]{-100, -100, -100, -100};   // rss数值数组（需要初始化）
        String[] macArray = new String[]{"50FA84CDBE57", "50FA84CBE203",
        "50FA84CDE06D", "50FA84CDBF97"};        // mac数值数组（需要初始化）

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();                        // 开始扫描
        List<ScanResult> resultsList = wifiManager.getScanResults();     // 获取扫描结果
        Log.d("CollectUtils", resultsList.toString());      // 打印扫描结果

        // 当扫描结果不为空时，更新 Rss数值
        if(resultsList.size() != 0) {
            for (int i = 0; i < resultsList.toArray().length; i++) {
                ScanResult scanResult = resultsList.get(i);
                // 遍历扫描到的 resultsList数组得到 Rss数组
                switch (scanResult.SSID) {
                    case "wifi_01": {
                        rssArray[0] = scanResult.level;
                        break;
                    }
                    case "wifi_02": {
                        rssArray[1] = scanResult.level;
                        break;
                    }
                    case "wifi_03": {
                        rssArray[2] = scanResult.level;
                        break;
                    }
                    case "wifi_04": {
                        rssArray[3] = scanResult.level;
                        break;
                    }
                }
            }
        }

        // 组装数据：初始化的好处是有默认值
        ApData apData01 = new ApData(1, macArray[0], rssArray[0]);
        ApData apData02 = new ApData(2, macArray[1], rssArray[1]);
        ApData apData03 = new ApData(3, macArray[2], rssArray[2]);
        ApData apData04 = new ApData(4, macArray[3], rssArray[3]);
        rssList.add(apData01);
        rssList.add(apData02);
        rssList.add(apData03);
        rssList.add(apData04);
        return rssList;
    }
}
