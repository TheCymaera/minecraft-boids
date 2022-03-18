package com.heledron.minecraft.boids;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Bounds {
	Location origin;
	Vector size;

	public Bounds(Location origin, Vector size) {
		this.origin = origin;
		this.size = size;
	}

	static Bounds fromCenter(Location location, Vector size) {
		return new Bounds(location.clone().add(size.clone().multiply(-.5)), size.clone());
	}

	double minX() {
		return origin.getX();
	}

	double minY() {
		return origin.getY();
	}

	double minZ() {
		return origin.getZ();
	}

	double maxX() {
		return origin.getX() + size.getX();
	}

	double maxY() {
		return origin.getY() + size.getY();
	}

	double maxZ() {
		return origin.getZ() + size.getZ();
	}

	boolean contains(Location loc) {
		if (loc.getX() <  minX() || maxX() < loc.getX()) return false;
		if (loc.getY() <  minY() || maxY() < loc.getY()) return false;
		if (loc.getZ() <  minZ() || maxZ() < loc.getZ()) return false;
		return true;
	}
}
