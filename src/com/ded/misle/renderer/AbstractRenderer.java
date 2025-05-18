package com.ded.misle.renderer;

import com.ded.misle.input.MouseHandler;

import java.awt.*;

public abstract class AbstractRenderer {
    public abstract void render(Graphics g, MouseHandler mouseHandler);
}
