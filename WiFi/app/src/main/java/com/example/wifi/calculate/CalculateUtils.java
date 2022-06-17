package com.example.wifi.calculate;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.wifi.coordinate.CoordinateBean;
import com.example.wifi.coordinate.CoordinateRecord;
import com.example.wifi.coordinate.ReturnCoordinates;
import com.example.wifi.database.ApData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class CalculateUtils {
    private final static String TAG = "CalculateUtils";

    // 将所有位置的 Rss数组拆分并重新组装为根据位置划分的二维数组
    public static ArrayList<ArrayList<Integer>> oneDiDivideToTwoDi(ArrayList<Integer> rssListFromDataBase) {
        ArrayList<ArrayList<Integer>> rssListOfLocationList = new ArrayList<>();
        // 两层循环给二维数组赋值
        for (int i = 0; i < 72; i++) {
            ArrayList<Integer> rssTemp = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                rssTemp.add(rssListFromDataBase.get(i * 4 + j));
            }
            rssListOfLocationList.add(rssTemp);
        }
        return rssListOfLocationList;
    }

    // 获得 Rss数组函数：从数据库中获取所有的 Rss数值
    @SuppressLint("Range")
    public static ArrayList<Integer> getRssArrayFromDB(SQLiteDatabase locationInfoDb) {
        ArrayList<Integer> rssList = new ArrayList<>(); // 存储查询结果的（每个位置的 Rss数值数组）
        Cursor cursor = locationInfoDb.query("location_info", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            rssList.add(cursor.getInt(cursor.getColumnIndex("ap_rss_01")));
            rssList.add(cursor.getInt(cursor.getColumnIndex("ap_rss_02")));
            rssList.add(cursor.getInt(cursor.getColumnIndex("ap_rss_03")));
            rssList.add(cursor.getInt(cursor.getColumnIndex("ap_rss_04")));
        }
        cursor.close();
        return rssList;
    }

    // 获得 Rss数组函数：从扫描和滤波后的结果数组中获取 Rss数组
    public static ArrayList<Integer> getRssArrayFromScan(ArrayList<ApData> rssList) {
        ArrayList<Integer> rssArray = new ArrayList<>();
        for (int i = 0; i < rssList.size(); i++) {
            rssArray.add(rssList.get(i).getRss());
        }
        return rssArray;
    }

    // NN算法（欧式距离的计算）：计算两个列表之间的欧氏距离，返回保留两位小数的距离
    public static Double calculateEuclideanDistance(ArrayList<Integer> rssListFromLocation, ArrayList<Integer> rssListFromDB) {
        double euclideanDistanceTemp;           // 累加的中间值
        double euclideanDistanceSum = 0.0;      // 每个坐标差的平方和
        double euclideanDistance;               // 欧氏距离
        for (int i = 0; i < 4; i++) {
            euclideanDistanceTemp = Math.pow(rssListFromLocation.get(i) - rssListFromDB.get(i), 2);
            euclideanDistanceSum += euclideanDistanceTemp;
        }
        euclideanDistance = Math.sqrt(euclideanDistanceSum);
        // Log.d(TAG, String.valueOf(euclideanDistance));
        // 保留两位小数
        BigDecimal formatHelper = new BigDecimal(euclideanDistance);
        return formatHelper.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // 计算匹配结果得到最终的定位点编号
    public static ReturnCoordinates calculateMatchResult(ArrayList<Integer> rssListFromScan,
                                                         ArrayList<ArrayList<Integer>> rssListOfLocationList) {
        // 1. 计算最小距离
        ArrayList<Double> distanceList = new ArrayList<>();
        for (int i = 0; i < 72; i++) {
            Double distance = CalculateUtils.calculateEuclideanDistance(rssListOfLocationList.get(i), rssListFromScan);
            distanceList.add(distance);
        }
        Log.d(TAG, distanceList.toString());

        // 2. 找到 distanceList数组中最小的 3个值及其编号（index + 1）
        HashMap<Integer, Double> minDistanceMap = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            Double minElement = Collections.min(distanceList);
            int indexOfMinElement = distanceList.indexOf(Collections.min(distanceList));
            minDistanceMap.put(indexOfMinElement + 1, minElement);
            distanceList.set(indexOfMinElement, 1000.0);    // 将 distanceList中已找到的最小值设为一个相对无穷大
        }
        Log.d(TAG, minDistanceMap.toString());

        // 3. 取到最小的 3个点及其定位坐标和 NN距离
        ArrayList<CoordinateBean> minCoordinateList = new ArrayList<>();
        for (Integer key : minDistanceMap.keySet()) {
            minCoordinateList.add(CoordinateRecord.coordinateRecord.get(key - 1));
        }
        Log.d(TAG, String.valueOf(minCoordinateList.get(0).getCoordinateId()));
        Log.d(TAG, String.valueOf(minCoordinateList.get(1).getCoordinateId()));
        Log.d(TAG, String.valueOf(minCoordinateList.get(2).getCoordinateId()));

        ArrayList<Double> minValues = new ArrayList<>(minDistanceMap.values());
        Log.d(TAG, minValues.toString());

        // 4. WKNN的计算：得到最小坐标（欧氏距离的倒数加权）
        double x = calculateWKNN(minValues.get(0), minValues.get(1), minValues.get(2),
                minCoordinateList.get(0).getX(), minCoordinateList.get(1).getX(), minCoordinateList.get(2).getX());
        double y = calculateWKNN(minValues.get(0), minValues.get(1), minValues.get(2),
                minCoordinateList.get(0).getY(), minCoordinateList.get(1).getY(), minCoordinateList.get(2).getY());
        Log.d(TAG, String.valueOf(x));
        Log.d(TAG, String.valueOf(y));

        // 5. 根据得到的最小坐标计算最终的定位点编号
        double[] minDistanceForId = new double[72];
        for (int i = 0; i < 72; i++) {
            minDistanceForId[i] = calculateDistance(x, y, CoordinateRecord.coordinateRecord.get(i).getX(), CoordinateRecord.coordinateRecord.get(i).getY());
        }
        Log.d(TAG, Arrays.toString(minDistanceForId));
        Log.d(TAG, String.valueOf(getMinIndex(minDistanceForId) + 1));

        return new ReturnCoordinates(x, y, getMinIndex(minDistanceForId) + 1);
    }

    // WKNN的计算
    private static double calculateWKNN(double minValue01, double minValue02, double minValue03, float p01, float p02, float p03) {
        double temp = (1 / (1 / minValue01 + 1 / minValue02 + 1 / minValue03));
        // Log.d(TAG, String.valueOf(temp));
        double result = ((1 / minValue01) * p01 + (1 / minValue02) * p02 + (1 / minValue03) * p03) * temp;
        // Log.d(TAG, String.valueOf(result));
        // 保留两位小数
        BigDecimal formatHelper = new BigDecimal(result);
        return formatHelper.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // 定位点编号计算：欧氏距离
    private static double calculateDistance(double minX, double minY, float coordinateX, float coordinateY) {
        double distance = Math.sqrt(Math.pow(minX - coordinateX, 2) + Math.pow(minY - coordinateY, 2));
        // 保留两位小数
        BigDecimal formatHelper = new BigDecimal(distance);
        return formatHelper.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // 得到数组最小值索引
    private static int getMinIndex(double[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int minIndex = 0;
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[minIndex] > arr[i + 1]) {
                minIndex = i + 1;
            }
        }
        return minIndex;
    }
}
