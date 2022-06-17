package com.example.wifi.mqtt;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class OnMessageCallback implements MqttCallback {
    private final String TAG = "MqttCallback";

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "连接断开");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(TAG, "接收消息主题: " + topic);
        Log.d(TAG, "接收消息Qos: " + message.getQos());
        Log.d(TAG, "接收消息内容: " + new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 发送消息成功时的回调
        Log.d(TAG, "deliveryComplete-" + token.isComplete());
    }
}
