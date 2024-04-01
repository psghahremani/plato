package com.battleship.server;

import java.awt.Point;
import java.util.Arrays;

public class Utils {
    public static boolean isPointInsideRectangle(Point point, Point rectangleBottomLeft, Point rectangleTopRight) {
        return (point.x >= rectangleBottomLeft.x && point.x <= rectangleTopRight.x) &&
                (point.y >= rectangleBottomLeft.y && point.y <= rectangleTopRight.y);
    }

    public static void normalizeRectanglePoints(Point a, Point b) {
        int[] xList = new int[]{a.x, b.x};
        int[] yList = new int[]{a.y, b.y};

        Arrays.sort(xList);
        Arrays.sort(yList);

        a.setLocation(xList[0], yList[0]);
        b.setLocation(xList[1], yList[1]);
    }

    public static String getRectangleDimensions(Point a, Point b) {
        int length = Math.abs(a.x - b.x) + 1;
        int width = Math.abs(a.y - b.y) + 1;
        if (length > width) {
            return length + "x" + width;
        } else {
            return width + "x" + length;
        }
    }

    public static boolean doOverlap(Point bottomLeft1, Point topRight1, Point bottomLeft2, Point topRight2) {
        return !(topRight1.x < bottomLeft2.x || bottomLeft1.x > topRight2.x || topRight1.y < bottomLeft2.y || bottomLeft1.y > topRight2.y);
    }
}
