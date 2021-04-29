package dev.jd.multihardcore;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class MHCListener implements Listener {

	private App plugin;

	public MHCListener(App plugin) {
		this.plugin = plugin;
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
					p.teleport(new Location(plugin.getWorldManager().getPurgatory(), 0, 65, 0));
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

}
