package com.example.wifi.database;

// AP数据抽象的 class
public class ApData {
    private int apId;       // AP的编号
    private String macAddress;     // AP的 mac地址
    private int rss;            // AP的 rss强度

    public ApData(int apId, String macAddress, int rss) {
        this.apId = apId;
        this.macAddress = macAddress;
        this.rss = rss;
    }

    public int getApId() {
        return apId;
    }

    public void setApId(int apId) {
        this.apId = apId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getRss() {
        return rss;
    }

    public void setRss(int rss) {
        this.rss = rss;
    }
}
