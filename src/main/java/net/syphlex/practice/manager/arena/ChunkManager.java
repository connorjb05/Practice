package net.syphlex.practice.manager.arena;

import lombok.Getter;
import net.syphlex.practice.Practice;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ChunkManager {

    private final Set<Chunk> chunks = new HashSet<>();

    public void onEnable(){
        new BukkitRunnable(){
            @Override
            public void run(){
                for (Arena arena : Practice.get().getArenaManager().getArenaMap().values()) {

                    int chunkMaxX = arena.getMaxX() >> 4;
                    int chunkMinX = arena.getMinX() >> 4;
                    int chunkMaxZ = arena.getMaxZ() >> 4;
                    int chunkMinZ = arena.getMinZ() >> 4;

                    for (int x = chunkMinX; x <= chunkMaxX; x++) {
                        for (int z = chunkMinZ; z <= chunkMaxZ; z++) {
                            Chunk chunk = arena.getWorld().getChunkAt(x, z);
                            chunk.load(true);
                            chunks.add(chunk);
                        }
                    }
                }
                Practice.get().getLogger().info("Successfully loaded all arena chunks.");
            }
        }.runTaskLater(Practice.get(), 10L);
    }

    public void onDisable(){
        for (Chunk chunk : chunks) {
            if (chunk.isLoaded()) {
                chunk.unload(true);
            }
        }
        chunks.clear();
        Practice.get().getLogger().info("Successfully unloaded all chunks.");
    }

}
