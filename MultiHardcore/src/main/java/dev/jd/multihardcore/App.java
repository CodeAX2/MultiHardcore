package dev.jd.multihardcore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class App extends JavaPlugin {

	private MHCListener eventListener;
	private MHCWorldManager worldManager;
	private FileConfiguration config;

	@Override
	public void onEnable() {

		config = getConfig();

		loadConfig();

		eventListener = new MHCListener(this);
		getServer().getPluginManager().registerEvents(eventListener, this);
		worldManager = new MHCWorldManager(this);

	}

	private void loadConfig() {

		config.addDefault("mch_worlds.overworld", "mhc_world");
		config.addDefault("mch_worlds.nether", "world_nether");
		config.addDefault("mch_worlds.end", "world_the_end");

		config.addDefault("purgatory.world", "world");
		config.addDefault("purgatory.spawn.x", 0);
		config.addDefault("purgatory.spawn.y", 65);
		config.addDefault("purgatory.spawn.z", 0);

		config.options().copyDefaults(true);
		saveConfig();

	}

	public MHCWorldManager getWorldManager() {
		return worldManager;
	}

	@Override
	public void onDisable() {

		getLogger().info("Goodbye!");

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (label.equalsIgnoreCase("purgatory")) {

			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "Invalid arguments! Usage: /purgatory <worldName>");
			} else {
				worldManager.createPurgatory(args[0]);
				sender.sendMessage(ChatColor.GREEN + "Created purgatory world: " + args[0]);
			}

			return true;
		}

		return false;
	}

}
