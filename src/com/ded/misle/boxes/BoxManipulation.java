package com.ded.misle.boxes;

import static com.ded.misle.Launcher.scale;

public class BoxManipulation {
	public static void teleportBox(Box box, double x, double y) {
		box.setCurrentX(x);
		box.setCurrentY(y);

	}
}
