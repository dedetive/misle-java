package com.ded.misle.world.boxes;

import com.ded.misle.world.logic.Path;
import com.ded.misle.world.logic.PhysicsEngine;

import java.awt.*;

public class BoxSight {
    private final Box origin;
    private Box target;
    private boolean mustUpdate = true;
    private boolean hasDirectSight = false;

    public BoxSight(Box origin, Box target) {
        this.origin = origin;
        this.target = target;
    }

    public BoxSight(Box origin) {
        this.origin = origin;
    }

    public boolean canSee(Box target) {
        if (mustUpdate) {
            Path pathToTarget = new Path(origin, target);

            hasDirectSight = true;
            for (Point point : pathToTarget.getPoints()) {
                if (PhysicsEngine.isSpaceOccupied(point.x, point.y)) {
                    hasDirectSight = false;
                    break;
                }
            }
        }


        mustUpdate = false;
        return hasDirectSight;
    }

    public boolean canSee() {
        if (target == null) throw new NullPointerException("The target box for this BoxSight is null");

        return canSee(this.target);
    }

    public void setTarget(Box target) {
        this.target = target;
        mustUpdate = true;
    }
}
