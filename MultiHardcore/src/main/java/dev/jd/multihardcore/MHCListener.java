package dev.jd.multihardcore;

import java.util.List;
import java.util.Random;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class MHCListener implements Listener {

	private App plugin;
	private FileConfiguration config;

	public MHCListener(App plugin) {
		this.plugin = plugin;
		config = this.plugin.getConfig();
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {

		// Check if a player was damaged
		if (event.getEntityType() == EntityType.PLAYER &&
				event.getEntity() instanceof Player) {

			Player player = (Player) event.getEntity();

			// Check if the event killed the player
			if (player.getHealth() - event.getFinalDamage() <= 0) {

				// The player was killed, so reset the world
				event.setCancelled(true);

				// Set all players to spectator and send message
				OfflinePlayer[] allPlayers = plugin.getServer().getOfflinePlayers();
				for (int i = 0; i < allPlayers.length; i++) {
					Player p = allPlayers[i].getPlayer();
					if (p == null) continue;

					p.setGameMode(GameMode.SPECTATOR);

					String mainText = config.getString("failuretext.main");
					List<String> subTextOptions = config.getStringList("failuretext.sub");

					Random rand = new Random();

					p.sendTitle(mainText,
							subTextOptions.get(rand.nextInt(subTextOptions.size())),
							7, 100, 14);
					p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + player.getName() +
							" DIED!");
					if (config.getBoolean("autoshutdown"))
						p.sendMessage("" + ChatColor.GOLD + ChatColor.BOLD + "The server will restart in 5 seconds!");
					p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 3.f, 1.f);
				}

				if (plugin.discordBotOnline()) {
					plugin.getDiscordBot().sendMessage("@jacobhofer " + player.getName() + " has died!");
				}

				// Set values in the config
				config.set("iteration", config.getInt("iteration") + 1);
				config.set("secondsalive", 0);

				// Write to the death file
				if (config.getBoolean("usedeathfile"))
					plugin.writeDeathFile();

				// Shutdown the server
				if (config.getBoolean("autoshutdown")) {
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						public void run() {
							plugin.shutdownServer();
						}
					}, 100L);
				}
			}
		}
	}
}
