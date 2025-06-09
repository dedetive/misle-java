package com.ded.misle.world.entities.ai;

import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.logic.World;

public record BehaviorContext(
    Entity self,
    Entity target,
    World world
) {

}
