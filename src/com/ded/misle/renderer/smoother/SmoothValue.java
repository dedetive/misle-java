package com.ded.misle.renderer.smoother;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.ded.misle.game.GamePanel.deltaTime;

public class SmoothValue {
    private float current;
    private float target;

    private final List<ValueModifier> modifiers = new ArrayList<>();

    public SmoothValue(float initial) {
        this.target = initial;
        this.current = 0;
        update(0);
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public void update(float speed) {
        if (current == 0 && target != 0) {
            current = target;
        } else if (Math.abs(current - target) >= 0.1f) {
            current += (float)((target - current) * deltaTime * speed);
        }

        Iterator<ValueModifier> iter = modifiers.iterator();
        while (iter.hasNext()) {
            ValueModifier m = iter.next();
            m.update((float) deltaTime);
            if (m.isFinished()) iter.remove();
        }
    }

    public void addModifier(ValueModifier modifier) {
        this.modifiers.add(modifier);
    }

    public void addModifiers(ValueModifier... modifiers) {
        this.modifiers.addAll(Arrays.asList(modifiers));
    }

    public float getCurrentFloat() {
        float sum = current;
        for (ValueModifier mod : modifiers) {
            sum += mod.getOffset();
        }
        return sum;
    }

    public int getCurrentInt() {
        return Math.round(getCurrentFloat());
    }
}
