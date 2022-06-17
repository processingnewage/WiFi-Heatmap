package com.example.wifi.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

// 使用 MQTT Client推送消息
public class MyMqtt {
    private MqttClient mqttClient;

    public MyMqtt(String serverUrl, String clientId) {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(serverUrl, clientId, persistence);
            mqttClient.setCallback(new OnMessageCallback());    // 设置回调接口
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);     // 保留会话
            connOpts.setConnectionTimeout(10);    // 设置超时时间，单位：秒
            connOpts.setKeepAliveInterval(120);    // 设置心跳包发送间隔，单位：秒
            mqttClient.connect(connOpts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String topic, int qos, boolean retained, String payload) {
        if (mqttClient == null) {
            return;
        }
        try {
            mqttClient.publish(topic, payload.getBytes(), qos, retained);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            mqttClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            mqttClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
