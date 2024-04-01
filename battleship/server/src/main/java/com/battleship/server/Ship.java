package com.battleship.server;

import java.awt.*;
import java.util.ArrayList;

public class Ship {
    private final Point bottomLeft, topRight;
    private final ArrayList<Point> holes;

    public Ship(Point bottomLeft, Point topRight) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
        this.holes = new ArrayList<>();
    }

    public Point getBottomLeft() {
        return bottomLeft;
    }

    public Point getTopRight() {
        return topRight;
    }

    public boolean getShot(Point globalShotPosition) {
        if (this.bottomLeft.x <= globalShotPosition.x && globalShotPosition.x <= this.topRight.x) {
            if (this.bottomLeft.y <= globalShotPosition.y && globalShotPosition.y <= this.topRight.y) {
                Point localHitPosition = new Point(globalShotPosition.x - bottomLeft.x, globalShotPosition.y - bottomLeft.y);
                if (!this.holes.contains(localHitPosition))
                    this.holes.add(localHitPosition);
                return true;
            }
        }
        return false;
    }

    public boolean isShotDown() {
        int length = this.topRight.x - this.bottomLeft.x + 1;
        int width = this.topRight.y - this.bottomLeft.y + 1;

        return (this.holes.size() == length * width);
    }
}
