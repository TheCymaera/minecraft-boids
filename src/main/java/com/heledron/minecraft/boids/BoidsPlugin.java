package com.heledron.minecraft.boids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class BoidsPlugin extends JavaPlugin {
	boolean avoidance = true;
	boolean alignment = true;
	boolean cohesion = true;
	
    @Override
    public void onEnable() {
        System.out.println("===================================");
        System.out.println("Starting Boids Plugin...");

		for (Player player : getServer().getOnlinePlayers()) {
			player.sendMessage("Boids: Enabled");
		}

		final World world = getServer().getWorld("world");
		final Bounds bounds = Bounds.fromCenter(new Location(world, 0,75,0), new Vector(20, 10, 20));

		// generate boids
		final List<Boid> boids = new ArrayList<Boid>();
		for (int i = 0; i < 70; i++) {
			final Location location = new Location(
				world, 
				bounds.minX() + Math.random() * bounds.size.getX(),
				bounds.minY() + Math.random() * bounds.size.getY(),
				bounds.minZ() + Math.random() * bounds.size.getZ()
			);

			final Boid boid = new Boid(location, bounds);
			boid.velocity = Vector.getRandom();
			boids.add(boid);
		}

		getCommand("boids").setExecutor((sender, command, label, args)->{
			if (args.length == 2) {
				boolean value = args[1].equals("true") ? true : false;
				switch(args[0]) {
					case "avoidance": avoidance = value; return true;
					case "alignment": alignment = value; return true;
					case "cohesion": cohesion = value; return true;
				}
            }
			return false;
		});

		// update and render boids every game tick.
		getServer().getScheduler().runTaskTimer(this, ()->{
			// avoid blocks
			Collection<Boid.Obstacle> obstacles = new ArrayList<Boid.Obstacle>();
			for (int x = (int)bounds.minX(); x < bounds.maxX(); x++) {
				for (int y = (int)bounds.minY(); y < bounds.maxY(); y++) {
					for (int z = (int)bounds.minZ(); z < bounds.maxZ(); z++) {
						final Location loc = new Location(world, x, y, z);
						if (loc.getBlock().getType() != Material.AIR) {
							obstacles.add(new Boid.Obstacle(loc, 4, .07));
						}
					}
				}
			}

			// avoid entities
			for (Entity entity : entityUtilities.allEntities()) {
				final Location loc = entity.getLocation();
				if (boidRenderer.isDisplay(entity)) continue;
				if (!bounds.contains(entity.getLocation())) continue;
				obstacles.add(new Boid.Obstacle(loc, 5, .1));
			}

			// update and render
			for (Boid boid : boids) boid.update(boids, obstacles, avoidance, alignment, cohesion);
			boidRenderer.draw(boids);
		}, 0, 1);

        System.out.println("Complete");
        System.out.println("===================================");
    }

    @Override
    public void onDisable() {
        System.out.println("===================================");
        System.out.println("Stopping Boids Plugin...");
		for (Player player : getServer().getOnlinePlayers()) {
			player.sendMessage("Boids: Disabled");
		}
        System.out.println("Complete");
        System.out.println("===================================");
    }
}