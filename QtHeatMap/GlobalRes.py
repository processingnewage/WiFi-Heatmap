# -*- coding: utf-8 -*-
import os
import sys
import cv2
import HeatMap

# 图标路径
icon_path = "./res/icon.ico"

# 地图原图路径
image_origin_path = "./res/pic_origin.png"

# 绘制热力图后的地图路径
image_heat_map_path = "./res/heat_map.png"

# 坐标点图片路径
image_coordinate_path = "./res/pic_coordinate.png"

# 存储上传的位置信息的列表: 设备的定位点在地图上对应的坐标列表, 用于绘制热力图
datas = []

# 定位点像素坐标映射字典: 定位点编号和像素坐标的映射关系, 在统计好后即保持静态
# 地图宽为: 1794, 高为: 695
# 注：横坐标两点差值为92, 纵坐标两点差值为95
coordinate_dict = {
    "1": [95, 595],
    "2": [95, 500],
    "3": [95, 405],
    "4": [95, 315],
    "5": [187, 595],
    "6": [187, 500],
    "7": [187, 405],
    "8": [187, 315],
    "9": [279, 595],
    "10": [279, 500],
    "11": [279, 405],
    "12": [279, 315],
    "13": [370, 595],
    "14": [370, 500],
    "15": [370, 405],
    "16": [370, 315],
    "17": [463, 595],
    "18": [463, 500],
    "19": [463, 405],
    "20": [463, 315],
    "21": [555, 595],
    "22": [555, 500],
    "23": [555, 405],
    "24": [555, 315],
    "25": [645, 595],
    "26": [645, 500],
    "27": [645, 405],
    "28": [645, 315],
    "29": [737, 595],
    "30": [737, 500],
    "31": [737, 405],
    "32": [737, 315],
    "33": [828, 595],
    "34": [828, 500],
    "35": [828, 405],
    "36": [828, 315],
    "37": [921, 595],
    "38": [921, 500],
    "39": [921, 405],
    "40": [921, 315],
    "41": [1012, 595],
    "42": [1012, 500],
    "43": [1012, 405],
    "44": [1012, 315],
    "45": [1105, 595],
    "46": [1105, 500],
    "47": [1105, 405],
    "48": [1105, 315],
    "49": [1195, 595],
    "50": [1195, 500],
    "51": [1195, 405],
    "52": [1195, 315],
    "53": [1288, 595],
    "54": [1288, 500],
    "55": [1288, 405],
    "56": [1288, 315],
    "57": [1380, 595],
    "58": [1380, 500],
    "59": [1380, 405],
    "60": [1380, 315],
    "61": [1470, 595],
    "62": [1470, 500],
    "63": [1470, 405],
    "64": [1470, 315],
    "65": [1563, 595],
    "66": [1563, 500],
    "67": [1563, 405],
    "68": [1563, 315],
    "69": [1650, 595],
    "70": [1650, 500],
    "71": [1650, 405],
    "72": [1650, 315]
}

# 定位设备坐标映射字典: 当设备上传定位点编号时就更新该字典
location_dict = {
}


# 将上传的定位点编号转换为像素坐标列表
def location_dict_to_datas():
    datas.clear()  # 清空已有的坐标, 重新组装数据
    locations_list = []
    '''取到所有安卓设备的定位点'''
    for key in location_dict.keys():
        locations_list.append(location_dict[key])
    '''遍历已知定位点列表, 将上传的定位点编号转换为像素坐标列表'''
    for id in locations_list:
        datas.append(coordinate_dict[str(id)])


# 解析上传的字符串得到设备名和定位点编号
# 注：安卓设备上传数据的格式为: A1-32
def parse_string_by_mqtt(msg):
    device_name = msg.split('-')[0]
    location_id = msg.split('-')[1]
    return device_name, int(location_id)


def init_heat_map():
    image_origin = cv2.imread(image_origin_path)
    HeatMap.apply_heat_map(image_origin, datas)