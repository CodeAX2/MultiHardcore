package dev.jd.multihardcore;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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

					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

						public void run() {
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
						}

					});

					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
						plugin.getWorldManager().resetMainWorlds();
					}, 10L);

					Bukkit.broadcastMessage("Event done!");

				}

			}
		}

	}

	private LinkedList<Entity> netherPortalEntities = new LinkedList<>();
	private LinkedList<Entity> endPortalEntities = new LinkedList<>();

	@EventHandler
	public void onEntityEnterPortal(EntityPortalEnterEvent event) {

		boolean wasAdded = false;

		if (event.getLocation().getBlock().getType() == Material.NETHER_PORTAL) {
			if (!netherPortalEntities.contains(event.getEntity())) {
				netherPortalEntities.add(event.getEntity());
				wasAdded = true;
				Bukkit.broadcastMessage("Added: " + event.getEntity() + " to nether portals.");
			}
		} else if (event.getLocation().getBlock().getType() == Material.END_PORTAL) {
			if (!endPortalEntities.contains(event.getEntity())) {
				endPortalEntities.add(event.getEntity());
				wasAdded = true;
				Bukkit.broadcastMessage("Added: " + event.getEntity() + " to end portals.");
			}
		}

		if (wasAdded) {

			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {

					if (netherPortalEntities.remove(event.getEntity()) || endPortalEntities.remove(event.getEntity()))
						Bukkit.broadcastMessage("Removed: " + event.getEntity());

				}
			}, 10L);
		}

	}

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event) {

		Bukkit.broadcastMessage("Player Portal!");

		EntityPortalEvent ePortalEvent = new EntityPortalEvent((Entity) event.getPlayer(), event.getFrom(),
				event.getTo(), event.getSearchRadius());

		onEntityPortal(ePortalEvent);

		event.setTo(ePortalEvent.getTo());

	}

	@EventHandler
	public void onEntityPortal(EntityPortalEvent event) {
		// Get the world we are coming from
		World fromWorld = event.getFrom().getWorld();

		Bukkit.broadcastMessage("Portal!");

		if (netherPortalEntities.remove(event.getEntity())) {

			Bukkit.broadcastMessage("Nether Portal!");

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

		} else if (endPortalEntities.remove(event.getEntity())) {
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

	@EventHandler
	public void onExitEnd(PlayerRespawnEvent event) {
		if (endPortalEntities.remove(event.getPlayer())) {
			if (!event.isBedSpawn() && !event.isAnchorSpawn()) {
				event.setRespawnLocation(plugin.getWorldManager().getOverworld().getSpawnLocation());
			}
		}
	}
}
