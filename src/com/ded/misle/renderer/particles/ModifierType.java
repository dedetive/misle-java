package com.ded.misle.renderer.particles;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ModifierType {
	Type value();

	enum Type {
		DESTRUCTIVE,
		POSITIONAL,
		TRANSFORMER,
		GENERIC,
	}
}