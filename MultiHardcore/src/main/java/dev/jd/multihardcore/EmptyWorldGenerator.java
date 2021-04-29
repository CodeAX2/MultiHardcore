package dev.jd.multihardcore;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import net.md_5.bungee.api.ChatColor;

public class EmptyWorldGenerator extends ChunkGenerator {

	@Override
	public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {

		ChunkData data = createChunkData(world);

		data.setRegion(0, 0, 0, 15, 15, 15, Material.AIR);

		if (x == 0 && z == 0) {
			data.setRegion(0, 64, 0, 15, 65, 15, Material.GRASS);
			Bukkit.broadcastMessage(ChatColor.GREEN + "Created Grass!");
		}

		return data;

	}

}
