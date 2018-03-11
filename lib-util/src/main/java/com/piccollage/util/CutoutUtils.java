//  Copyright Nov 2017-present CardinalBlue
//
//  Author: prada@cardinalblue.com
//          boy@cardinalblue.com
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package com.piccollage.util;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class CutoutUtils {

    /**
     * The points of the path are determined by referring the left-top as origin.
     * This function returns the points that is determined by referring the center
     * as the origin.
     * <br/>
     * For example:
     * <pre>
     * o is the left-top
     * + is the center
     *
     * o ---------------.
     * |           .--- |
     * |           |   ||
     * |       +  /   / |
     * |         /    \ |
     * |        '-----' |
     * '----------------'
     * </pre>
     */
    public static List<PointF> inflatePathWithCenterOrigin(List<PointF> points,
                                                           float width,
                                                           float height) {
        final List<PointF> copy = new ArrayList<>();
        for (PointF pt : points) {
            copy.add(new PointF((pt.x - 0.5f) * width,
                                (pt.y - 0.5f) * height));
        }

        return copy;
    }

    /**
     * The points of the path are determined by referring the origin as left-top.
     * This function returns the points that is determined by referring the center
     * as the origin in the VISIBLE boundary.
     * <br/>
     * For example:
     * <pre>
     * o is the left-top
     * + is the center
     *
     * o----------------.
     * |           .--- |
     * |           |   ||
     * |          /+  / |
     * |         /    \ |
     * |        '-----' |
     * '----------------'
     * </pre>
     */
    public static List<PointF> inflatePathWithCenterOriginInVisibleBound(List<PointF> points,
                                                                         float width,
                                                                         float height) {
        float left = Float.MAX_VALUE;
        float top = Float.MAX_VALUE;
        float right = Float.MIN_VALUE;
        float bottom = Float.MIN_VALUE;
        for (PointF pt : points) {
            left = Math.min(left, pt.x);
            top = Math.min(top, pt.y);
            right = Math.max(right, pt.x);
            bottom = Math.max(bottom, pt.y);
        }

        // Shift the origin to the center of the points.
        final float normalizedHalfWidth = (right - left) / 2f;
        final float normalizedHalfHeight = (bottom - top) / 2f;
        final List<PointF> copy = new ArrayList<>();
        for (PointF pt : points) {
            copy.add(new PointF(
                (pt.x - left - normalizedHalfWidth) * width,
                (pt.y - top - normalizedHalfHeight) * height));
        }

        return copy;
    }

    /**
     * The points of the path are determined by referring the left-top as origin.
     * This function returns the points that is determined by referring the left-
     * top as the origin.
     * <br/>
     * For example:
     * <pre>
     * o is the left-top
     * + is the center
     *
     * + ---------------.
     * |           .--- |
     * |           |   ||
     * |          /   / |
     * |         /    \ |
     * |        '-----' |
     * '----------------'
     * </pre>
     */
    public static List<PointF> inflatePath(List<PointF> points,
                                           float width,
                                           float height) {
        final List<PointF> copy = new ArrayList<>();
        for (PointF pt : points) {
            copy.add(new PointF(pt.x * width, pt.y * height));
        }

        return copy;
    }

    public static void rebuildPath(Path path, List<PointF> pts) {
        PointF pointStart = pts.get(0);
        float prevX = pointStart.x;
        float prevY = pointStart.y;
        float x;
        float y;
        path.moveTo(prevX, prevY);

        for (int i = 1; i < pts.size(); i++) {
            PointF point = pts.get(i);
            x = point.x;
            y = point.y;

            // Workaround : we used line-to function for appending the corner points for the dividing
            // paths. but the new ScrapUtils.inflatePathForPresenting will only return the array of
            // points(it was Path object). so we use this way to fix it.
            if (onEdge(prevX, prevY, x, y)) {
                path.lineTo(x, y);
            } else {
                path.quadTo(prevX, prevY, (x + prevX) / 2, (y + prevY) / 2);
            }
            prevX = x;
            prevY = y;
        }
    }

    private static boolean onEdge(float x1, float y1, float x2, float y2) {
        if (x1 == x2 && (x1 == 0 || x1 == 1)) {
            return true;
        }
        if (y1 == y2 && (y1 == 0 || y1 == 1)) {
            return true;
        }
        return false;
    }

    public static int getIndexOfClosestPoint(List<PointF> points,
                                             float x,
                                             float y,
                                             float minDistanceThreshold) {
        double minDistance = Double.MAX_VALUE;
        int minDistanceIndex = -1;
        boolean isCollecting = false;

        for (int i = points.size() - 1; i >= 0; i--) {
            PointF point = points.get(i);
            double distance = Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2);
            // when user touches a point quite near one recorded point
            if (distance < minDistanceThreshold) {
                isCollecting = true;

                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceIndex = i;
                }
            } else if (isCollecting) {
                break;
            }
        }

        return minDistanceIndex;
    }

    /**
     * @return The angle (in degrees) between and vertical axis and a line formed by the last two points.
     */
    public static double getEndAngle(List<PointF> pts) {
        int size = pts.size();
        if (size < 2) {
            return 0;
        }

        PointF point1 = pts.get(size - 2);
        PointF point2 = pts.get(size - 1);
        return Math.toDegrees(Math.atan2(point2.x - point1.x, -point2.y + point1.y));
    }
}
