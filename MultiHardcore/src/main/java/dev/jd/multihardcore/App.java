package dev.jd.multihardcore;

import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {

	@Override
	public void onEnable() {

		getLogger().info("Hello there!");


	}

	@Override
	public void onDisable(){

		getLogger().info("Goodbye!");

	}
	
}
