package com.ded.misle.renderer;

import javax.swing.*;
import java.awt.*;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.renderer.ColorManager.fadingColor;

public class Fader {
    private FadingState state;
    private float fadingProgress;

    public Fader() {
        reset();
    }

    public enum FadingState {
        NONE(),
        FADED(0, 1, NONE),
        UNFADED(0, 0, NONE),
        FADING_IN(0.019F, 1, FADED),
        SLOWLY_FADING_IN(0.005F, 1, FADED),
        FADING_OUT(-0.02125F, 0, UNFADED),
        SLOWLY_FADING_OUT(-0.005F, 0, UNFADED);

        float progressIncrease;
        float progressMax;
        FadingState turnsInto;

        FadingState () {}

        FadingState (float progressIncrease, float progressMax, FadingState turnsInto) {
            this.progressIncrease = progressIncrease;
            this.progressMax = progressMax;
            this.turnsInto = turnsInto;
        }

        public float getProgressIncrease() { return progressIncrease; }

        public float getProgressMax() { return progressMax; }

        public FadingState getTurnsInto() { return turnsInto; }
    }

    public void fadeIn() { state = FadingState.FADING_IN; }

    public void fadeOut() { state = FadingState.FADING_OUT; }

    public void slowlyFadeOut() { state = FadingState.SLOWLY_FADING_OUT; }

    public void slowlyFadeIn() { state = FadingState.SLOWLY_FADING_IN; }

    public void fadeInThenOut(int ms) {
        fadeIn();

        Timer timer = new Timer(ms, e -> {
            fadeOut();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void drawFading(Graphics2D g2d) {
        if (state != FadingState.UNFADED) {
            fadingProgress = (float) Math.clamp(fadingProgress + state.getProgressIncrease() * Math.pow(deltaTime, 0.3) * 6, 0F, 1F);
            g2d.setColor(new Color((float) fadingColor.getRed() / 256, (float) fadingColor.getGreen() / 256, (float) fadingColor.getBlue() / 256, fadingProgress));
            g2d.fillRect(0, 0, (int) screenWidth, (int) screenHeight);
            if (fadingProgress == state.getProgressMax()) {
                state = state.getProgressMax() == 0 ? FadingState.UNFADED : FadingState.FADED;
            }
        }
    }

    public boolean isState(FadingState... state) {
        for (FadingState f : state) {
            if (this.state == f) return true;
        }
        return false;
    }

    public FadingState getState() {
        return this.state;
    }

    public Fader reset() {
        fadingProgress = 0f;
        state = FadingState.UNFADED;
        return this;
    }
}
