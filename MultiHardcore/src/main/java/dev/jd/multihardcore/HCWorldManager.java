package dev.jd.multihardcore;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class HCWorldManager {

	private App plugin;

	private World purgatory;
	private World[] mainWorlds;

	public HCWorldManager(App plugin) {
		this.plugin = plugin;

		mainWorlds = new World[3];
		mainWorlds[0] = Bukkit.getWorld("world");
		mainWorlds[1] = Bukkit.getWorld("world_nether");
		mainWorlds[2] = Bukkit.getWorld("world_the_end");

	}

	public void loadPurgatory() {

		WorldCreator wc = new WorldCreator("purgatory");
		wc.environment(World.Environment.NORMAL);
		wc.generator(new EmptyWorldGenerator());
		wc.generateStructures(false);
		purgatory = wc.createWorld();
		purgatory.setDifficulty(Difficulty.PEACEFUL);

	}

	public World getPurgatory() {
		return purgatory;
	}

	public void resetMainWorlds() {

		// Create a new seed for the new world
		Random rand = new Random();
		long seed = rand.nextLong();

		// Loop over the main worlds
		for (int i = 0; i < mainWorlds.length; i++) {
			// Unload the world
			Bukkit.broadcastMessage(Bukkit.unloadWorld(mainWorlds[i], false) + "");

			// Delete the folder containing the world
			mainWorlds[i].getWorldFolder().delete();

			// Create a new world with the new seed
			WorldCreator wc = new WorldCreator(mainWorlds[i].getName());
			wc.environment(mainWorlds[i].getEnvironment());
			wc.seed(seed);

			mainWorlds[i] = wc.createWorld();

			// Set the difficulty to hard
			mainWorlds[i].setDifficulty(Difficulty.HARD);
		}

		Bukkit.broadcastMessage("World done!");

	}

}
