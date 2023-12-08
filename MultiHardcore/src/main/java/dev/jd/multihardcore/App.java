package dev.jd.multihardcore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

	private File deathFile;

	@Override
	public void onEnable() {

		config = getConfig();
		loadConfig();

		if (config.getBoolean("usedeathfile"))
			openDeathFile();
		else
			getLogger().info("Not using death file.");

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
			for (Player p : getServer().getOnlinePlayers()) {
				if (p == null) continue;

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

			for (Player p : getServer().getOnlinePlayers()) {
				if (p == null) continue;
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

		config.addDefault("autoshutdown", false);

		config.addDefault("usedeathfile", false);
		config.addDefault("deathfile", "death.txt");

		config.options().copyDefaults(true);
		saveConfig();

	}

	private void openDeathFile() {
		deathFile = new File(config.getString("deathfile"));
		try {
			FileOutputStream fs = new FileOutputStream(deathFile, false);
			fs.write('0');
			fs.close();
			getLogger().info("Successfully reset death file.");
		} catch (IOException e) {
			getLogger().warning("Could not operate on deathfile: " + config.getString("deathfile"));
		}
	}

	public void writeDeathFile() {
		try {
			FileOutputStream fs = new FileOutputStream(deathFile, false);
			fs.write('1');
			fs.close();
			getLogger().info("Successfully wrote to death file.");
		} catch (IOException e) {
			getLogger().warning("Could not operate on deathfile: " + config.getString("deathfile"));
		}
	}

	public void shutdownServer() {
		getServer().shutdown();
	}

	@Override
	public void onDisable() {

		saveConfig();
		getLogger().info("Goodbye!");

	}

}
