package net.syphlex.practice.manager.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.syphlex.practice.manager.kit.Kit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Arena {
    private final String name;
    private final List<Kit> kits = new ArrayList<>();
    private Location position1, position2,
            corner1, corner2, spectate;

    private boolean open = true;

    public boolean isLocationInsideArena(Location location) {

        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());

        return location.getBlockX() <= maxX
                && location.getBlockY() <= maxY
                && location.getBlockZ() <= maxZ
                && location.getBlockX() >= minX
                && location.getBlockY() >= minY
                && location.getBlockZ() >= minZ;
    }

    public World getWorld(){
        return spectate.getWorld();
    }

    public int getMaxX(){

        if (corner1 == null || corner2 == null) {
            return Integer.MIN_VALUE;
        }

        return Math.max(corner1.getBlockX(), corner2.getBlockX());
    }

    public int getMaxY(){

        if (corner1 == null || corner2 == null) {
            return Integer.MIN_VALUE;
        }

        return Math.max(corner1.getBlockY(), corner2.getBlockY());
    }

    public int getMaxZ(){

        if (corner1 == null || corner2 == null) {
            return Integer.MIN_VALUE;
        }

        return Math.max(corner1.getBlockZ(), corner2.getBlockZ());
    }

    public int getMinX(){

        if (corner1 == null || corner2 == null) {
            return Integer.MAX_VALUE;
        }

        return Math.min(corner1.getBlockX(), corner2.getBlockX());
    }

    public int getMinY(){

        if (corner1 == null || corner2 == null) {
            return Integer.MAX_VALUE;
        }

        return Math.min(corner1.getBlockY(), corner2.getBlockY());
    }

    public int getMinZ(){

        if (corner1 == null || corner2 == null) {
            return Integer.MAX_VALUE;
        }

        return Math.min(corner1.getBlockZ(), corner2.getBlockZ());
    }
}
