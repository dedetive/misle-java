package com.ded.misle.world.entities.ai;

import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.logic.World;

public record BehaviorContext(
    Entity self,
    Entity target,
    World world
) {
    public BehaviorContext(Entity self, Entity target, World world) {
        this.self = self;
        this.target = target;
        this.world = world;
    }

    public BehaviorContext(Entity self, World world) {
        this(self, null, world);
    }
}
