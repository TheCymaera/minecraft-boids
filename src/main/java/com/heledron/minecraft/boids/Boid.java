package com.heledron.minecraft.boids;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Boid {
	static public class Obstacle {
		Location position;
		double avoidRadius;
		double avoidFactor;
		public Obstacle(Location position, double avoidRadius, double avoidFactor) {
			this.position = position;
			this.avoidRadius = avoidRadius;
			this.avoidFactor = avoidFactor;
		}
	}

	Bounds bounds;
	Location position;
	Vector velocity = new Vector(0,0,0);

	// this should be equal to velocity unless velocity is zero.
	// this will give us a non-zero vector for rendering.
	Vector direction = new Vector(1,0,0);

	Boid(Location position, Bounds bounds) {
		this.position = position;
		this.bounds = bounds;
	}

	void update(Collection<? extends Boid> boids, Collection<Boid.Obstacle> obstacles, boolean avoidance, boolean alignment, boolean cohesion) {
		final double flockRadius = 2.5;
		final double maxVelocity = .3;
		Collection<Boid> flock = _getInRange(boids, this.position, flockRadius);
		
		Vector acceleration = new Vector();
		
		acceleration.add(_stayInBounds());

		if (avoidance) {
			Collection<Obstacle> flockObstacles = new ArrayList<Obstacle>();
			for (Boid boid : flock) flockObstacles.add(new Boid.Obstacle(boid.position, .5, .05));

			acceleration.add(_awayFrom(obstacles));
			acceleration.add(_awayFrom(flockObstacles));
		}
		if (alignment) {
			acceleration.add(_averageVelocity(flock).multiply(0.06));
		}
		if (cohesion) {
			acceleration.add(_centerDisplacement(flock).multiply(0.006));
		}


		velocity.add(acceleration);

		if (!velocity.equals(new Vector())) {
			if (velocity.length() > maxVelocity) velocity.normalize().multiply(maxVelocity);
			direction = velocity.clone();
		}

		position.add(velocity);
	}

	private Vector _stayInBounds() {
		final Vector acceleration = new Vector();
		final double magnitude = .025;

		if (position.getX() < bounds.minX()) acceleration.setX(magnitude);
		if (position.getY() < bounds.minY()) acceleration.setY(magnitude);
		if (position.getZ() < bounds.minZ()) acceleration.setZ(magnitude);
			
		if (position.getX() > bounds.maxX()) acceleration.setX(-magnitude);
		if (position.getY() > bounds.maxY()) acceleration.setY(-magnitude);
		if (position.getZ() > bounds.maxZ()) acceleration.setZ(-magnitude);

		return acceleration;
	}

	private Vector _averageVelocity(Collection<Boid> flock) {
		if (flock.size() == 0) return new Vector();
		
		Vector avg = new Vector();
		for (Boid other : flock) avg.add(other.velocity);
		avg.multiply(1.0/flock.size());

		return avg;
	}

	private Vector _centerDisplacement(Collection<Boid> flock) {
		if (flock.size() == 0) return new Vector();

		final Vector center = new Vector();
		for (Boid other : flock) center.add(other.position.toVector());
		center.multiply(1.0/flock.size());

		return center.subtract(position.toVector());
	}

	private Vector _awayFrom(Collection<Obstacle> obstacles) {
		Vector acc = new Vector();
		for (Obstacle obstacle : obstacles) {
			double distance = this.position.distance(obstacle.position);
			if (distance > obstacle.avoidRadius) continue;

			Vector diff = this.position.toVector().subtract(obstacle.position.toVector());
			acc.add(diff.multiply(obstacle.avoidFactor));
		}
		
		return acc;
	}

	private Collection<Boid> _getInRange(Collection<? extends Boid> boids, Location location, double radius) {
		Collection<Boid> out = new ArrayList<Boid>();
		for (Boid other : boids) {
			double distance = this.position.distance(other.position);
			if (distance < radius) out.add(other);
		}
		return out;
	}
}
