package com.heledron.minecraft.boids;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class entityUtilities {
	/**
	 * Get all entities in all worlds.
	 * @return
	 */
	static Collection<Entity> allEntities() {
		Collection<Entity> entities = new ArrayList<Entity>();
		for (World world : Bukkit.getServer().getWorlds()) {
			entities.addAll(world.getEntities());
		}
		return entities;
	}
}
