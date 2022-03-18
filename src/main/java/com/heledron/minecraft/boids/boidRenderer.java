package com.heledron.minecraft.boids;

import java.util.Collection;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;

public class boidRenderer {
	static final String tagName = "boid";

	static void draw(Iterable<Boid> boids) {
		// delete old entities AFTER the new ones have been created, 
		// otherwise they will flicker.
		Collection<Entity> oldEntities = entityUtilities.allEntities();

		for (Boid boid : boids) {
			Arrow arrow = boid.position.getWorld().spawnArrow(boid.position, boid.direction, (float)boid.direction.length(), 0);
			arrow.setGravity(false);
			arrow.addScoreboardTag(tagName);
		}
		
		for (Entity entity : oldEntities) if (isDisplay(entity)) entity.remove();
	}

	static boolean isDisplay(Entity entity) {
		return entity.getScoreboardTags().contains(tagName);
	}
}
