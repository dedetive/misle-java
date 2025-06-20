package com.ded.misle.renderer.smoother;

/**
 * Represents a temporary effect that may modify a value.
 */
public interface ValueModifier {
    void update(float deltaTime);
    float getOffset();
    boolean isFinished();
    ValueModifier clone() throws CloneNotSupportedException;
}
