package dev.jd.multihardcore;

import java.util.List;
import java.util.Random;

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

import net.dv8tion.jda.api.EmbedBuilder;
import net.md_5.bungee.api.ChatColor;
import java.awt.Color;
import java.time.Instant;

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
					long roleID = plugin.getConfig().getLong("discord.mentionRoleID");
					String mention = plugin.getDiscordBot().getRoleMention(roleID);

					EmbedBuilder embed = new EmbedBuilder();
					embed.setTitle("A Player Has Died!");
					embed.setDescription(mention + " " + player.getName() + " has died!");
					embed.setColor(new Color(168, 14, 14));
					embed.setThumbnail("https://static.wikia.nocookie.net/minecraft_gamepedia/images/4/4a/Skeleton_Skull_%288%29.png");
					embed.setTimestamp(Instant.now());

					plugin.getDiscordBot().sendEmbed(embed.build());
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
