package dev.jd.multihardcore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class App extends JavaPlugin {

	private MHCListener eventListener;
	private HCWorldManager worldManager;

	@Override
	public void onEnable() {

		eventListener = new MHCListener(this);
		getServer().getPluginManager().registerEvents(eventListener, this);
		worldManager = new HCWorldManager(this);

	}

	public HCWorldManager getWorldManager() {
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
