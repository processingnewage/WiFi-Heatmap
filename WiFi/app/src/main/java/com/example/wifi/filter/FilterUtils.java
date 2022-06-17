package com.example.wifi.filter;

import android.util.Log;

import com.example.wifi.database.ApData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

// 对收集到的数据做滤波
public class FilterUtils {
    private final static String TAG = "FilterUtils";   // 调试

    public static ArrayList<ApData> filterWifiData(ArrayList<ArrayList<ApData>> rssList) {
        ArrayList<ApData> rssListByFilter = new ArrayList<>();  // 扫描结果保存
        // 定义 4个数组分别存储 4个 AP的 Rss数值
        ArrayList<Integer> rssArray01 = new ArrayList<>();
        ArrayList<Integer> rssArray02 = new ArrayList<>();
        ArrayList<Integer> rssArray03 = new ArrayList<>();
        ArrayList<Integer> rssArray04 = new ArrayList<>();
        // 遍历扫描得到的 x组数据填充上述数组
        for (int i = 0; i < rssList.size(); i++) {
            ArrayList<ApData> rssTemp = rssList.get(i);
            rssArray01.add(rssTemp.get(0).getRss());
            rssArray02.add(rssTemp.get(1).getRss());
            rssArray03.add(rssTemp.get(2).getRss());
            rssArray04.add(rssTemp.get(3).getRss());
        }
        // 做高斯和均值滤波
        int filterResult01 = filterFunction(rssArray01);
        int filterResult02 = filterFunction(rssArray02);
        int filterResult03 = filterFunction(rssArray03);
        int filterResult04 = filterFunction(rssArray04);
        // 定义新的 4个 ApData对象，重新组装
        ApData apData01 = new ApData(rssList.get(0).get(0).getApId(), rssList.get(0).get(0).getMacAddress(), filterResult01);
        ApData apData02 = new ApData(rssList.get(0).get(1).getApId(), rssList.get(0).get(1).getMacAddress(), filterResult02);
        ApData apData03 = new ApData(rssList.get(0).get(2).getApId(), rssList.get(0).get(2).getMacAddress(), filterResult03);
        ApData apData04 = new ApData(rssList.get(0).get(3).getApId(), rssList.get(0).get(3).getMacAddress(), filterResult04);
        // 组装数据
        rssListByFilter.add(apData01);
        rssListByFilter.add(apData02);
        rssListByFilter.add(apData03);
        rssListByFilter.add(apData04);
        return rssListByFilter;
    }

    public static ArrayList<ArrayList<ApData>> dedupWifiData(ArrayList<ArrayList<ApData>> rssList) {
        ArrayList<ArrayList<ApData>> rssResult = new ArrayList<>();     // 返回的结果
        // 循环取出 rss值
        ArrayList<ArrayList<Integer>> onlyRssList = new ArrayList<>();
        for (ArrayList<ApData> info : rssList) {
            ArrayList<Integer> rssTemp = new ArrayList<>();
            for (int i = 0; i < info.size(); i++) {
                rssTemp.add(info.get(i).getRss());
            }
            onlyRssList.add(rssTemp);
        }
        // 去重
        onlyRssList = new ArrayList<>(new HashSet<>(onlyRssList));
        // 重新组装数据
        for (int i = 0; i < onlyRssList.size(); i++) {
            ArrayList<ApData> apTemp = new ArrayList<>();
            ApData apData01 = new ApData(1, "50FA84CDBE57", onlyRssList.get(i).get(0));
            ApData apData02 = new ApData(2, "50FA84CBE203", onlyRssList.get(i).get(1));
            ApData apData03 = new ApData(3, "50FA84CDE06D", onlyRssList.get(i).get(2));
            ApData apData04 = new ApData(4, "50FA84CDBF97", onlyRssList.get(i).get(3));
            apTemp.add(apData01);
            apTemp.add(apData02);
            apTemp.add(apData03);
            apTemp.add(apData04);
            rssResult.add(apTemp);
        }
        return rssResult;
    }

    // 高斯和均值滤波：处理传入的 Rss数组，最后得到均值
    private static int filterFunction(ArrayList<Integer> rssArray) {
        // 从大到小排序
        Collections.sort(rssArray);
        for(int i = 0; i < rssArray.size(); i++) {
            Log.d(TAG, String.valueOf(rssArray.get(i)));
        }
        // 均值滤波
        if (rssArray.size() == 1) {
            return rssArray.get(0);
        } else if (rssArray.size() == 2) {
            return (rssArray.get(0) + rssArray.get(1)) / 2;
        } else {
            return (rssArray.get(rssArray.size() / 2 - 1) +
                    rssArray.get(rssArray.size() / 2) +
                    rssArray.get(rssArray.size() / 2 + 1)) / 3;
        }
    }
}
