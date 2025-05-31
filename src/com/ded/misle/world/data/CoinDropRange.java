package com.ded.misle.world.data;

import java.util.concurrent.ThreadLocalRandom;

public record CoinDropRange(int min, int max) {
    public CoinDropRange {
        if (min > max) {
            throw new IllegalArgumentException("Min coins must be <= max coins");
        }
    }

    public CoinDropRange(int fixedAmount) {
        this(fixedAmount, fixedAmount);
    }

    public int roll() {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}