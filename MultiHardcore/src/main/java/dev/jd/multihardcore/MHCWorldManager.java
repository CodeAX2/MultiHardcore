package dev.jd.multihardcore;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

public class MHCWorldManager {

	private App plugin;
	private FileConfiguration config;

	private World[] mainWorlds;

	public MHCWorldManager(App plugin) {
		this.plugin = plugin;
		config = this.plugin.getConfig();

		mainWorlds = new World[3];

		Random rand = new Random();
		long seed = rand.nextLong();

		WorldCreator wc = new WorldCreator(config.getString("mch_worlds.overworld"));
		wc.environment(World.Environment.NORMAL);
		wc.generateStructures(true);
		wc.seed(seed);
		mainWorlds[0] = wc.createWorld();
		mainWorlds[0].setDifficulty(Difficulty.HARD);

		wc = new WorldCreator(config.getString("mch_worlds.nether"));
		wc.environment(World.Environment.NETHER);
		wc.generateStructures(true);
		wc.seed(seed);
		mainWorlds[1] = wc.createWorld();
		mainWorlds[1].setDifficulty(Difficulty.HARD);

		wc = new WorldCreator(config.getString("mch_worlds.end"));
		wc.environment(World.Environment.THE_END);
		wc.generateStructures(true);
		wc.seed(seed);
		mainWorlds[2] = wc.createWorld();
		mainWorlds[2].setDifficulty(Difficulty.HARD);

	}

	public void createPurgatory(String purgatoryWorldName) {

		WorldCreator wc = new WorldCreator(purgatoryWorldName);
		wc.environment(World.Environment.NORMAL);
		wc.generator(new EmptyWorldGenerator());
		wc.generateStructures(false);
		World purgatory = wc.createWorld();
		purgatory.setDifficulty(Difficulty.PEACEFUL);

	}

	public void resetMainWorlds() {

		// Create a new seed for the new world
		Random rand = new Random();
		long seed = rand.nextLong();
		System.out.println(seed);

		// Loop over the main worlds
		for (int i = 0; i < mainWorlds.length; i++) {
			// Unload the world
			mainWorlds[i].setKeepSpawnInMemory(false);
			Bukkit.broadcastMessage(Bukkit.unloadWorld(mainWorlds[i], false) + "");

			// Delete the folder containing the world
			mainWorlds[i].getWorldFolder().delete();

			// Create a new world with the new seed
			WorldCreator wc = new WorldCreator(mainWorlds[i].getName());
			wc.environment(mainWorlds[i].getEnvironment());
			wc.generateStructures(true);
			wc.seed(seed);

			mainWorlds[i] = wc.createWorld();

			// Set the difficulty to hard
			mainWorlds[i].setDifficulty(Difficulty.HARD);
		}

		Bukkit.broadcastMessage("World done!");

	}

	public World getOverworld() {
		return mainWorlds[0];
	}

	public World getNether() {
		return mainWorlds[1];
	}

	public World getEnd() {
		return mainWorlds[2];
	}

}
