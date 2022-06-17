# coding:utf-8
from PyQt5.QtWidgets import (QWidget, QLabel, QApplication, QDesktopWidget,
                             QMessageBox)
from PyQt5.QtGui import QPixmap, QIcon
from PyQt5.QtCore import QTimer, Qt
import sys
import cv2
import threading
import MqttSubClient
import GlobalRes
import HeatMap


class HeatMapWindow(QWidget):

    def __init__(self):
        super().__init__()
        self.title = "车站人员热力检测系统"
        GlobalRes.init_heat_map()  # 初始化热力图，避免残留数据的影响
        self.initUI()

    def initUI(self):
        self.setWindowTitle(self.title)
        self.setWindowIcon(QIcon(GlobalRes.icon_path))
        self.setFixedSize(1794, 695)
        self.center()
        self.label = QLabel(self)
        timer = QTimer(self)
        timer.timeout.connect(self.update_image)
        timer.start(1000)  # 定时器 1s刷新一次数据
        self.update_image()

    # 主窗口居中显示
    def center(self):
        screen = QDesktopWidget().screenGeometry()
        size = self.geometry()
        self.move((screen.width() - size.width()) / 2,
                  (screen.height() - size.height()) / 2)

    # 更新图片
    def update_image(self):
        pixmap = QPixmap(GlobalRes.image_heat_map_path)
        if not pixmap.isNull():
            self.label.setPixmap(pixmap)
            self.label.adjustSize()
            self.resize(pixmap.size())


# mqtt client to connect broker
def loop_mqtt_client():
    client = MqttSubClient.connect_mqtt()
    MqttSubClient.subscribe(client)
    client.loop_forever()


# mqtt thread to accept message from broker and update datas and heat map
def mqtt_thread(func):
    mqtt_thread = threading.Thread(target=func, name='LoopThread')
    mqtt_thread.setDaemon(True)  # 设置守护进程, 在窗口关闭时，断开 Mqtt连接
    mqtt_thread.start()


if __name__ == '__main__':
    '''开启一个新线程去做连接服务器'''
    mqtt_thread(loop_mqtt_client)
    '''展示热力图'''
    app = QApplication(sys.argv)
    heat_map_ex = HeatMapWindow()
    heat_map_ex.show()
    sys.exit(app.exec_())