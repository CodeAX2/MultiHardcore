package dev.jd.multihardcore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import net.md_5.bungee.api.ChatColor;

public class App extends JavaPlugin {

	private MHCListener eventListener;
	private FileConfiguration config;

	private Scoreboard sidebarBoard;

	@Override
	public void onEnable() {

		config = getConfig();

		loadConfig();

		eventListener = new MHCListener(this);
		getServer().getPluginManager().registerEvents(eventListener, this);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

			config.set("secondsalive", config.getLong("secondsalive") + 1);

		}, 0, 20L);

		sidebarBoard = Bukkit.getScoreboardManager().getNewScoreboard();
		CustomSidebar sidebar = new CustomSidebar(sidebarBoard, "mhcData", ChatColor.RED + "Multi-Hardcore");

		sidebar.setLine(15, ChatColor.YELLOW + "-=-=-=-=-=-=-=-=-");

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

			long secondsAlive = config.getLong("secondsalive");
			int aliveH = (int) (secondsAlive / (60 * 60));
			int aliveM = (int) (secondsAlive - aliveH * 60 * 60) / 60;
			int aliveS = (int) (secondsAlive - aliveH * 60 * 60 - aliveM * 60);
			sidebar.setLine(14,
					ChatColor.GREEN + "Alive: " + ChatColor.GOLD + aliveH + "h" + aliveM + "m" + aliveS + "s");
			sidebar.setLine(13, ChatColor.GREEN + "Iteration: " + ChatColor.GOLD + config.getInt("iteration"));
			sidebar.setLine(12, "   ");

			int line = 11;
			for (Player p : Bukkit.getOnlinePlayers()) {

				String healthColor = ChatColor.GREEN.toString();
				if (p.getHealth() < 18)
					healthColor = ChatColor.YELLOW.toString();
				if (p.getHealth() <= 10)
					healthColor = ChatColor.RED.toString();

				String nameColor = ChatColor.BLUE.toString();
				if (p.getWorld().getEnvironment() == Environment.NETHER) {
					nameColor = ChatColor.DARK_RED.toString();
				} else if (p.getWorld().getEnvironment() == Environment.THE_END) {
					nameColor = ChatColor.DARK_PURPLE.toString();
				}

				sidebar.setLine(line, nameColor + p.getName() + ": " + healthColor + (int) p.getHealth());
				line--;
			}

			while (line > 0) {
				sidebar.clearLine(line);
				line--;
			}

			for (Player p : Bukkit.getOnlinePlayers()) {
				p.setScoreboard(sidebar.getBoard());
			}

		}, 0, 5L);

	}

	private void loadConfig() {

		config.addDefault("failuretext.main", ChatColor.RED + "YOU LOSE!");
		List<String> subText = new ArrayList<>();
		subText.add(ChatColor.GOLD + "sudo git gud");
		subText.add(ChatColor.GOLD + "Wow, you suck");
		subText.add(ChatColor.GOLD + "RIP");
		config.addDefault("failuretext.sub", subText);

		config.addDefault("secondsalive", 0);
		config.addDefault("iteration", 0);

		config.options().copyDefaults(true);
		saveConfig();

	}

	@Override
	public void onDisable() {

		saveConfig();
		getLogger().info("Goodbye!");

	}

}
