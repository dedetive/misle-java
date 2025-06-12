package com.ded.misle.world.data;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a range of coins that can be dropped, defined by a minimum and maximum amount.
 * Provides functionality to randomly roll an amount within this range.
 *
 * <p>This class is immutable and ensures that {@code min} is not greater than {@code max}.
 */
public record CoinDropRange(int min, int max) {

    /**
     * Constructs a {@code CoinDropRange} with the specified minimum and maximum coin values.
     *
     * @param min the minimum number of coins that can be dropped (inclusive)
     * @param max the maximum number of coins that can be dropped (inclusive)
     * @throws IllegalArgumentException if {@code min > max}
     */
    public CoinDropRange {
        if (min > max) {
            throw new IllegalArgumentException("Min coins must be <= max coins");
        }
    }

    /**
     * Constructs a {@code CoinDropRange} that always returns a fixed amount.
     *
     * @param fixedAmount the exact number of coins to drop
     */
    public CoinDropRange(int fixedAmount) {
        this(fixedAmount, fixedAmount);
    }

    /**
     * Rolls a random coin amount within the defined range, inclusive.
     *
     * @return a random integer between {@code min} and {@code max}, inclusive
     */
    public int roll() {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
