# WiFi-Heatmap

### 毕业设计：基于WiFi定位的热力图监测系统设计

### 使用说明

#### 1. Android APP

* Android端通过wifimanager实时收集WiFi RSSI数据

* 基于位置指纹法定位
* 通过MQTT协议上传位置坐标

#### 2. QT HeatMap

* 通过MQTT协议接收各个手机客户端上传的位置坐标
* 实时刷新热力图