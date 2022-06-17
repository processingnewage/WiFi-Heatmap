# -*- coding: utf-8 -*-
import cv2
from paho.mqtt import client as MqttClient
import GlobalRes
import HeatMap

broker = 'broker-cn.emqx.io'
port = 1883
topic = "LocationUpload"
client_id = "Windows_Device"


def connect_mqtt() -> MqttClient:

    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Connected to MQTT Broker!")
        else:
            print("Failed to connect, return code %d\n", rc)

    def on_disconnect(client, userdata, rc):
        print("Connection has ended!")

    client = MqttClient.Client(client_id)
    client.on_connect = on_connect
    client.on_disconnect = on_disconnect
    client.connect(broker, port)
    return client


def subscribe(client: MqttClient):

    def on_message(client, userdata, msg):
        print(f"Received `{msg.payload.decode()}` from `{msg.topic}` topic")
        '''刷新数据'''
        if len(msg.payload.decode()) > 1:
            device_name, location_id = GlobalRes.parse_string_by_mqtt(
                msg.payload.decode())
            GlobalRes.location_dict.update({device_name: location_id})
            GlobalRes.location_dict_to_datas()
        '''绘制热力图'''
        print(GlobalRes.location_dict)  # 输出设备字典
        print(GlobalRes.datas)  # 输出datas列表
        GlobalRes.init_heat_map()  # 绘制热力图

    client.subscribe(topic)
    client.on_message = on_message
    # 回调函数


if __name__ == '__main__':
    client = connect_mqtt()
    subscribe(client)
    client.loop_forever()
