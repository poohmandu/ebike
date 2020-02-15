/*
 * Copyright 2019 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a to of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.common.core.util.geo;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 参考: http://blog.csdn.net/happy__888/article/details/315762
 * <p>
 * 多边形_扩展算法
 * <p>
 * Created by niezhao on 2017/9/13.
 */
public class PolygonUtil {

    /**
     * 即： Qi ＝ Pi ＋ (v1 + v2)
     * Qi = Pi + L / sinθ * ( Normalize(v2) + Normalize(v1))
     * Sin θ = |v1 × v2 | /|v1|/|v2|
     * <p>
     * 计算步骤：
     * ⑴、获取多边形顶点数组PList;
     * ⑵、计算DPList[Vi+1-Vi];
     * ⑶、单位化NormalizeDPList,得到NDP[DPi];（用同一个数组存储）
     * ⑷、Sinα = Dp(i+1) X DP(i);
     * ⑸、Qi = Pi + d/sinα (NDPi+1-NDPi)
     * ⑹、这样一次性可以把所有顶点计算完。
     * 注意，交换Qi表达式当中NDPi+1-NDPi的顺序就可以得到外部多边形顶点数组。
     */

    private static Point2D.Double sub(Point2D.Double left, Point2D.Double right) {
        return new Point2D.Double(left.x - right.x, left.y - right.y);
    }

    private static Point2D.Double add(Point2D.Double left, Point2D.Double right) {
        return new Point2D.Double(left.x + right.x, left.y + right.y);
    }

    private static double multi(Point2D.Double left, Point2D.Double right) {
        return left.x * right.x + left.y * right.y;
    }

    private static Point2D.Double multi(Point2D.Double left, double value) {
        return new Point2D.Double(left.x * value, left.y * value);
    }

    private static double multiMulti(Point2D.Double left, Point2D.Double right) {
        return left.x * right.y - left.y * right.x;
    }

    // http://blog.csdn.net/pyx6119822/article/details/42393999
    // http://blog.csdn.net/z278930050/article/details/53319091
    // 初始化顶点队列
    private static List<Point2D.Double> initPList(List<Point2D.Double> plist) {
        int index = 0;
        for (int i = 0; i < plist.size(); i++) {
            if (plist.get(i).getX() > plist.get(index).getX()) {
                index = i;
            }
        }
        Point2D.Double p1;
        Point2D.Double p2;
        Point2D.Double p3;
        for (int i = 0; i < plist.size(); i++) {
            if (index == 0) {
                p1 = plist.get(plist.size() - 1);
                p2 = plist.get(0);
                p3 = plist.get(1);
            } else {
                p1 = plist.get((index - 1) % plist.size());
                p2 = plist.get((index) % plist.size());
                p3 = plist.get((index + 1) % plist.size());
            }
            double p12_p23 = (p2.x - p1.x) * (p3.y - p2.y) - (p2.y - p1.y) * (p3.x - p2.x);//p12和p23的向量积
            if (p12_p23 > 0) {
                Collections.reverse(plist);
                return plist;
            } else if (p12_p23 < 0) {
                return plist;
            } else {
                index++;
            }
        }
        return plist;
    }

    // 初始化dpList  两顶点间向量差
    private static List<Point2D.Double> initDPList(List<Point2D.Double> plist) {
        List<Point2D.Double> dpList = new ArrayList<>();
        for (int i = 0; i < plist.size(); ++i) {
            Point2D.Double sub = sub(plist.get(i == (plist.size() - 1) ? 0 : (i + 1)), plist.get(i));
            dpList.add(sub);
        }
        return dpList;
    }

    // 初始化ndpList，单位化两顶点向量差
    private static List<Point2D.Double> initNDPList(List<Point2D.Double> dpList) {
        List<Point2D.Double> ndpList = new ArrayList<>();
        for (int i = 0; i < dpList.size(); ++i) {
            Point2D.Double dp = dpList.get(i);
            ndpList.add(multi(dp, (1.0 / Math.sqrt(multi(dp, dp)))));
        }
        return ndpList;
    }

    // 计算新顶点， 注意参数为负是向内收缩， 为正是向外扩张
    private static List<Point2D.Double> computeLine(double dist, List<Point2D.Double> plist, List<Point2D.Double> ndpList) {
        List<Point2D.Double> newList = new ArrayList<>();
        for (int i = 0; i < plist.size(); ++i) {
            int startIndex = i == 0 ? (plist.size() - 1) : (i - 1);
            double sina = multiMulti(ndpList.get(startIndex), ndpList.get(i));
            double length = dist / sina;
            Point2D.Double vector = sub(ndpList.get(i), ndpList.get(startIndex));
            Point2D.Double point = add(plist.get(i), multi(vector, length));
            newList.add(point);
        }
        return newList;
    }

    // 比较标准的数据 1度(111公里) 1/111000 <=> 1米
    //0.0001 => 10米 或 0.00005=> 5米 较为合适
    public static List<Point2D.Double> run(List<Point2D.Double> points, int meter) {
        if (points != null && points.size() < 3) {
            throw new RuntimeException("多边形端点不可小于3");
        }
        double dist = ((double) 1 / 111000) * (double) meter;
        List<Point2D.Double> pList = initPList(points);
        List<Point2D.Double> dpList = initDPList(pList);
        List<Point2D.Double> ndpList = initNDPList(dpList);
        return computeLine(dist, pList, ndpList);
    }

}
