package net.syphlex.practice.manager.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.syphlex.practice.manager.kit.Kit;
import org.bukkit.Location;

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

    public int getMinY(){
        return Math.min(corner1.getBlockY(), corner2.getBlockY());
    }
}
