package dev.jd.multihardcore;

import java.util.EventListener;

import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {

	private MHCListener eventListener;
	private HCWorldManager worldManager;

	@Override
	public void onEnable() {

		eventListener = new MHCListener(this);
		getServer().getPluginManager().registerEvents(eventListener, this);
		worldManager = new HCWorldManager(this);
		worldManager.loadPurgatory();

	}

	public HCWorldManager getWorldManager(){
		return worldManager;
	}

	@Override
	public void onDisable() {

		getLogger().info("Goodbye!");

	}

}
