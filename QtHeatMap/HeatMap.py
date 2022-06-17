# -*- coding: utf-8 -*-
import cv2
import numpy as np
from PIL import Image
from pyheatmap.heatmap import HeatMap
import GlobalRes


# 绘制热力图的函数
def apply_heat_map(image, datas):
    '''创建蓝底图片'''
    overlay = image.copy()
    # 从(0, 0)到(image.shape[1], image.shape[0])绘制一个颜色为蓝色的矩形, -1表示填充整个矩形
    cv2.rectangle(overlay, (0, 0), (overlay.shape[1], overlay.shape[0]),
                  (255, 0, 0), -1)
    '''用背景让创建的热力图和原图一样大'''
    background = Image.new("RGB", (image.shape[1], image.shape[0]), color=0)
    hm = HeatMap(datas)  # 传入datas数组构造HeatMap对象
    # r表示辐射半径: 最好设置为平均误差对应的像素点
    heat_img = hm.heatmap(base=background, r=130)
    '''转换格式, 方便cv库的使用'''
    heat_img = cv2.cvtColor(np.asarray(heat_img), cv2.COLOR_RGB2BGR)
    '''图片融合'''
    image = cv2.addWeighted(overlay, 0.3, image, 0.7, 0)
    image = cv2.addWeighted(heat_img, 0.5, image, 0.5, 0)
    '''保存绘制好的热力图'''
    cv2.imwrite(GlobalRes.image_heat_map_path, image)


if __name__ == '__main__':
    image_origin = cv2.imread(GlobalRes.image_origin_path)
    # 识别得到的图像中人的中心坐标(像素点): (x, y)
    data_test = [[100, 100], [200, 200], [300, 300]]
    apply_heat_map(image_origin, data_test)
    image_heat_map = cv2.imread(GlobalRes.image_heat_map_path)
    cv2.imshow("heat_map", image_heat_map)
    cv2.waitKey(0)
    cv2.destroyAllWindows()