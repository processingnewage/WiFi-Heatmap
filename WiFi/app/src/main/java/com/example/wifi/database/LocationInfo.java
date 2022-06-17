package com.example.wifi.database;

// 由位置信息抽象出来的 class
public class LocationInfo {
    private int locationId;     // 位置编号
    private ApData apData01;    // AP节点 1的数据
    private ApData apData02;    // AP节点 2的数据
    private ApData apData03;    // AP节点 3的数据
    private ApData apData04;    // AP节点 4的数据

    public LocationInfo(int locationId, ApData apData01, ApData apData02, ApData apData03, ApData apData04) {
        this.locationId = locationId;
        this.apData01 = apData01;
        this.apData02 = apData02;
        this.apData03 = apData03;
        this.apData04 = apData04;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public ApData getApData01() {
        return apData01;
    }

    public void setApData01(ApData apData01) {
        this.apData01 = apData01;
    }

    public ApData getApData02() {
        return apData02;
    }

    public void setApData02(ApData apData02) {
        this.apData02 = apData02;
    }

    public ApData getApData03() {
        return apData03;
    }

    public void setApData03(ApData apData03) {
        this.apData03 = apData03;
    }

    public ApData getApData04() {
        return apData04;
    }

    public void setApData04(ApData apData04) {
        this.apData04 = apData04;
    }
}
