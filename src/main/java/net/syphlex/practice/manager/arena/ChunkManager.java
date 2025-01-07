package net.syphlex.practice.manager.arena;

import lombok.Getter;
import net.syphlex.practice.Practice;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class ChunkManager {

    private boolean chunksLoaded;

    public void onEnable(){
        new BukkitRunnable(){
            @Override
            public void run(){

                for (Arena arena : Practice.get().getArenaManager().getArenaMap().values()) {

                    Location location1 = arena.getCorner1();
                    Location location2 = arena.getCorner2();

                    if (location1 != null && location2 != null) {
                        int spawnMinX = location1.getBlockX() >> 4;
                        int spawnMinZ = location2.getBlockZ() >> 4;
                        int spawnMaxX = location1.getBlockX() >> 4;
                        int spawnMaxZ = location2.getBlockZ() >> 4;
                        if (spawnMinX > spawnMaxX) {
                            int lastSpawnMinX = spawnMinX;
                            spawnMinX = spawnMaxX;
                            spawnMaxX = lastSpawnMinX;
                        }
                        if (spawnMinZ > spawnMaxZ) {
                            int lastSpawnMinZ = spawnMinZ;
                            spawnMinZ = spawnMaxZ;
                            spawnMaxZ = lastSpawnMinZ;
                        }
                        World spawnWorld = location1.getWorld();
                        for (int x = spawnMinX; x <= spawnMaxX; ++x) {
                            for (int z = spawnMinZ; z <= spawnMaxZ; ++z) {
                                Chunk chunk = spawnWorld.getChunkAt(x >> 4, z >> 4);
                                if (chunk.isLoaded()) continue;
                                chunk.load();
                            }
                        }
                        continue;
                    }

                    // todo alert console of arena chunk error
                }

            }
        }.runTaskLater(Practice.get(), 2L);

        Practice.get().getLogger().info("Finished loading chunks for maps!");

        chunksLoaded = true;
    }

}
