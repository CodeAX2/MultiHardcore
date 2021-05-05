package dev.jd.multihardcore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

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
		if (event.getEntityType() == EntityType.PLAYER && event.getEntity() instanceof Player) {

			Player player = (Player) event.getEntity();

			// Check if the event killed the player
			if (player.getHealth() - event.getFinalDamage() <= 0) {

				// The player was killed, so reset the world
				event.setCancelled(true);

				OfflinePlayer[] allPlayers = Bukkit.getOfflinePlayers();

				for (int i = 0; i < allPlayers.length; i++) {
					Player p = allPlayers[i].getPlayer();

					p.teleport(new Location(Bukkit.getWorld(config.getString("purgatory.world")),
							config.getDouble("purgatory.spawn.x"), config.getDouble("purgatory.spawn.y"),
							config.getDouble("purgatory.spawn.z")));

					p.getInventory().clear();
					p.setExp(0);
					p.setLevel(0);
					p.setHealth(20);
					p.setFoodLevel(20);
					p.setFallDistance(0);
					p.setFireTicks(0);

					plugin.getWorldManager().resetMainWorlds();
					Bukkit.broadcastMessage("Event done!");

				}

			}
		}

	}

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event) {
		// Get the world we are coming from
		World fromWorld = event.getFrom().getWorld();

		if (event.getCause() == TeleportCause.NETHER_PORTAL) {
			// Went through a nether portal
			if (fromWorld.getEnvironment() == Environment.NORMAL) {
				// From the overworld, so go to the nether
				event.getTo().setWorld(plugin.getWorldManager().getNether());
			} else if (fromWorld.getEnvironment() == Environment.NETHER) {
				// From the nether, so go to the overworld, with location times 8
				Location newToLocation = event.getFrom().multiply(8.0D);
				newToLocation.setWorld(plugin.getWorldManager().getOverworld());
				event.setTo(newToLocation);
			}

		} else if (event.getCause() == TeleportCause.END_PORTAL) {
			// Went through an end portal
			if (fromWorld.getEnvironment() == Environment.NORMAL) {
				// From the overworld, go to the end
				event.getTo().setWorld(plugin.getWorldManager().getEnd());
			} else if (fromWorld.getEnvironment() == Environment.THE_END) {
				// From the end, go to the overworld
				event.getTo().setWorld(plugin.getWorldManager().getOverworld());
			}
		}

	}
}
